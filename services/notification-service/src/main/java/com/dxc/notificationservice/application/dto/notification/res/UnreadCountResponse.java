package com.dxc.notificationservice.application.dto.notification.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnreadCountResponse {
    private long count;
}
