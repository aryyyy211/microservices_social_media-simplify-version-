package com.charllson.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreatedEvent {
    private Long postId;
    private Long userId;
    private String username;
    private String content;
    private String imageUrl;
    private LocalDateTime eventTime;
}