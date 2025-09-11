package com.example.takwiraapi.service;

import com.example.takwiraapi.constants.ErrorConstants;
import com.example.takwiraapi.dto.*;
import com.example.takwiraapi.entity.Match;
import com.example.takwiraapi.entity.MatchPlayer;
import com.example.takwiraapi.entity.Player;
import com.example.takwiraapi.entity.Team;
import com.example.takwiraapi.mapper.Mapper;
import com.example.takwiraapi.repository.MatchRepository;
import com.example.takwiraapi.repository.PlayerRepository;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private MatchService matchService;

    private Player player1;
    private Player player2;
    private Match match;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        player1 = new Player();
        player1.setPlayerId(1L);
        player1.setPlayerName("Alice");

        player2 = new Player();
        player2.setPlayerId(2L);
        player2.setPlayerName("Bob");

        match = new Match();
        match.setMatchId(1L);
        match.setMatchName("Match 1");
        match.setMatchPlayers(new ArrayList<>());
        match.setMatchGoals(new ArrayList<>());
    }

    @Test
    void testCreateMatch() {
        CreateMatchDto dto = new CreateMatchDto();
        dto.setMatchName("New Match");

        Match savedMatch = new Match();
        savedMatch.setMatchId(1L);
        savedMatch.setMatchName("New Match");

        when(matchRepository.save(any())).thenReturn(savedMatch);
        when(mapper.matchToDto(savedMatch)).thenReturn(new MatchDto());

        MatchDto result = matchService.createMatch(dto);

        assertNotNull(result);
        verify(matchRepository, times(1)).save(any(Match.class));
        verify(mapper, times(1)).matchToDto(savedMatch);
    }

    @Test
    void testAddPlayers() {
        AddPlayersToMatchDto dto = new AddPlayersToMatchDto();
        dto.setTeam1PlayerIds(List.of(1L));
        dto.setTeam2PlayerIds(List.of(2L));

        when(matchRepository.findActiveMatchesByMatchId(1L)).thenReturn(Optional.of(match));
        when(playerRepository.findAllById(List.of(1L))).thenReturn(List.of(player1));
        when(playerRepository.findAllById(List.of(2L))).thenReturn(List.of(player2));
        when(matchRepository.save(any(Match.class))).thenReturn(match);
        when(mapper.matchToDto(match)).thenReturn(new MatchDto());

        MatchDto result = matchService.addPlayers(1L, dto);

        assertNotNull(result);
        assertEquals(2, match.getMatchPlayers().size());
    }

    @Test
    void testAddGoalsSuccessful() {
        // ajouter les joueurs dans le match
        MatchPlayer mp1 = new MatchPlayer();
        mp1.setPlayer(player1);
        mp1.setTeam(Team.TEAM_1);
        MatchPlayer mp2 = new MatchPlayer();
        mp2.setPlayer(player2);
        mp2.setTeam(Team.TEAM_1);
        match.setMatchPlayers(List.of(mp1, mp2));

        GoalDto goalDto = new GoalDto();
        goalDto.setGoalScorer(new PlayerDto(player1.getPlayerId(), player1.getPlayerName()));
        goalDto.setGoalAssist(new PlayerDto(player2.getPlayerId(), player2.getPlayerName()));
        goalDto.setTeam(Team.TEAM_1);

        AddGoalsToMatchDto addGoalsDto = new AddGoalsToMatchDto();
        addGoalsDto.setGoals(List.of(goalDto));

        when(matchRepository.findActiveMatchesByMatchId(1L)).thenReturn(Optional.of(match));
        when(playerRepository.findById(player1.getPlayerId())).thenReturn(Optional.of(player1));
        when(playerRepository.findById(player2.getPlayerId())).thenReturn(Optional.of(player2));
        when(matchRepository.save(any(Match.class))).thenReturn(match);
        when(mapper.matchToDto(match)).thenReturn(new MatchDto());

        MatchDto result = matchService.addGoals(1L, addGoalsDto);

        assertNotNull(result);
        assertEquals(1, match.getMatchGoals().size());
        verify(matchRepository, times(1)).save(match);
    }

    @Test
    void testAddGoalsScorerNotInMatch() {
        GoalDto goalDto = new GoalDto();
        goalDto.setGoalScorer(new PlayerDto(player1.getPlayerId(), player1.getPlayerName()));
        goalDto.setGoalAssist(new PlayerDto(player2.getPlayerId(), player2.getPlayerName()));
        goalDto.setTeam(Team.TEAM_1);

        AddGoalsToMatchDto addGoalsDto = new AddGoalsToMatchDto();
        addGoalsDto.setGoals(List.of(goalDto));

        when(matchRepository.findActiveMatchesByMatchId(1L)).thenReturn(Optional.of(match));
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player1));

        FunctionArgumentException ex = assertThrows(FunctionArgumentException.class,
                () -> matchService.addGoals(1L, addGoalsDto));

        assertEquals(ErrorConstants.SCORER_NOT_FOUND_IN_MATCH, ex.getMessage());
    }

    @Test
    void testDeleteMatch() {
        when(matchRepository.findActiveMatchesByMatchId(1L)).thenReturn(Optional.of(match));
        matchService.deleteMatch(1L);
        assertTrue(match.isDeleted());
        verify(matchRepository, times(1)).save(match);
    }

    @Test
    void testGetMatchByIdNotFound() {
        when(matchRepository.findActiveMatchesByMatchId(1L)).thenReturn(Optional.empty());
        FunctionArgumentException ex = assertThrows(FunctionArgumentException.class,
                () -> matchService.getMatchById(1L));
        assertEquals(ErrorConstants.MATCH_NOT_FOUND, ex.getMessage());
    }
}
