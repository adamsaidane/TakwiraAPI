package com.example.takwiraapi.repository;

import com.example.takwiraapi.entity.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, Long> {

    @Query("SELECT mp FROM MatchPlayer mp WHERE mp.player.playerId = :playerId AND mp.deleted = false")
    List<MatchPlayer> getPlayerMatches(Long playerId);

}
