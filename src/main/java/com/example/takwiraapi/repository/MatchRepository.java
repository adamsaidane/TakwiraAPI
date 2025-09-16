package com.example.takwiraapi.repository;

import com.example.takwiraapi.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("SELECT m FROM Match m WHERE m.deleted = false order by m.createdAt desc")
    List<Match> findAllActiveMatches();

    @Query("SELECT m FROM Match m WHERE m.matchId = :matchId AND m.deleted = false")
    Optional<Match> findActiveMatchesByMatchId(Long matchId);

}
