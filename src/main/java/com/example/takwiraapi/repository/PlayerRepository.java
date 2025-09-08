package com.example.takwiraapi.repository;

import com.example.takwiraapi.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByPlayerNameAndDeletedIsFalse(String playerName);

    @Query("SELECT p FROM Player p WHERE p.deleted = false")
    List<Player> findAllActivePlayers();

    @Query("SELECT p FROM Player p WHERE p.playerId = :playerId AND p.deleted = false")
    Optional<Player> findActivePlayerById(Long playerId);

    @Query("SELECT p FROM Player p WHERE p.playerName = :playerName AND p.deleted = false")
    Optional<Player> findActivePlayerByName(String playerName);
}
