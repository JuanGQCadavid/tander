package com.tander.profile.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {

    @Min(value = -90, message = "Latitude must be greater than -90")
    @Max(value = 90, message = "Latitude must be less than 90")
    @NotNull(message = "Latitude is required")
    private Long latitude;

    @Min(value = -180, message = "Longitude must be greater than -180")
    @Max(value = 180, message = "Longitude must be less than 180")
    private Long longitude;
}
