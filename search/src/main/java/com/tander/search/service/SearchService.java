package com.tander.search.service;

import com.tander.matches.dto.MatchDTO;
import com.tander.matches.service.MatchesService;
import com.tander.profile.dto.ProfileDTO;
import com.tander.profile.model.Location;
import com.tander.profile.model.Preferences;
import com.tander.profile.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SearchService {

    private ProfileService profileService;
    private MatchesService matchesService;

    // First in the list are the matches user has to respond to (e.g the other person has responded)
    // Secondly all profiles are acquired that match the users preferences
    public List<ProfileDTO> search(Long userId) {
        if (Objects.isNull(userId)) {
            return profileService.getAllProfiles();
        } else {
            ProfileDTO userProfile = profileService.getProfileByUserId(userId);
            Preferences userPreferences = userProfile.getPreferences();
            List<ProfileDTO> profilesBasedOnPreferences = searchByPreferences(userPreferences);
            List<ProfileDTO> unAnsweredProfiles = getUnAnsweredMatches(userId);
            List<ProfileDTO> results = new ArrayList<>(unAnsweredProfiles);
            results.addAll(profilesBasedOnPreferences);
            return results;
        }
    }

    // Search by interests, location and preferences taking into account all of those filters
    public List<ProfileDTO> searchByPreferences (Preferences preferences) {
        List<ProfileDTO> basedOnInterests = new ArrayList<>();
        List<ProfileDTO> basedOnLocation = new ArrayList<>();
        List<ProfileDTO> basedOnPreferences = profileService.findProfilesByPreferences(
                preferences.getGenderPreference(),
                preferences.getMinAge(),
                preferences.getMaxAge()
        );

        if (preferences.getInterests() != null) {
            basedOnInterests = profileService.findProfilesByInterests(preferences.getInterests());
        }

        if (preferences.getLocationPreference() != null && preferences.getMaxDistance() > 0) {
            Location locPref = preferences.getLocationPreference();
            basedOnLocation = profileService.findProfilesWithinDistance(
                    locPref.getLatitude(),
                    locPref.getLongitude(),
                    preferences.getMaxDistance()
            );
        }

        List<ProfileDTO> finalResults = new ArrayList<>(basedOnInterests);
        finalResults.retainAll(basedOnLocation);
        finalResults.retainAll(basedOnPreferences);

        return finalResults;
    }

    // Get list of matches, that have been unanswered by the user
    // Return the list of profiles of other users from these matches
    public List<ProfileDTO> getUnAnsweredMatches(Long id) {
        List<MatchDTO> unAnsweredMatches = matchesService.getUnansweredMatches(id);
        List<ProfileDTO> profiles = new ArrayList<>(unAnsweredMatches.size());
        for (MatchDTO match : unAnsweredMatches) {
            ProfileDTO profileToAdd;
            if (!Objects.equals(match.getProfileId1(), id)) {
                profileToAdd = profileService.getProfileByUserId(match.getProfileId1());
            } else {
                profileToAdd = profileService.getProfileByUserId(match.getProfileId2());
            }
            profiles.add(profileToAdd);
        }
        return profiles;
    }

}
