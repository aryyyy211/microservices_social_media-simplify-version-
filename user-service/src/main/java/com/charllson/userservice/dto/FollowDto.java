package com.charllson.userservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowDto {
    private Long id;
    private Long followerId;
    private Long followingId;
    private String followerUsername;
    private String followingUsername;
    private LocalDateTime createdAt;
}
