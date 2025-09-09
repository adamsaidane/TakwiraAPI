package com.example.takwiraapi.controller;

import com.example.takwiraapi.dto.*;
import com.example.takwiraapi.entity.Match;
import com.example.takwiraapi.entity.Team;
import com.example.takwiraapi.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class MatchControllerTest {

    @Mock
    private MatchService matchService;

    @InjectMocks
    private MatchController matchController;

    private MatchDto matchDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        matchDto = new MatchDto();
        matchDto.setMatchId(1L);
        matchDto.setMatchName("Match 1");
        matchDto.setTeam1Players(List.of(new PlayerDto(1L, "Alice")));
        matchDto.setTeam2Players(List.of(new PlayerDto(2L, "Bob")));
        matchDto.setMatchGoals(List.of(new GoalDto(10L, new PlayerDto(1L, "Alice"), null, Team.TEAM_1)));
    }

    @Test
    void testGetAllMatches() {
        when(matchService.getAllMatches()).thenReturn(List.of(matchDto));

        ResponseEntity<List<MatchDto>> response = matchController.getAllMatches();

        assertEquals(1, response.getBody().size());
        assertEquals("Match 1", response.getBody().get(0).getMatchName());
        verify(matchService, times(1)).getAllMatches();
    }

    @Test
    void testGetMatchById() {
        when(matchService.getMatchById(1L)).thenReturn(matchDto);

        ResponseEntity<Optional<MatchDto>> response = matchController.getMatchById(1L);

        assertNotNull(response.getBody());
        verify(matchService, times(1)).getMatchById(1L);
    }

    @Test
    void testCreateMatch() {
        CreateMatchDto createDto = new CreateMatchDto();
        createDto.setMatchName("Match 1");

        when(matchService.createMatch(createDto)).thenReturn(matchDto);

        ResponseEntity<MatchDto> response = matchController.createMatch(createDto);

        assertNotNull(response.getBody());
        assertEquals("Match 1", response.getBody().getMatchName());
        verify(matchService, times(1)).createMatch(createDto);
    }

    @Test
    void testAddPlayers() {
        AddPlayersToMatchDto addPlayersDto = new AddPlayersToMatchDto();
        addPlayersDto.setTeam1PlayerIds(List.of(1L));
        addPlayersDto.setTeam2PlayerIds(List.of(2L));

        when(matchService.addPlayers(1L, addPlayersDto)).thenReturn(matchDto);

        ResponseEntity<MatchDto> response = matchController.addPlayers(1L, addPlayersDto);

        assertEquals("Match 1", response.getBody().getMatchName());
        verify(matchService, times(1)).addPlayers(1L, addPlayersDto);
    }

    @Test
    void testAddGoals() {
        AddGoalsToMatchDto addGoalsDto = new AddGoalsToMatchDto();
        addGoalsDto.setGoals(matchDto.getMatchGoals());

        when(matchService.addGoals(1L, addGoalsDto)).thenReturn(matchDto);

        ResponseEntity<MatchDto> response = matchController.addGoals(1L, addGoalsDto);

        assertEquals(1, response.getBody().getMatchGoals().size());
        verify(matchService, times(1)).addGoals(1L, addGoalsDto);
    }

    @Test
    void testUpdateMatch() {
        Match updatedMatch = new Match();
        updatedMatch.setMatchName("Updated Match");

        MatchDto updatedDto = new MatchDto();
        updatedDto.setMatchId(1L);
        updatedDto.setMatchName("Updated Match");

        when(matchService.updateMatch(1L, updatedMatch)).thenReturn(updatedDto);

        ResponseEntity<MatchDto> response = matchController.updateMatch(1L, updatedMatch);

        assertEquals("Updated Match", response.getBody().getMatchName());
        verify(matchService, times(1)).updateMatch(1L, updatedMatch);
    }

    @Test
    void testDeleteMatch() {
        doNothing().when(matchService).deleteMatch(1L);

        ResponseEntity<String> response = matchController.deleteMatch(1L);

        assertEquals("Match with ID 1 deleted successfully", response.getBody());
        verify(matchService, times(1)).deleteMatch(1L);
    }
}
