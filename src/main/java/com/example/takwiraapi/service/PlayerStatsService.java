package com.example.takwiraapi.service;

import com.example.takwiraapi.constants.ErrorConstants;
import com.example.takwiraapi.dto.PlayerStatsDto;
import com.example.takwiraapi.entity.Match;
import com.example.takwiraapi.entity.MatchPlayer;
import com.example.takwiraapi.entity.Player;
import com.example.takwiraapi.entity.Team;
import com.example.takwiraapi.repository.MatchPlayerRepository;
import com.example.takwiraapi.repository.MatchRepository;
import com.example.takwiraapi.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerStatsService {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final MatchPlayerRepository matchPlayerRepository;

    public List<PlayerStatsDto> getAllPlayersStats() {
        List<Player> players = playerRepository.findAllActivePlayers();
        return players.stream()
                .map(this::calculatePlayerStats)
                .toList();
    }

    public PlayerStatsDto getPlayerStats(Long playerId) {
        Player player = playerRepository.findActivePlayerById(playerId)
                .orElseThrow(() -> new RuntimeException(ErrorConstants.PLAYER_NOT_FOUND));
        return calculatePlayerStats(player);
    }

    private PlayerStatsDto calculatePlayerStats(Player player) {
        PlayerStatsDto stats = new PlayerStatsDto(player.getPlayerId(), player.getPlayerName());

        List<MatchPlayer> playerMatches = matchPlayerRepository.getPlayerMatches(player.getPlayerId());

        if (playerMatches.isEmpty()) {
            return stats;
        }

        // Pré-charger toutes les données d'un coup au lieu de boucler sur les matchs
        Map<Long, Match> matchMap = playerMatches.stream()
                .map(MatchPlayer::getMatch)
                .collect(Collectors.toMap(Match::getMatchId, match -> match));

        // Pré-calculer tous les buts par équipe pour tous les matchs d'un coup
        Map<Long, Integer> team1GoalsMap = new java.util.HashMap<>();
        Map<Long, Integer> team2GoalsMap = new java.util.HashMap<>();
        Map<Long, Integer> playerGoalsMap = new java.util.HashMap<>();
        Map<Long, Integer> playerAssistsMap = new java.util.HashMap<>();

        // Pré-calculer tous les scores et statistiques d'un coup
        for (MatchPlayer matchPlayer : playerMatches) {
            Long matchId = matchPlayer.getMatch().getMatchId();
            Match match = matchMap.get(matchId);

            // Calculer les buts par équipe une seule fois par match
            if (!team1GoalsMap.containsKey(matchId)) {
                int team1Goals = (int) match.getMatchGoals().stream()
                        .filter(goal -> Team.TEAM_1.equals(goal.getTeam()))
                        .count();
                int team2Goals = (int) match.getMatchGoals().stream()
                        .filter(goal -> Team.TEAM_2.equals(goal.getTeam()))
                        .count();

                team1GoalsMap.put(matchId, team1Goals);
                team2GoalsMap.put(matchId, team2Goals);
            }

            // Calculer les buts du joueur pour ce match
            int goalsInMatch = (int) match.getMatchGoals().stream()
                    .filter(goal -> goal.getGoalScorer().getPlayerId().equals(player.getPlayerId()))
                    .count();
            playerGoalsMap.put(matchId, goalsInMatch);

            // Calculer les passes du joueur pour ce match
            int assistsInMatch = (int) match.getMatchGoals().stream()
                    .filter(goal -> goal.getGoalAssist() != null &&
                            goal.getGoalAssist().getPlayerId().equals(player.getPlayerId()))
                    .count();
            playerAssistsMap.put(matchId, assistsInMatch);
        }

        // Calculs agrégés en une seule passe
        int goalsScored = playerGoalsMap.values().stream().mapToInt(Integer::intValue).sum();
        int assists = playerAssistsMap.values().stream().mapToInt(Integer::intValue).sum();
        int maxGoalsInMatch = playerGoalsMap.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        int maxAssistsInMatch = playerAssistsMap.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        int hatTricks = (int) playerGoalsMap.values().stream().mapToInt(Integer::intValue).filter(g -> g >= 3).count();
        int assistHatTricks = (int) playerAssistsMap.values().stream().mapToInt(Integer::intValue).filter(a -> a >= 3).count();

        stats.setMatchesPlayed(playerMatches.size());
        stats.setGoalsScored(goalsScored);
        stats.setAssists(assists);
        stats.setMaxGoalsInMatch(maxGoalsInMatch);
        stats.setMaxAssistsInMatch(maxAssistsInMatch);
        stats.setHatTricks(hatTricks);
        stats.setAssistHatTricks(assistHatTricks);
        stats.setTotalGoalContributions(goalsScored + assists);

        // Calcul des victoires/défaites optimisé
        int[] winLoss = calculateWinLossDrawStatsOptimized(playerMatches, team1GoalsMap, team2GoalsMap);
        int matchesWon = winLoss[0];
        int matchesLost = winLoss[1];

        // Calcul des ratios et moyennes
        double winRatio = !playerMatches.isEmpty() ? (double) matchesWon / playerMatches.size() : 0.0;
        double avgGoalsPerMatch = !playerMatches.isEmpty() ? (double) goalsScored / playerMatches.size() : 0.0;
        double avgAssistsPerMatch = !playerMatches.isEmpty() ? (double) assists / playerMatches.size() : 0.0;

        stats.setMatchesWon(matchesWon);
        stats.setMatchesLost(matchesLost);
        stats.setWinRatio(Math.round(winRatio * 100.0) / 100.0);
        stats.setAvgGoalsPerMatch(Math.round(avgGoalsPerMatch * 100.0) / 100.0);
        stats.setAvgAssistsPerMatch(Math.round(avgAssistsPerMatch * 100.0) / 100.0);

        // Victoires consécutives optimisées
        int consecutiveWins = calculateConsecutiveWinsOptimized(playerMatches, team1GoalsMap, team2GoalsMap);

        stats.setConsecutiveWins(consecutiveWins);

        return stats;
    }

    private int[] calculateWinLossDrawStatsOptimized(List<MatchPlayer> playerMatches,
                                                     Map<Long, Integer> team1GoalsMap,
                                                     Map<Long, Integer> team2GoalsMap) {
        int wins = 0;
        int losses = 0;

        for (MatchPlayer matchPlayer : playerMatches) {
            Long matchId = matchPlayer.getMatch().getMatchId();
            boolean isTeam1 = matchPlayer.getTeam() == Team.TEAM_1;

            int team1Goals = team1GoalsMap.get(matchId);
            int team2Goals = team2GoalsMap.get(matchId);

            // Système de score basé sur la différence
            int team1Score = Math.max(team1Goals - team2Goals, 0);
            int team2Score = Math.max(team2Goals - team1Goals, 0);

            if ((isTeam1 && team1Score > team2Score) || (!isTeam1 && team2Score > team1Score)) {
                wins++;
            } else if ((!isTeam1 && team1Score > team2Score) || (isTeam1 && team2Score > team1Score)) {
                losses++;
            }
        }

        return new int[]{wins, losses};
    }

    private int calculateConsecutiveWinsOptimized(List<MatchPlayer> playerMatches,
                                                  Map<Long, Integer> team1GoalsMap,
                                                  Map<Long, Integer> team2GoalsMap) {
        // Trier les matchs par ID (plus récent en premier)
        List<MatchPlayer> sortedMatches = playerMatches.stream()
                .sorted((mp1, mp2) -> mp2.getMatch().getMatchId().compareTo(mp1.getMatch().getMatchId()))
                .toList();

        int consecutive = 0;

        for (MatchPlayer matchPlayer : sortedMatches) {
            Long matchId = matchPlayer.getMatch().getMatchId();
            boolean isTeam1 = matchPlayer.getTeam() == Team.TEAM_1;

            int team1Goals = team1GoalsMap.get(matchId);
            int team2Goals = team2GoalsMap.get(matchId);

            int team1Score = Math.max(team1Goals - team2Goals, 0);
            int team2Score = Math.max(team2Goals - team1Goals, 0);

            boolean hasWon = (isTeam1 && team1Score > team2Score) || (!isTeam1 && team2Score > team1Score);

            if (hasWon) {
                consecutive++;
            } else {
                break;
            }
        }

        return consecutive;
    }
}