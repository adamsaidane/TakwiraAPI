package com.example.takwiraapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchDto {

    private Long matchId;

    private String matchName;

    private List<PlayerDto> team1Players;

    private List<PlayerDto> team2Players;

    private List<GoalDto> matchGoals;
}
