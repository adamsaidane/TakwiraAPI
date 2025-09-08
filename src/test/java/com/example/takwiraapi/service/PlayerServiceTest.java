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
        when(playerRepository.findAll()).thenReturn(List.of(player));

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
        when(playerRepository.existsById(1L)).thenReturn(true);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Optional<Player> found = playerService.getPlayerById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getPlayerName()).isEqualTo("Name");

        verify(playerRepository).existsById(1L);
        verify(playerRepository).findById(1L);
    }

    @Test
    void testGetPlayerById_NotFound() {
        Long playerId = 1L;

        when(playerRepository.existsById(playerId)).thenReturn(false);

        assertThatThrownBy(() -> playerService.getPlayerById(playerId))
                .isInstanceOf(FunctionArgumentException.class)
                .hasMessage(ErrorConstants.PLAYER_NOT_FOUND);

        verify(playerRepository).existsById(playerId);
        verify(playerRepository, never()).findById(anyLong());
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

        when(playerRepository.existsByPlayerName("Name")).thenReturn(true);

        assertThatThrownBy(() -> playerService.createPlayer(player))
                .isInstanceOf(FunctionArgumentException.class)
                .hasMessage(ErrorConstants.PLAYER_NAME_ALREADY_EXISTS);

        verify(playerRepository).existsByPlayerName("Name");
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

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));
        when(playerRepository.save(existingPlayer)).thenReturn(existingPlayer);

        Player updatedPlayer = playerService.updatePlayer(playerId, updatedInfo);

        assertThat(updatedPlayer.getPlayerName()).isEqualTo("New Name");
        verify(playerRepository, times(1)).findById(playerId);
        verify(playerRepository, times(1)).save(existingPlayer);
    }

    @Test
    void testUpdatePlayer_NotFound() {
        Long playerId = 1L;
        Player updated = new Player();
        updated.setPlayerName("Name");

        when(playerRepository.existsByPlayerName("Name")).thenReturn(false);
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.updatePlayer(playerId, updated))
                .isInstanceOf(FunctionArgumentException.class)
                .hasMessage(ErrorConstants.PLAYER_NOT_FOUND);

        verify(playerRepository).findById(playerId);
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void testUpdatePlayer_NameAlreadyExists() {
        Long playerId = 1L;
        Player updated = new Player();
        updated.setPlayerName("Name");

        when(playerRepository.existsByPlayerName("Name")).thenReturn(true);

        assertThatThrownBy(() -> playerService.updatePlayer(playerId, updated))
                .isInstanceOf(FunctionArgumentException.class)
                .hasMessage(ErrorConstants.PLAYER_NAME_ALREADY_EXISTS);

        verify(playerRepository).existsByPlayerName("Name");
        verify(playerRepository, never()).findById(anyLong());
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void testDeletePlayer() {
        Long playerId = 1L;

        when(playerRepository.existsById(playerId)).thenReturn(true);

        playerService.deletePlayer(playerId);

        verify(playerRepository).existsById(playerId);
        verify(playerRepository).deleteById(playerId);
    }

    @Test
    void testDeletePlayer_NotFound() {
        Long playerId = 1L;

        when(playerRepository.existsById(playerId)).thenReturn(false);

        assertThatThrownBy(() -> playerService.deletePlayer(playerId))
                .isInstanceOf(FunctionArgumentException.class)
                .hasMessage(ErrorConstants.PLAYER_NOT_FOUND);

        verify(playerRepository).existsById(playerId);
        verify(playerRepository, never()).deleteById(anyLong());
    }
}
