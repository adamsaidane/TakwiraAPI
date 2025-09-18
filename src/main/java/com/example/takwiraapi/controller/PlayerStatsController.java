package com.example.takwiraapi.controller;

import com.example.takwiraapi.dto.PlayerStatsDto;
import com.example.takwiraapi.service.PlayerStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class PlayerStatsController {

    private final PlayerStatsService playerStatsService;

    @GetMapping("/players")
    public ResponseEntity<List<PlayerStatsDto>> getAllPlayersStats() {
        List<PlayerStatsDto> stats = playerStatsService.getAllPlayersStats();
        return ResponseEntity.ok(stats);

    }

    @GetMapping("/players/{playerId}")
    public ResponseEntity<PlayerStatsDto> getPlayerStats(@PathVariable Long playerId) {
        PlayerStatsDto stats = playerStatsService.getPlayerStats(playerId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/top-scorers")
    public ResponseEntity<List<PlayerStatsDto>> getTopScorers(@RequestParam(defaultValue = "10") int limit) {
        List<PlayerStatsDto> allStats = playerStatsService.getAllPlayersStats();
        List<PlayerStatsDto> topScorers = allStats.stream()
                .filter(stat -> stat.getGoalsScored() > 0)
                .sorted((s1, s2) -> s2.getGoalsScored().compareTo(s1.getGoalsScored()))
                .limit(limit)
                .toList();
        return ResponseEntity.ok(topScorers);
    }

    @GetMapping("/top-assisters")
    public ResponseEntity<List<PlayerStatsDto>> getTopAssisters(@RequestParam(defaultValue = "10") int limit) {
        List<PlayerStatsDto> allStats = playerStatsService.getAllPlayersStats();
        List<PlayerStatsDto> topAssisters = allStats.stream()
                .filter(stat -> stat.getAssists() > 0)
                .sorted((s1, s2) -> s2.getAssists().compareTo(s1.getAssists()))
                .limit(limit)
                .toList();
        return ResponseEntity.ok(topAssisters);
    }

    @GetMapping("/best-win-ratio")
    public ResponseEntity<List<PlayerStatsDto>> getBestWinRatio(@RequestParam(defaultValue = "10") int limit,
                                                                @RequestParam(defaultValue = "3") int minMatches) {

        List<PlayerStatsDto> allStats = playerStatsService.getAllPlayersStats();
        List<PlayerStatsDto> bestWinRatio = allStats.stream()
                .filter(stat -> stat.getMatchesPlayed() >= minMatches)
                .sorted((s1, s2) -> s2.getWinRatio().compareTo(s1.getWinRatio()))
                .limit(limit)
                .toList();
        return ResponseEntity.ok(bestWinRatio);
    }

    @GetMapping("/most-active")
    public ResponseEntity<List<PlayerStatsDto>> getMostActivePlayer(@RequestParam(defaultValue = "10") int limit) {
        List<PlayerStatsDto> allStats = playerStatsService.getAllPlayersStats();
        List<PlayerStatsDto> mostActive = allStats.stream()
                .filter(stat -> stat.getMatchesPlayed() > 0)
                .sorted((s1, s2) -> s2.getMatchesPlayed().compareTo(s1.getMatchesPlayed()))
                .limit(limit)
                .toList();
        return ResponseEntity.ok(mostActive);
    }
}
