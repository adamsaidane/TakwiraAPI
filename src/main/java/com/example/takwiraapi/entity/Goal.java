package com.example.takwiraapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "goals")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long goalId;

    // Joueur qui marque
    @ManyToOne(optional = false)
    @JoinColumn(name = "scorer_id", nullable = false)
    private Player goalScorer;

    // Joueur qui fait la passe décisive (peut être null)
    @ManyToOne
    @JoinColumn(name = "assist_id")
    private Player goalAssist;

    // L’équipe à laquelle appartient le but
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Team team;

    // Match associé
    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;
}
