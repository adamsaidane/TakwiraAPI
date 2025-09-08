package com.example.takwiraapi.controller;

import com.example.takwiraapi.entity.Player;
import com.example.takwiraapi.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlayerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(playerController).build();
    }

    @Test
    void testGetAllPlayers() throws Exception {
        Player player1 = new Player(1L, "Kane");
        Player player2 = new Player(2L, "Messi");

        when(playerService.getAllPlayers()).thenReturn(Arrays.asList(player1, player2));

        mockMvc.perform(get("/api/players")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].playerName").value("Kane"))
                .andExpect(jsonPath("$[1].playerName").value("Messi"));

        verify(playerService, times(1)).getAllPlayers();
    }

    @Test
    void testGetPlayerById() throws Exception {
        Player player = new Player(1L, "Kane");

        when(playerService.getPlayerById(1L)).thenReturn(Optional.of(player));

        mockMvc.perform(get("/api/players/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName").value("Kane"));

        verify(playerService, times(1)).getPlayerById(1L);
    }

    @Test
    void testCreatePlayer() throws Exception {
        Player player = new Player(1L, "Kane");
        when(playerService.createPlayer(any(Player.class))).thenReturn(player);

        String playerJson = "{\"playerName\": \"Kane\"}";

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName").value("Kane"));

        verify(playerService, times(1)).createPlayer(any(Player.class));
    }

    @Test
    void testUpdatePlayer() throws Exception {
        Player updatedPlayer = new Player(1L, "Kane Updated");
        when(playerService.updatePlayer(eq(1L), any(Player.class))).thenReturn(updatedPlayer);

        String playerJson = "{\"playerName\": \"Kane Updated\"}";

        mockMvc.perform(put("/api/players/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName").value("Kane Updated"));

        verify(playerService, times(1)).updatePlayer(eq(1L), any(Player.class));
    }

    @Test
    void testDeletePlayer() throws Exception {
        doNothing().when(playerService).deletePlayer(1L);

        mockMvc.perform(delete("/api/players/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(playerService, times(1)).deletePlayer(1L);
    }
}
