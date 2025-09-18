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

        List<Match> matches = playerMatches.stream()
                .map(MatchPlayer::getMatch)
                .toList();

        if (playerMatches.isEmpty()) {
            return stats;
        }

        stats.setMatchesPlayed(playerMatches.size());

        // Calculs des buts et passes
        int goalsScored = 0;
        int assists = 0;
        int maxGoalsInMatch = 0;
        int maxAssistsInMatch = 0;
        int hatTricks = 0;
        int assistHatTricks = 0;

        for (Match match : matches) {
            // Compter les buts dans ce match
            int goalsInThisMatch = (int) match.getMatchGoals().stream()
                    .filter(goal -> goal.getGoalScorer().getPlayerId().equals(player.getPlayerId()))
                    .count();

            if (goalsInThisMatch >= 3) hatTricks++;

            goalsScored += goalsInThisMatch;
            maxGoalsInMatch = Math.max(maxGoalsInMatch, goalsInThisMatch);

            // Compter les passes décisives
            int assistsInThisMatch = (int) match.getMatchGoals().stream()
                    .filter(goal -> goal.getGoalAssist() != null &&
                            goal.getGoalAssist().getPlayerId().equals(player.getPlayerId()))
                    .count();

            if (assistsInThisMatch >= 3) assistHatTricks++;

            assists += assistsInThisMatch;
            maxAssistsInMatch = Math.max(maxAssistsInMatch, assistsInThisMatch);
        }

        // Calcul des victoires, défaites
        int[] winLoss = calculateWinLossDrawStats(playerMatches);
        int matchesWon = winLoss[0];
        int matchesLost = winLoss[1];

        // Calcul des ratios et moyennes
        double winRatio = !playerMatches.isEmpty() ? (double) matchesWon / playerMatches.size() : 0.0;
        double avgGoalsPerMatch = !playerMatches.isEmpty() ? (double) goalsScored / playerMatches.size() : 0.0;
        double avgAssistsPerMatch = !playerMatches.isEmpty() ? (double) assists / playerMatches.size() : 0.0;

        // Victoires consécutives actuelles
        int consecutiveWins = calculateConsecutiveWins(player.getPlayerId(), playerMatches);

        // Assignation des valeurs
        stats.setGoalsScored(goalsScored);
        stats.setAssists(assists);
        stats.setMatchesWon(matchesWon);
        stats.setMatchesLost(matchesLost);
        stats.setWinRatio(Math.round(winRatio * 100.0) / 100.0);
        stats.setAvgGoalsPerMatch(Math.round(avgGoalsPerMatch * 100.0) / 100.0);
        stats.setAvgAssistsPerMatch(Math.round(avgAssistsPerMatch * 100.0) / 100.0);
        stats.setTotalGoalContributions(goalsScored + assists);
        stats.setConsecutiveWins(consecutiveWins);
        stats.setMaxGoalsInMatch(maxGoalsInMatch);
        stats.setMaxAssistsInMatch(maxAssistsInMatch);
        stats.setHatTricks(hatTricks);
        stats.setAssistHatTricks(assistHatTricks);

        return stats;
    }

    private int[] calculateWinLossDrawStats(List<MatchPlayer> playerMatches) {
        int wins = 0;
        int losses = 0;

        for (MatchPlayer matchPlayer : playerMatches) {
            boolean isTeam1 = matchPlayer.getTeam() == Team.TEAM_1;

            Match match = matchPlayer.getMatch();

            int team1Goals = (int) match.getMatchGoals().stream()
                    .filter(goal -> Team.TEAM_1.equals(goal.getTeam()))
                    .count();

            int team2Goals = (int) match.getMatchGoals().stream()
                    .filter(goal -> Team.TEAM_2.equals(goal.getTeam()))
                    .count();

            // Système de score basé sur la différence
            int team1Score = Math.max(team1Goals - team2Goals, 0);
            int team2Score = Math.max(team2Goals - team1Goals, 0);

             if ((isTeam1 && team1Score > team2Score) || (!isTeam1 && team2Score > team1Score)) {
                wins++;
            } else if  ((!isTeam1 && team1Score > team2Score) || (isTeam1 && team2Score > team1Score)){
                losses++;
            }
        }

        return new int[]{wins, losses};
    }

    private int calculateConsecutiveWins(Long playerId, List<MatchPlayer> playerMatches) {
        // Trier les matchs par ID (plus récent en premier)
        List<Match> sortedMatches = playerMatches.stream()
                .map(MatchPlayer::getMatch)
                .sorted((m1, m2) -> m2.getMatchId().compareTo(m1.getMatchId()))
                .toList();

        int consecutive = 0;

        for (Match match : sortedMatches) {
            boolean isTeam1 = match.getMatchPlayers().stream()
                    .filter(mp -> mp.getTeam() == Team.TEAM_1)
                    .anyMatch(p -> p.getPlayer().getPlayerId().equals(playerId));

            int team1Goals = (int) match.getMatchGoals().stream()
                    .filter(goal -> Team.TEAM_1.equals(goal.getTeam()))
                    .count();

            int team2Goals = (int) match.getMatchGoals().stream()
                    .filter(goal -> Team.TEAM_2.equals(goal.getTeam()))
                    .count();

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
