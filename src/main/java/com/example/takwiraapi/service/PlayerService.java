package com.example.takwiraapi.service;

import com.example.takwiraapi.constants.ErrorConstants;
import com.example.takwiraapi.entity.Player;
import com.example.takwiraapi.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
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

    public Optional<Player> getPlayerById(Long playerId) {
        if (!repository.existsById(playerId)) {
            throw new FunctionArgumentException(ErrorConstants.PLAYER_NOT_FOUND);
        }
        return repository.findById(playerId);
    }

    public Player createPlayer(Player player) throws FunctionArgumentException {
        if (repository.existsByPlayerName(player.getPlayerName())) {
            throw new FunctionArgumentException(ErrorConstants.PLAYER_NAME_ALREADY_EXISTS);
        }
        player.setPlayerId(null);
        return repository.save(player);
    }

    public Player updatePlayer(Long playerId, Player updatedPlayer) {
        if (repository.existsByPlayerName(updatedPlayer.getPlayerName())) {
            throw new FunctionArgumentException(ErrorConstants.PLAYER_NAME_ALREADY_EXISTS);
        }
        return repository.findById(playerId)
                .map(player -> {
                    player.setPlayerName(updatedPlayer.getPlayerName());
                    return repository.save(player);
                })
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.PLAYER_NOT_FOUND));
    }

    public void deletePlayer(Long playerId) {
        if (!repository.existsById(playerId)) {
            throw new FunctionArgumentException(ErrorConstants.PLAYER_NOT_FOUND);
        }
        repository.deleteById(playerId);
    }
}
