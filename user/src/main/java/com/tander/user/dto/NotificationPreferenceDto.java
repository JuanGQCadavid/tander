package com.tander.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationPreferenceDto {
    private boolean allowMessage;
    private boolean allowEmail;
    private boolean allowPush;

}
