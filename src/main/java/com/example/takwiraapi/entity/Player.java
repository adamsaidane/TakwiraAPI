package com.example.takwiraapi.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "players")
@JsonPropertyOrder({"playerId", "playerName", "deleted", "createdAt", "updatedAt", "deletedAt"})
public class Player extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long playerId;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String playerName;
}
