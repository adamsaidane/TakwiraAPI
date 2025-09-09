package com.example.takwiraapi.controller;

import com.example.takwiraapi.dto.MatchDto;
import com.example.takwiraapi.entity.Match;
import com.example.takwiraapi.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // GET all matches
    @GetMapping
    public ResponseEntity<List<MatchDto>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    // GET match by id
    @GetMapping("/{matchId}")
    public ResponseEntity<Optional<MatchDto>> getMatchById(@PathVariable Long matchId) {
        return ResponseEntity.ok(Optional.ofNullable(matchService.getMatchById(matchId)));
    }

    // CREATE new match
    @PostMapping
    public ResponseEntity<MatchDto> createMatch(@RequestBody Match match) {
        return ResponseEntity.ok(matchService.createMatch(match));
    }

    // UPDATE match
    @PutMapping("/{matchId}")
    public ResponseEntity<MatchDto> updateMatch(@PathVariable Long matchId, @RequestBody Match match) {
        return ResponseEntity.ok(matchService.updateMatch(matchId, match));
    }

    // DELETE match
    @DeleteMapping("/{matchId}")
    public ResponseEntity<String> deleteMatch(@PathVariable Long matchId) {
        matchService.deleteMatch(matchId);
        return ResponseEntity.ok("Match with ID " + matchId + " deleted successfully");
    }
}
