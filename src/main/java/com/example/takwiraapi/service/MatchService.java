package com.example.takwiraapi.service;

import com.example.takwiraapi.constants.ErrorConstants;
import com.example.takwiraapi.entity.Match;
import com.example.takwiraapi.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

    public List<Match> getAllMatches() {
        return matchRepository.findAllActiveMatches();
    }

    public Optional<Match> getMatchById(Long matchId) {
        return Optional.ofNullable(matchRepository.findActiveMatchesByMatchId(matchId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.MATCH_NOT_FOUND)));
    }

    public Match createMatch(Match match) throws FunctionArgumentException {
        match.setMatchId(null);
        match.setDeleted(false);
        match.setDeletedAt(null);
        return matchRepository.save(match);
    }

    public Match updateMatch(Long matchId, Match updatedMatch) {
        Match match = matchRepository.findActiveMatchesByMatchId(matchId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.MATCH_NOT_FOUND));

        match.setMatchName(updatedMatch.getMatchName());
        match.setTeam1Players(updatedMatch.getTeam1Players());
        match.setTeam2Players(updatedMatch.getTeam2Players());
        match.setDeleted(false);
        match.setDeletedAt(null);
        return matchRepository.save(match);
    }

    public void deleteMatch(Long matchId) {
        Match match = matchRepository.findActiveMatchesByMatchId(matchId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.MATCH_NOT_FOUND));

        match.markDeleted();
        matchRepository.save(match);
    }
}
