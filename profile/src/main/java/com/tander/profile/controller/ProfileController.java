package com.tander.profile.controller;

import com.tander.profile.dto.ProfileDTO;
import com.tander.profile.dto.PreferencesDTO;
import com.tander.profile.model.Gender;
import com.tander.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<List<ProfileDTO>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getAllProfiles());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDTO> getProfileByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfileByUserId(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<ProfileDTO> createProfile(
            @Valid @RequestBody ProfileDTO profileDTO) {
        return new ResponseEntity<>(profileService.createProfile(profileDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ProfileDTO> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody ProfileDTO profileDTO) {
        return ResponseEntity.ok(profileService.updateProfile(userId, profileDTO));
    }

    @PutMapping("/{userId}/preferences")
    public ResponseEntity<ProfileDTO> updatePreferences(
            @PathVariable Long userId,
            @Valid @RequestBody PreferencesDTO preferencesDTO) {
        return ResponseEntity.ok(profileService.updatePreferences(userId, preferencesDTO));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(
            @PathVariable Long userId) {
        profileService.deleteProfile(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search/gender/{gender}")
    public ResponseEntity<List<ProfileDTO>> findProfilesByGender(
            @PathVariable Gender gender) {
        return ResponseEntity.ok(profileService.findProfilesByGender(gender));
    }

    @GetMapping("/search/distance")
    public ResponseEntity<List<ProfileDTO>> findProfilesWithinDistance(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Integer distance) {
        return ResponseEntity.ok(profileService.findProfilesWithinDistance(latitude, longitude, distance));
    }

    @GetMapping("/search/interests")
    public ResponseEntity<List<ProfileDTO>> findProfilesByInterests(
            @RequestParam List<String> interests) {
        return ResponseEntity.ok(profileService.findProfilesByInterests(interests));
    }

    @GetMapping("/search/preferences")
    public ResponseEntity<List<ProfileDTO>> findProfilesByPreferences(
            @RequestParam(required = false) Gender gender,
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        return ResponseEntity.ok(profileService.findProfilesByPreferences(gender, minAge, maxAge));
    }
}