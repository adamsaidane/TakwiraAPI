package com.example.takwiraapi.mapper;

import com.example.takwiraapi.dto.GoalDto;
import com.example.takwiraapi.dto.MatchDto;
import com.example.takwiraapi.dto.PlayerDto;
import com.example.takwiraapi.entity.Goal;
import com.example.takwiraapi.entity.Match;
import com.example.takwiraapi.entity.Player;
import com.example.takwiraapi.entity.Team;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Mapper {
    public PlayerDto playerToDto(Player player) {
        if (player == null) {
            return null;
        }
        return new PlayerDto(player.getPlayerId(), player.getPlayerName());
    }

    public GoalDto goalToDto(Goal goal) {
        if (goal == null) {
            return null;
        }
        return new GoalDto(goal.getGoalId(), playerToDto(goal.getGoalScorer()), playerToDto(goal.getGoalAssist()), goal.getTeam());
    }

    public MatchDto matchToDto(Match match) {
        if (match == null) {
            return null;
        }
        List<PlayerDto> team1 = match.getMatchPlayers().stream()
                .filter(pm -> pm.getTeam() == Team.TEAM_1)
                .map(pm -> playerToDto(pm.getPlayer()))
                .collect(Collectors.toList());

        List<PlayerDto> team2 = match.getMatchPlayers().stream()
                .filter(pm -> pm.getTeam() == Team.TEAM_2)
                .map(pm -> playerToDto(pm.getPlayer()))
                .collect(Collectors.toList());

        List<GoalDto> goals = match.getMatchGoals().stream()
                .map(this::goalToDto)
                .collect(Collectors.toList());

        return new MatchDto(match.getMatchId(), match.getMatchName(), team1, team2, goals);
    }
}
