package com.tander.profile.model;

import java.util.List;

import jakarta.persistence.Embeddable;
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

    private Gender gender;
    private List<String> interests;
    private Location location;
    private int maxDistance;
    private int minAge;
    private int maxAge;
}
