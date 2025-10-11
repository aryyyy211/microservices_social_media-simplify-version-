package com.charllson.userservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFollowedEvent {
    private Long followerId;
    private Long followingId;
    private String followerUsername;
    private String followingUsername;
    private LocalDateTime eventTime;
}