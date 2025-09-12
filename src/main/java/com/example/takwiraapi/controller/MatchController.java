package com.example.takwiraapi.controller;

import com.example.takwiraapi.dto.AddGoalsToMatchDto;
import com.example.takwiraapi.dto.AddPlayersToMatchDto;
import com.example.takwiraapi.dto.CreateMatchDto;
import com.example.takwiraapi.dto.MatchDto;
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
    @PostMapping("/create")
    public ResponseEntity<MatchDto> createMatch(@RequestBody CreateMatchDto createMatchDto) {
        return ResponseEntity.ok(matchService.createMatch(createMatchDto));
    }

    @PostMapping("/create/{matchId}/players")
    public ResponseEntity<MatchDto> addPlayers(@PathVariable Long matchId, @RequestBody AddPlayersToMatchDto addPlayersToMatchDto) {
        return ResponseEntity.ok(matchService.addPlayers(matchId, addPlayersToMatchDto));
    }

    @PostMapping("/create/{matchId}/goals")
    public ResponseEntity<MatchDto> addGoals(@PathVariable Long matchId, @RequestBody AddGoalsToMatchDto addGoalsToMatchDto) {
        return ResponseEntity.ok(matchService.addGoals(matchId, addGoalsToMatchDto));
    }

    // DELETE match
    @DeleteMapping("delete/{matchId}")
    public ResponseEntity<String> deleteMatch(@PathVariable Long matchId) {
        matchService.deleteMatch(matchId);
        return ResponseEntity.ok("Match with ID " + matchId + " deleted successfully");
    }
}
