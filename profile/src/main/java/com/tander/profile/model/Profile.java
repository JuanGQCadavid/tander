package com.tander.profile.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "profiletable")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @Id
    private Long userId;
    private String name;
    // TODO: image
    private LocalDateTime dateOfBirth;
    private Gender gender;
    private Preferences preferences;
    private Location location;
    private String bio;
}
