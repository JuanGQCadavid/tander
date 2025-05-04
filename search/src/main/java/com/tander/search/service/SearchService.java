package com.tander.search.service;

import com.tander.matches.dto.MatchDTO;
import com.tander.profile.dto.ProfileDTO;
import com.tander.profile.model.Gender;
import com.tander.profile.model.Location;
import com.tander.profile.model.Preferences;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchService {

    private final WebClient webClient;
    private final String profileServiceUrl;
    private final String matchesServiceUrl;

    public SearchService(
            WebClient.Builder webClientBuilder,
            @Value("${profile.service.url}") String profileServiceUrl,
            @Value("${matches.service.url}") String matchesServiceUrl) {
            this.webClient = webClientBuilder.build();
            this.profileServiceUrl = profileServiceUrl;
            this.matchesServiceUrl = matchesServiceUrl;
    }

    public List<ProfileDTO> search(Long userId) {
        if (Objects.isNull(userId)) {
            return getAllProfiles();
        } else {
            ProfileDTO userProfile = getProfileByUserId(userId);
            Preferences userPreferences = userProfile.getPreferences();
            List<ProfileDTO> profilesBasedOnPreferences = searchByPreferences(userPreferences);
            List<ProfileDTO> unAnsweredProfiles = getUnAnsweredMatches(userId);

            // Filter duplicates by userId, keeping the first occurrence
            List<ProfileDTO> results = new ArrayList<>(unAnsweredProfiles);
            results.addAll(profilesBasedOnPreferences);

            Map<Long, ProfileDTO> uniqueProfiles = new LinkedHashMap<>();
            for (ProfileDTO profile : results) {
                if (!Objects.equals(profile.getUserId(), userId)) {
                    uniqueProfiles.putIfAbsent(profile.getUserId(), profile);
                }
            }
            return new ArrayList<>(uniqueProfiles.values());
        }
    }

    private List<ProfileDTO> getAllProfiles() {
        return webClient.get()
                .uri(profileServiceUrl + "/api/profiles")
                .retrieve()
                .bodyToFlux(ProfileDTO.class)
                .collectList()
                .block();
    }

    private ProfileDTO getProfileByUserId(Long userId) {
        return webClient.get()
                .uri(profileServiceUrl + "/api/profiles/{userId}", userId)
                .retrieve()
                .bodyToMono(ProfileDTO.class)
                .block();
    }

    public List<ProfileDTO> searchByPreferences(Preferences preferences) {
        // Get all profiles that match gender and age preferences
        Gender genderPreference = preferences.getGenderPreference() != null ? 
            preferences.getGenderPreference() : null;
        int minAge = preferences.getMinAge();
        // If max age is 0 then, use default maxAge (99)
        int maxAge = preferences.getMaxAge() > 0 ? preferences.getMaxAge() : 99;

        List<ProfileDTO> filteredProfiles = findProfilesByPreferences(genderPreference, minAge, maxAge);

        // If location preference is provided, filter by distance
        if (preferences.getLocationPreference() != null && preferences.getMaxDistance() > 0) {
            Location locPref = preferences.getLocationPreference();
            List<ProfileDTO> profilesInRange = findProfilesWithinDistance(
                    locPref.getLatitude(),
                    locPref.getLongitude(),
                    preferences.getMaxDistance()
            );
            filteredProfiles.retainAll(profilesInRange);
        }

        // Calculate interest match scores and sort
        if (preferences.getInterests() != null && !preferences.getInterests().isEmpty()) {
            // Create a map of profile to interest match count
            Map<ProfileDTO, Integer> interestMatchCounts = new HashMap<>();
            for (ProfileDTO profile : filteredProfiles) {
                int matchCount = 0;
                if (profile.getPreferences() != null && profile.getPreferences().getInterests() != null) {
                    for (String userInterest : preferences.getInterests()) {
                        if (profile.getPreferences().getInterests().contains(userInterest)) {
                            matchCount++;
                        }
                    }
                }
                interestMatchCounts.put(profile, matchCount);
            }

            // Sort profiles by interest match count in descending order
            filteredProfiles.sort((p1, p2) -> {
                int count1 = interestMatchCounts.getOrDefault(p1, 0);
                int count2 = interestMatchCounts.getOrDefault(p2, 0);
                return Integer.compare(count2, count1); // Descending order
            });
        }

        return filteredProfiles;
    }

    private List<ProfileDTO> findProfilesByPreferences(Gender gender, Integer minAge, Integer maxAge) {
        if (gender == null) {
            // If no gender preference, get all profiles within age range
            return webClient.get()
                    .uri(profileServiceUrl + "/api/profiles/search/preferences?minAge={minAge}&maxAge={maxAge}",
                            minAge, maxAge)
                    .retrieve()
                    .bodyToFlux(ProfileDTO.class)
                    .collectList()
                    .block();
        } else {
            // Get profiles matching both gender and age range
            return webClient.get()
                    .uri(profileServiceUrl + "/api/profiles/search/preferences?gender={gender}&minAge={minAge}&maxAge={maxAge}",
                            gender, minAge, maxAge)
                    .retrieve()
                    .bodyToFlux(ProfileDTO.class)
                    .collectList()
                    .block();
        }
    }

    private List<ProfileDTO> findProfilesByInterests(List<String> interests) {
        return webClient.get()
                .uri(profileServiceUrl + "/api/profiles/search/interests?interests={interests}",
                        String.join(",", interests))
                .retrieve()
                .bodyToFlux(ProfileDTO.class)
                .collectList()
                .block();
    }

    private List<ProfileDTO> findProfilesWithinDistance(Double latitude, Double longitude, Integer distance) {
        return webClient.get()
                .uri(profileServiceUrl + "/api/profiles/search/distance?latitude={latitude}&longitude={longitude}&distance={distance}",
                        latitude, longitude, distance)
                .retrieve()
                .bodyToFlux(ProfileDTO.class)
                .collectList()
                .block();
    }

    public List<ProfileDTO> getUnAnsweredMatches(Long id) {
        List<MatchDTO> unAnsweredMatches = webClient.get()
                .uri(matchesServiceUrl + "/api/matches/profile/{id}/unAnswered", id)
                .retrieve()
                .bodyToFlux(MatchDTO.class)
                .collectList()
                .block();

        assert unAnsweredMatches != null;
        List<ProfileDTO> profiles = new ArrayList<>(unAnsweredMatches.size());
        for (MatchDTO match : unAnsweredMatches) {
            ProfileDTO profileToAdd;
            if (!Objects.equals(match.getProfileId1(), id)) {
                profileToAdd = getProfileByUserId(match.getProfileId1());
            } else {
                profileToAdd = getProfileByUserId(match.getProfileId2());
            }
            profiles.add(profileToAdd);
        }
        return profiles;
    }
}
