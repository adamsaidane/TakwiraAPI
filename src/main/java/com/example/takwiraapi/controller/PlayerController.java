package com.example.takwiraapi.controller;

import com.example.takwiraapi.entity.Player;
import com.example.takwiraapi.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService service;

    // GET all players
    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(service.getAllPlayers());
    }

    // GET player by id
    @GetMapping("/{playerId}")
    public ResponseEntity<Optional<Player>> getPlayerById(@PathVariable Long playerId) {
        return ResponseEntity.ok(service.getPlayerById(playerId));
    }

    // CREATE new player
    @PostMapping("/create")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        return ResponseEntity.ok(service.createPlayer(player));
    }

    // UPDATE player
    @PutMapping("/create/{playerId}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long playerId, @RequestBody Player player) {
        return ResponseEntity.ok(service.updatePlayer(playerId, player));
    }

    // DELETE player
    @DeleteMapping("/delete/{playerId}")
    public ResponseEntity<String> deletePlayer(@PathVariable Long playerId) {
        service.deletePlayer(playerId);
        return ResponseEntity.ok("Player with ID " + playerId + " deleted successfully");
    }
}
