package com.example.takwiraapi.service;

import com.example.takwiraapi.constants.ErrorConstants;
import com.example.takwiraapi.dto.MatchDto;
import com.example.takwiraapi.entity.Match;
import com.example.takwiraapi.mapper.Mapper;
import com.example.takwiraapi.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final Mapper mapper; // ton Mapper pour Match <-> MatchDto

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

    public MatchDto createMatch(Match match) {
        match.setMatchId(null);
        match.setDeleted(false);
        match.setDeletedAt(null);
        Match savedMatch = matchRepository.save(match);
        return mapper.matchToDto(savedMatch);
    }

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
