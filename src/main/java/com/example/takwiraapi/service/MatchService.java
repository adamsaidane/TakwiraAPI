package com.example.takwiraapi.service;

import com.example.takwiraapi.constants.ErrorConstants;
import com.example.takwiraapi.dto.AddGoalsToMatchDto;
import com.example.takwiraapi.dto.AddPlayersToMatchDto;
import com.example.takwiraapi.dto.CreateMatchDto;
import com.example.takwiraapi.dto.MatchDto;
import com.example.takwiraapi.entity.Goal;
import com.example.takwiraapi.entity.Match;
import com.example.takwiraapi.entity.Player;
import com.example.takwiraapi.mapper.Mapper;
import com.example.takwiraapi.repository.MatchRepository;
import com.example.takwiraapi.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

        match.setTeam1Players(team1);
        match.setTeam2Players(team2);

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
                    goal.setGoalScorer(scorer);
                    goal.setGoalAssist(assist);
                    goal.setMatch(match);
                    goal.setTeam(goalDto.getTeam());
                    return goal;
                }).toList();

        match.getMatchGoals().addAll(goals);

        return mapper.matchToDto(matchRepository.save(match));
    }


    //à revoir
    public MatchDto updateMatch(Long matchId, Match updatedMatch) {
        Match match = matchRepository.findActiveMatchesByMatchId(matchId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.MATCH_NOT_FOUND));

        match.setMatchName(updatedMatch.getMatchName());
        match.setTeam1Players(updatedMatch.getTeam1Players());
        match.setTeam2Players(updatedMatch.getTeam2Players());
        match.setDeleted(false);
        match.setDeletedAt(null);

        Match savedMatch = matchRepository.save(match);
        return mapper.matchToDto(savedMatch);
    }

    public void deleteMatch(Long matchId) {
        Match match = matchRepository.findActiveMatchesByMatchId(matchId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.MATCH_NOT_FOUND));

        match.markDeleted();
        matchRepository.save(match);
    }
}
