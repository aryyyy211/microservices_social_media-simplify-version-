package com.charllson.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
