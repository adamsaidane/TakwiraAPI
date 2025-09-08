package com.example.takwiraapi.service;

import com.example.takwiraapi.entity.Player;
import com.example.takwiraapi.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository repository;

    public List<Player> getAllPlayers() {
        return repository.findAll();
    }

    public Optional<Player> getPlayerById(Long id) {
        return repository.findById(id);
    }

    public Player createPlayer(Player player) {
        if (player.getPlayerId() != null && repository.existsById(player.getPlayerId())) {
            throw new IllegalArgumentException("Player with ID " + player.getPlayerId() + " already exists.");
        }
        player.setPlayerId(null);
        return repository.save(player);
    }

    public Player updatePlayer(Long id, Player updatedPlayer) {
        return repository.findById(id)
                .map(player -> {
                    player.setPlayerName(updatedPlayer.getPlayerName());
                    return repository.save(player);
                })
                .orElseThrow(() -> new RuntimeException("Player not found with id " + id));
    }

    public void deletePlayer(Long id) {
        repository.deleteById(id);
    }
}
