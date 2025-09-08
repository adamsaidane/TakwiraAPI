package com.example.takwiraapi.service;

import com.example.takwiraapi.constants.ErrorConstants;
import com.example.takwiraapi.entity.Player;
import com.example.takwiraapi.repository.PlayerRepository;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    public PlayerServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPlayers() {
        Player player = new Player();
        player.setPlayerId(1L);
        player.setPlayerName("Name");
        when(playerRepository.findAllActivePlayers()).thenReturn(List.of(player));

        List<Player> players = playerService.getAllPlayers();
        assertThat(players).hasSize(1);
        assertThat(players.get(0).getPlayerName()).isEqualTo("Name");
    }

    @Test
    void testGetPlayerById() {
        Player player = new Player();
        player.setPlayerId(1L);
        player.setPlayerName("Name");

        // Mock du existsById + findById
        when(playerRepository.findActivePlayerById(1L)).thenReturn(Optional.of(player));

        Optional<Player> found = playerService.getPlayerById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getPlayerName()).isEqualTo("Name");

        verify(playerRepository).findActivePlayerById(1L);

    }

    @Test
    void testGetPlayerById_NotFound() {
        when(playerRepository.findActivePlayerById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.getPlayerById(1L))
                .isInstanceOf(FunctionArgumentException.class)
                .hasMessage(ErrorConstants.PLAYER_NOT_FOUND);

        verify(playerRepository).findActivePlayerById(1L);
    }

    @Test
    void testCreatePlayer() {
        Player player = new Player();
        player.setPlayerName("Name");

        when(playerRepository.save(player)).thenReturn(player);

        Player created = playerService.createPlayer(player);
        assertThat(created.getPlayerName()).isEqualTo("Name");
        verify(playerRepository, times(1)).save(player);
    }

    @Test
    void testCreatePlayer_NameAlreadyExists() {
        Player player = new Player();
        player.setPlayerName("Name");

        when(playerRepository.existsByPlayerNameAndDeletedIsFalse("Name")).thenReturn(true);

        assertThatThrownBy(() -> playerService.createPlayer(player))
                .isInstanceOf(FunctionArgumentException.class)
                .hasMessage(ErrorConstants.PLAYER_NAME_ALREADY_EXISTS);

        verify(playerRepository).existsByPlayerNameAndDeletedIsFalse("Name");
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void testUpdatePlayer() {
        Long playerId = 1L;

        Player existingPlayer = new Player();
        existingPlayer.setPlayerId(playerId);
        existingPlayer.setPlayerName("Old Name");

        Player updatedInfo = new Player();
        updatedInfo.setPlayerName("New Name");

        when(playerRepository.findActivePlayerById(playerId)).thenReturn(Optional.of(existingPlayer));
        when(playerRepository.save(existingPlayer)).thenReturn(existingPlayer);

        Player updatedPlayer = playerService.updatePlayer(playerId, updatedInfo);

        assertThat(updatedPlayer.getPlayerName()).isEqualTo("New Name");
        verify(playerRepository, times(1)).findActivePlayerById(playerId);
        verify(playerRepository, times(1)).save(existingPlayer);
    }

    @Test
    void testUpdatePlayer_NotFound() {
        Long playerId = 1L;
        Player updated = new Player();
        updated.setPlayerName("Name");

        when(playerRepository.existsByPlayerNameAndDeletedIsFalse("Name")).thenReturn(false);
        when(playerRepository.findActivePlayerById(playerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.updatePlayer(playerId, updated))
                .isInstanceOf(FunctionArgumentException.class)
                .hasMessage(ErrorConstants.PLAYER_NOT_FOUND);

        verify(playerRepository).findActivePlayerById(playerId);
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void testUpdatePlayer_NameAlreadyExists() {
        Long playerId = 1L;
        Player updated = new Player();
        updated.setPlayerName("Name");

        when(playerRepository.existsByPlayerNameAndDeletedIsFalse("Name")).thenReturn(true);

        assertThatThrownBy(() -> playerService.updatePlayer(playerId, updated))
                .isInstanceOf(FunctionArgumentException.class)
                .hasMessage(ErrorConstants.PLAYER_NAME_ALREADY_EXISTS);

        verify(playerRepository).existsByPlayerNameAndDeletedIsFalse("Name");
        verify(playerRepository, never()).findActivePlayerById(anyLong());
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void testDeletePlayer() {
        Long playerId = 1L;

        Player player = new Player();
        player.setPlayerId(playerId);
        player.setPlayerName("Name");
        player.setDeleted(false);

        when(playerRepository.findActivePlayerById(playerId)).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        playerService.deletePlayer(playerId);

        assertThat(player.isDeleted()).isTrue();
        verify(playerRepository).findActivePlayerById(playerId);
        verify(playerRepository).save(player);
    }

    @Test
    void testDeletePlayer_NotFound() {
        Long playerId = 1L;

        when(playerRepository.findActivePlayerById(playerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.deletePlayer(playerId))
                .isInstanceOf(FunctionArgumentException.class)
                .hasMessage(ErrorConstants.PLAYER_NOT_FOUND);

        verify(playerRepository).findActivePlayerById(playerId);
        verify(playerRepository, never()).save(any(Player.class));
    }
}
