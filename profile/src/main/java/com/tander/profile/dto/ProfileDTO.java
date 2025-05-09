package com.tander.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.tander.profile.model.Gender;
import com.tander.profile.model.Location;
import com.tander.profile.model.Preferences;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

    private Long userId;

    @NotBlank(message = "Name is required")
    private String name;

    private Long imageId;

    @NotNull(message = "Date of birth is required")
    private LocalDateTime dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private Preferences preferences;

    private Location location;

    private String bio;
}
