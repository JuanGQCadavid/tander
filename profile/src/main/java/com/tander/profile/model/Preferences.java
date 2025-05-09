package com.tander.profile.model;

import java.util.List;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Preferences {

    @Enumerated(EnumType.STRING)
    private Gender genderPreference;
    private List<String> interests;

    @Embedded
    private Location locationPreference;
    private int maxDistance;
    private int minAge;
    private int maxAge;
}
