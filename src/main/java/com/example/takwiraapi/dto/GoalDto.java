package com.example.takwiraapi.dto;

import com.example.takwiraapi.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto {

    private Long goalId;

    private PlayerDto goalScorer;

    private PlayerDto goalAssist;

    private Team team;

}

