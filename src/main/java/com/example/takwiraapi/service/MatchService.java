package com.example.takwiraapi.service;

import com.example.takwiraapi.constants.ErrorConstants;
import com.example.takwiraapi.dto.AddGoalsToMatchDto;
import com.example.takwiraapi.dto.AddPlayersToMatchDto;
import com.example.takwiraapi.dto.CreateMatchDto;
import com.example.takwiraapi.dto.MatchDto;
import com.example.takwiraapi.entity.*;
import com.example.takwiraapi.mapper.Mapper;
import com.example.takwiraapi.repository.MatchRepository;
import com.example.takwiraapi.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    private final Mapper mapper;

    public List<MatchDto> getAllMatches() {
        return matchRepository.findAllActiveMatches()
                .stream()
                .map(mapper::matchToDto)
                .toList();
    }

    public MatchDto getMatchById(Long matchId) {
        Match match = matchRepository.findActiveMatchesByMatchId(matchId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.MATCH_NOT_FOUND));
        return mapper.matchToDto(match);
    }

    public MatchDto createMatch(CreateMatchDto createMatchDto) {
        Match match = new Match();
        match.setMatchName(createMatchDto.getMatchName());
        match.setMatchId(null);
        match.setCreatedAt(LocalDateTime.now());
        match.setDeleted(false);
        match.setDeletedAt(null);
        Match savedMatch = matchRepository.save(match);
        return mapper.matchToDto(savedMatch);
    }

    @CacheEvict(value = {"playerStats", "allPlayerStats"}, allEntries = true)
    public MatchDto addPlayers(Long matchId, AddPlayersToMatchDto addPlayersToMatchDto) {
        Match match = matchRepository.findActiveMatchesByMatchId(matchId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.MATCH_NOT_FOUND));

        List<Player> team1 = playerRepository.findAllById(addPlayersToMatchDto.getTeam1PlayerIds());
        List<Player> team2 = playerRepository.findAllById(addPlayersToMatchDto.getTeam2PlayerIds());

        //Check for inexistant players
        Set<Long> foundIds = Stream.concat(team1.stream(), team2.stream())
                .map(Player::getPlayerId)
                .collect(Collectors.toSet());

        Set<Long> requestedIds = new HashSet<>();
        requestedIds.addAll(addPlayersToMatchDto.getTeam1PlayerIds());
        requestedIds.addAll(addPlayersToMatchDto.getTeam2PlayerIds());

        requestedIds.removeAll(foundIds);
        if (!requestedIds.isEmpty()) {
            throw new FunctionArgumentException(ErrorConstants.PLAYER_NOT_FOUND);
        }

        //Check for duplicates
        Set<Long> team1Ids = team1.stream().map(Player::getPlayerId).collect(Collectors.toSet());
        Set<Long> team2Ids = team2.stream().map(Player::getPlayerId).collect(Collectors.toSet());

        Set<Long> intersection = new HashSet<>(team1Ids);
        intersection.retainAll(team2Ids);
        if (!intersection.isEmpty()) {
            throw new FunctionArgumentException(ErrorConstants.PLAYER_IN_BOTH_TEAMS);
        }

        //Check for deleted players
        Stream.concat(team1.stream(), team2.stream()).forEach(player -> {
            if (player.isDeleted()) {
                throw new FunctionArgumentException(ErrorConstants.PLAYER_NOT_FOUND);
            }
        });

        // Updating players
        List<MatchPlayer> existingPlayers = new ArrayList<>(match.getMatchPlayers());

        // Removing players no longer in match
        existingPlayers.forEach(mp -> {
            Long playerId = mp.getPlayer().getPlayerId();
            if (!team1Ids.contains(playerId) && !team2Ids.contains(playerId)) {
                match.getMatchPlayers().remove(mp);
            }
        });

        // Create or update teams
        Stream.concat(team1.stream(), team2.stream()).forEach(player -> {
            Optional<MatchPlayer> existing = match.getMatchPlayers().stream()
                    .filter(mp -> mp.getPlayer().getPlayerId().equals(player.getPlayerId()))
                    .findFirst();

            if (existing.isPresent()) {
                // Update player
                existing.get().setTeam(team1.contains(player) ? Team.TEAM_1 : Team.TEAM_2);
            } else {
                // New player
                MatchPlayer mp = new MatchPlayer();
                mp.setMatch(match);
                mp.setPlayer(player);
                mp.setTeam(team1.contains(player) ? Team.TEAM_1 : Team.TEAM_2);
                match.getMatchPlayers().add(mp);
            }
        });

        return mapper.matchToDto(matchRepository.save(match));
    }

    @CacheEvict(value = {"playerStats", "allPlayerStats"}, allEntries = true)
    public MatchDto addGoals(Long matchId, AddGoalsToMatchDto addGoalsToMatchDto) {
        Match match = matchRepository.findActiveMatchesByMatchId(matchId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.MATCH_NOT_FOUND));

        List<Goal> goals = addGoalsToMatchDto.getGoals().stream()
                .map(goalDto -> {
                    Goal goal = new Goal();
                    Player scorer = playerRepository.findById(goalDto.getGoalScorer().getPlayerId())
                            .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.PLAYER_NOT_FOUND));
                    Player assist;
                    if (goalDto.getGoalAssist() != null) {
                        assist = playerRepository.findById(goalDto.getGoalAssist().getPlayerId())
                                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.PLAYER_NOT_FOUND));

                        if (scorer.getPlayerId().equals(assist.getPlayerId())) {
                            throw new FunctionArgumentException(ErrorConstants.SCORER_AND_ASSIST_PLAYER_ARE_THE_SAME);
                        }
                    } else {
                        assist = null;
                    }

                    boolean scorerInMatch = match.getMatchPlayers().stream()
                            .anyMatch(pm -> pm.getPlayer().getPlayerId().equals(scorer.getPlayerId())
                                    && pm.getTeam().equals(goalDto.getTeam()));

                    if (!scorerInMatch) {
                        throw new FunctionArgumentException(ErrorConstants.SCORER_NOT_FOUND_IN_MATCH);
                    }

                    if (assist != null) {
                        boolean assistInMatch = match.getMatchPlayers().stream()
                                .anyMatch(pm -> pm.getPlayer().getPlayerId().equals(assist.getPlayerId())
                                        && pm.getTeam().equals(goalDto.getTeam()));
                        if (!assistInMatch) {
                            throw new FunctionArgumentException(ErrorConstants.ASSIST_PLAYER_NOT_FOUND_IN_MATCH);
                        }
                    }

                    goal.setGoalScorer(scorer);
                    goal.setGoalAssist(assist);
                    goal.setMatch(match);
                    goal.setTeam(goalDto.getTeam());
                    return goal;
                }).toList();

        match.getMatchGoals().addAll(goals);

        return mapper.matchToDto(matchRepository.save(match));
    }

    @CacheEvict(value = {"playerStats", "allPlayerStats"}, allEntries = true)
    public void deleteMatch(Long matchId) {
        Match match = matchRepository.findActiveMatchesByMatchId(matchId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.MATCH_NOT_FOUND));

        match.markDeleted();

        if (match.getMatchGoals() != null) {
            match.getMatchGoals().forEach(Goal::markDeleted);
        }

        if (match.getMatchPlayers() != null) {
            match.getMatchPlayers().forEach(MatchPlayer::markDeleted);
        }
        matchRepository.save(match);
    }

    public MatchDto updateMatch(Long matchId, CreateMatchDto createMatchDto) {
        Match match = matchRepository.findActiveMatchesByMatchId(matchId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.MATCH_NOT_FOUND));

        match.setMatchName(createMatchDto.getMatchName());
        match.setUpdatedAt(LocalDateTime.now());

        Match savedMatch = matchRepository.save(match);
        return mapper.matchToDto(savedMatch);
    }

    @CacheEvict(value = {"playerStats", "allPlayerStats"}, allEntries = true)
    public MatchDto deleteGoal(Long matchId, Long goalId) {
        Match match = matchRepository.findActiveMatchesByMatchId(matchId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.MATCH_NOT_FOUND));

        Goal goalToDelete = match.getMatchGoals().stream()
                .filter(goal -> goal.getGoalId().equals(goalId) && !goal.isDeleted())
                .findFirst()
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.GOAL_NOT_FOUND_IN_MATCH));

        match.getMatchGoals().remove(goalToDelete);
        matchRepository.save(match);

        return mapper.matchToDto(match);
    }
}
