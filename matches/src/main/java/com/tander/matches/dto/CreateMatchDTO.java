package com.tander.matches.dto;

import com.tander.matches.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMatchDTO {

    private Long profileId1;
    private Long profileId2;
    private Status profileStatus1;
}
