package com.tander.profile.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.tander.profile.model.Gender;
import com.tander.profile.model.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends CrudRepository<Profile, Long> {

    List<Profile> findByGender(Gender gender);

    Optional<Profile> findByName(String name);

    @Query(value = """
        SELECT * FROM profiletable p
        WHERE p.interests && :preferredInterests
        """, nativeQuery = true)
    List<Profile> findByInterests(@Param("preferredInterests") String[] preferredInterests);

    @Query(value = """
            SELECT * FROM profiletable p
            WHERE p.gender = :preferredGender
            AND EXTRACT(YEAR FROM AGE(CURRENT_DATE, p.date_of_birth)) BETWEEN :minAge AND :maxAge
            """, nativeQuery = true)
    List<Profile> findProfilesByPreferences(
            @Param("preferredGender") String preferredGender,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge);

    @Query(value = """
            SELECT * FROM profiletable p
            WHERE
            (6371 * acos(
                cos(radians(:latitude)) *
                cos(radians(p.profile_latitude)) *
                cos(radians(p.profile_longitude) - radians(:longitude)) +
                sin(radians(:latitude)) *
                sin(radians(p.profile_latitude))
            )) < :distance
            """, nativeQuery = true)
    List<Profile> findProfilesWithinDistance(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distance") double distanceInKm);

}
