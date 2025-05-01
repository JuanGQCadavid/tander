package com.tander.profile.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.tander.profile.model.Gender;
import com.tander.profile.model.Location;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferencesDTO {

    private Gender gender;

    private List<String> interests;

    private Location location;

    @Min(value = 1, message = "Minimum distance must be at least 1 km")
    @Max(value = 200, message = "Maximum distance must be at most 200 km")
    private int maxDistance;

    @Min(value = 18, message = "Minimum age must be at least 18")
    private int minAge;

    @Min(value = 18, message = "Maximum age must be at least 18")
    @Max(value = 120, message = "Maximum age must be at most 120")
    private int maxAge;
}