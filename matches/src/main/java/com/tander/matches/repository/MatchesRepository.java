package com.tander.matches.repository;

import com.tander.matches.model.Match;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchesRepository extends CrudRepository<Match, Long> {

    @Query("SELECT m FROM Match m WHERE m.profileId1 = :profileId1 AND m.profileId2 = :profileId2 OR m.profileId1 = :profileId2 AND m.profileId2 = :profileId1")
    Optional<Match> findByUserIds (Long profileId1, Long profileId2);

    @Query("SELECT m FROM Match m WHERE m.profileId1 = :profileId OR m.profileId2 = :profileId")
    List<Match> findUserMatches (Long profileId);
}
