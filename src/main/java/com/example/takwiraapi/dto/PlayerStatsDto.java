package com.example.takwiraapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStatsDto {
    private Long playerId;

    private String playerName;

    private Integer matchesPlayed;

    private Integer goalsScored;

    private Integer assists;

    private Integer hatTricks;

    private Integer assistHatTricks;

    private Integer totalGoalContributions;

    private Integer matchesWon;

    private Integer matchesLost;

    private Double winRatio;

    private Double avgGoalsPerMatch;

    private Double avgAssistsPerMatch;

    private Double avgGoalContributionsPerMatch;

    private Integer consecutiveWins;

    private Integer maxGoalsInMatch;

    private Integer maxAssistsInMatch;

    private Integer maxGoalContributionsInMatch;

    public PlayerStatsDto(Long playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.matchesPlayed = 0;
        this.goalsScored = 0;
        this.assists = 0;
        this.hatTricks = 0;
        this.assistHatTricks = 0;
        this.matchesWon = 0;
        this.matchesLost = 0;
        this.winRatio = 0.0;
        this.avgGoalsPerMatch = 0.0;
        this.avgAssistsPerMatch = 0.0;
        this.avgGoalContributionsPerMatch = 0.0;
        this.totalGoalContributions = 0;
        this.consecutiveWins = 0;
        this.maxGoalsInMatch = 0;
        this.maxAssistsInMatch = 0;
        this.maxGoalContributionsInMatch = 0;
    }
}
