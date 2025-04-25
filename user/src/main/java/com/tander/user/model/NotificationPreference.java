package com.tander.user.model;

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
public class NotificationPreference {
    private boolean allowMessage;
    private boolean allowEmail;
    private boolean allowPush;
}
