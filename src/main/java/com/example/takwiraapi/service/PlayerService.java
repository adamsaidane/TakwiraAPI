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

    private final PlayerRepository playerRepository;

    public List<Player> getAllPlayers() {
        return playerRepository.findAllActivePlayers();
    }

    public Optional<Player> getPlayerById(Long playerId) {
        return Optional.ofNullable(playerRepository.findActivePlayerById(playerId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.PLAYER_NOT_FOUND)));
    }

    public Player createPlayer(Player player) throws FunctionArgumentException {
        if (playerRepository.existsByPlayerNameAndDeletedIsFalse(player.getPlayerName())) {
            throw new FunctionArgumentException(ErrorConstants.PLAYER_NAME_ALREADY_EXISTS);
        }
        player.setPlayerId(null);
        player.setDeleted(false);
        player.setDeletedAt(null);
        return playerRepository.save(player);
    }

    public Player updatePlayer(Long playerId, Player updatedPlayer) {
        if (playerRepository.existsByPlayerNameAndDeletedIsFalse(updatedPlayer.getPlayerName())) {
            throw new FunctionArgumentException(ErrorConstants.PLAYER_NAME_ALREADY_EXISTS);
        }
        Player player = playerRepository.findActivePlayerById(playerId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.PLAYER_NOT_FOUND));

        player.setPlayerName(updatedPlayer.getPlayerName());
        player.setDeleted(false);
        player.setDeletedAt(null);
        return playerRepository.save(player);
    }

    public void deletePlayer(Long playerId) {
        Player player = playerRepository.findActivePlayerById(playerId)
                .orElseThrow(() -> new FunctionArgumentException(ErrorConstants.PLAYER_NOT_FOUND));

        player.markDeleted();
        playerRepository.save(player);
    }
}
