package com.tander.matches.dto;

import com.tander.matches.model.Status;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchDTO {

    @Id
    private Long matchId;
    private Long profileId1;
    private Long profileId2;
    private Status profileStatus1;
    private Status profileStatus2;
    private LocalDateTime createdAt;
}
