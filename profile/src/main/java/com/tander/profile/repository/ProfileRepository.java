package com.tander.profile.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.tander.profile.model.Gender;
import com.tander.profile.model.Profile;
import java.util.List;

public interface ProfileRepository extends CrudRepository<Profile, Long> {

    List<Profile> findByGender(Gender gender);

    List<Profile> findByName(String name);

    @Query("SELECT p FROM Profile p JOIN p.preferences.interests i WHERE i IN :interests")
    List<Profile> findByInterests(@Param("interests") List<String> interests);

    @Query(value = """
            SELECT p FROM Profile p
            WHERE
            (6371 * acos(
                cos(radians(:latitude)) *
                cos(radians(p.location.latitude)) *
                cos(radians(p.location.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) *
                sin(radians(p.location.latitude))
            )) < :distance
            """)
    List<Profile> findProfilesWithinDistance(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distance") double distanceInKm);

}
