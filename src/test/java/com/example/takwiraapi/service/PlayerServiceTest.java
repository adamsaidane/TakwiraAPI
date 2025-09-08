package com.example.takwiraapi.service;

import com.example.takwiraapi.entity.Player;
import com.example.takwiraapi.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Optional<Player> found = playerService.getPlayerById(1L);
        assertThat(found).isPresent();
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
    void testUpdatePlayer() {
        Long playerId = 1L;

        Player existingPlayer = new Player();
        existingPlayer.setPlayerId(playerId);
        existingPlayer.setPlayerName("Old Name");

        // Joueur avec nouvelles données
        Player updatedInfo = new Player();
        updatedInfo.setPlayerName("New Name");

        // Stub du repository
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));
        when(playerRepository.save(existingPlayer)).thenReturn(existingPlayer);

        Player updatedPlayer = playerService.updatePlayer(playerId, updatedInfo);

        assertThat(updatedPlayer.getPlayerName()).isEqualTo("New Name");
        verify(playerRepository, times(1)).findById(playerId);
        verify(playerRepository, times(1)).save(existingPlayer);
    }

    @Test
    void testDeletePlayer() {
        Player player = new Player();
        player.setPlayerId(1L);
        player.setPlayerName("Kane");

        doNothing().when(playerRepository).delete(player);

        playerService.deletePlayer(1L);
        verify(playerRepository, times(1)).deleteById(player.getPlayerId());
    }
}
