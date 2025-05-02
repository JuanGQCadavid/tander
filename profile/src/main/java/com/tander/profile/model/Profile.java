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

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private Preferences preferences;

    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "profile_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "profile_longitude"))
    })
    @Embedded
    private Location location;
    private String bio;
}
