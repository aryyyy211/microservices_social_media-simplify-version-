package com.charllson.interactionservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikedEvent {

    private Long postId;
    private Long userId;
    private String username;
    private Long postOwnerId;  // Owner of the post (for notification)
    private LocalDateTime eventTime;
}
