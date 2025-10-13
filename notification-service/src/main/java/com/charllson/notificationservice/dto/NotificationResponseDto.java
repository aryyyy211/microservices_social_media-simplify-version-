package com.charllson.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {

    private Long id;
    private Long userId;
    private String type;
    private String message;
    private Boolean isRead;
    private Long relatedUserId;
    private Long relatedPostId;
    private LocalDateTime createdAt;
}