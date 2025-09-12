package com.example.takwiraapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPlayersToMatchDto {

    private List<Long> team1PlayerIds;

    private List<Long> team2PlayerIds;
}
