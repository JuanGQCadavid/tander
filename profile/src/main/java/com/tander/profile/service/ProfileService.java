package com.tander.profile.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tander.profile.dto.LocationDTO;
import com.tander.profile.dto.PreferencesDTO;
import com.tander.profile.dto.ProfileDTO;
import com.tander.profile.exception.ProfileNotFoundException;
import com.tander.profile.model.Gender;
import com.tander.profile.model.Location;
import com.tander.profile.model.Profile;
import com.tander.profile.repository.ProfileRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    public List<ProfileDTO> getAllProfiles() {
        List<Profile> profiles = new ArrayList<>();
        profileRepository.findAll().forEach(profiles::add);
        return profiles.stream().map(this::mapToProfileDto).toList();
    }

    public ProfileDTO getProfileByUserId(Long userId) {
        Profile profile = getProfile(userId);
        return mapToProfileDto(profile);
    }

    public ProfileDTO getProfileByName(String name) {
        Profile profile = profileRepository.findByName(name)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with name " + name));
        return mapToProfileDto(profile);
    }

    public ProfileDTO createProfile(ProfileDTO profileDTO) {
        Profile profile = Profile.builder()
                .userId(profileDTO.getUserId())
                .name(profileDTO.getName())
                .dateOfBirth(profileDTO.getDateOfBirth())
                .gender(profileDTO.getGender())
                .preferences(profileDTO.getPreferences())
                .location(profileDTO.getLocation())
                .bio(profileDTO.getBio())
                .build();
        profileRepository.save(profile);
        log.info("Profile {} is added to DB", profile.getUserId());
        return mapToProfileDto(profile);
    }

    public ProfileDTO updateProfile(Long userId, ProfileDTO profileDTO) {
        Profile profile = getProfile(userId);

        profile.setName(profileDTO.getName());
        profile.setDateOfBirth(profileDTO.getDateOfBirth());
        profile.setGender(profileDTO.getGender());
        profile.setLocation(profileDTO.getLocation());
        profile.setBio(profileDTO.getBio());
        if (profileDTO.getPreferences() != null) {
            profile.setPreferences(profileDTO.getPreferences());
        }
        log.info("Profile {} is updated", profile.getUserId());
        Profile updatedProfile = profileRepository.save(profile);
        return mapToProfileDto(updatedProfile);
    }

    public ProfileDTO updatePreferences(Long userId, PreferencesDTO preferencesDTO) {
        Profile profile = getProfile(userId);

        profile.getPreferences().setGenderPreference(preferencesDTO.getGender());
        profile.getPreferences().setInterests(preferencesDTO.getInterests());
        profile.getPreferences().setLocationPreference(preferencesDTO.getLocation());
        profile.getPreferences().setMaxDistance(preferencesDTO.getMaxDistance());
        profile.getPreferences().setMinAge(preferencesDTO.getMinAge());
        profile.getPreferences().setMaxAge(preferencesDTO.getMaxAge());
        log.info("Profile {} preferences are updated", profile.getUserId());
        Profile updatedProfile = profileRepository.save(profile);
        return mapToProfileDto(updatedProfile);
    }

    public ProfileDTO updateLocation(Long userId, LocationDTO locationDTO) {
        Profile profile = getProfile(userId);

        Location location = Location.builder()
                .latitude(locationDTO.getLatitude())
                .longitude(locationDTO.getLongitude())
                .build();
        profile.setLocation(location);
        profileRepository.save(profile);
        log.info("Profile {} location is updated", profile.getUserId());
        return mapToProfileDto(profile);
    }

    public void deleteProfile(Long userId) {
        getProfile(userId);
        profileRepository.deleteById(userId);
        log.info("Profile {} has been deleted", userId);
    }

    public List<ProfileDTO> findProfilesByGender(Gender gender) {
        return profileRepository.findByGender(gender).stream()
                .map(this::mapToProfileDto)
                .collect(Collectors.toList());
    }

    public List<ProfileDTO> findProfilesWithinDistance(Double latitude, Double longitude, Integer distance) {
        return profileRepository.findProfilesWithinDistance(latitude, longitude, distance).stream()
                .map(this::mapToProfileDto)
                .collect(Collectors.toList());
    }

    public List<ProfileDTO> findProfilesByPreferences(Gender gender, Integer minAge, Integer maxAge) {
        return profileRepository.findProfilesByPreferences(gender, minAge, maxAge).stream()
                .map(this::mapToProfileDto)
                .collect(Collectors.toList());
    }

    public List<ProfileDTO> findProfilesByInterests(List<String> interests) {
        return profileRepository.findByInterests(interests).stream()
                .map(this::mapToProfileDto)
                .collect(Collectors.toList());
    }

    private Profile getProfile(Long userId) {
        return profileRepository.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with ID " + userId));
    }

    private ProfileDTO mapToProfileDto(Profile profile) {
        return ProfileDTO.builder()
                .userId(profile.getUserId())
                .name(profile.getName())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .preferences(profile.getPreferences())
                .location(profile.getLocation())
                .bio(profile.getBio())
                .build();
    }
}
