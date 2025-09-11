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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public MatchDto addPlayers(Long matchId, AddPlayersToMatchDto addPlayersToMatchDto) {
        Match match = matchRepository.findActiveMatchesByMatchId(matchId)
                .orElseThrow(() -> new FunctionArgumentException("Match not found"));

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

        team1.forEach(player -> {
            MatchPlayer pm = new MatchPlayer();
            pm.setMatch(match);
            pm.setPlayer(player);
            pm.setTeam(Team.TEAM_1);
            match.getMatchPlayers().add(pm);
        });

        team2.forEach(player -> {
            MatchPlayer pm = new MatchPlayer();
            pm.setMatch(match);
            pm.setPlayer(player);
            pm.setTeam(Team.TEAM_2);
            match.getMatchPlayers().add(pm);
        });

        return mapper.matchToDto(matchRepository.save(match));
    }

    public MatchDto addGoals(Long matchId, AddGoalsToMatchDto addGoalsToMatchDto) {
        Match match = matchRepository.findActiveMatchesByMatchId(matchId)
                .orElseThrow(() -> new FunctionArgumentException("Match not found"));

        List<Goal> goals = addGoalsToMatchDto.getGoals().stream()
                .map(goalDto -> {
                    Goal goal = new Goal();
                    Player scorer = playerRepository.findById(goalDto.getGoalScorer().getPlayerId())
                            .orElseThrow(() -> new FunctionArgumentException("Player not found"));
                    Player assist = playerRepository.findById(goalDto.getGoalAssist().getPlayerId())
                            .orElseThrow(() -> new FunctionArgumentException("Player not found"));

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
}
