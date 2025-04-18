package com.tander.user.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @Id
    private Long id;
    private String email;
    private String phoneNumber;
    // TODO: notification preference
}
