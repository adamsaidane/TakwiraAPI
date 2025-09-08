package com.example.takwiraapi.controller;

import com.example.takwiraapi.entity.Player;
import com.example.takwiraapi.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService service;

    // GET all players
    @GetMapping
    public List<Player> getAllPlayers() {
        return service.getAllPlayers();
    }

    // GET player by id
    @GetMapping("/{playerId}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long playerId) {
        return service.getPlayerById(playerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE new player
    @PostMapping
    public Player createPlayer(@RequestBody Player player) {
        return service.createPlayer(player);
    }

    // UPDATE player
    @PutMapping("/{playerId}")
    public Player updatePlayer(@PathVariable Long playerId, @RequestBody Player player) {
        return service.updatePlayer(playerId, player);
    }

    // DELETE player
    @DeleteMapping("/{playerId}")
    public ResponseEntity<String> deletePlayer(@PathVariable Long playerId) {
        service.deletePlayer(playerId);
        return ResponseEntity.ok("Player with ID " + playerId + " deleted successfully");
    }
}
