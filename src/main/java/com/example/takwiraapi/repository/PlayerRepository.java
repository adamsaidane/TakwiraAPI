package com.example.takwiraapi.repository;

import com.example.takwiraapi.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByPlayerName(String playerName);

    Optional<Player> findByPlayerName(String playerName);
}
