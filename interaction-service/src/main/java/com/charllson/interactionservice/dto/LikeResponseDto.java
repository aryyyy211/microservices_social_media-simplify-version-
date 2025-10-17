package com.charllson.interactionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDto {
    private Long id;
    private Long userId;
    private String username;
    private Long postId;
    private LocalDateTime createdAt;
}