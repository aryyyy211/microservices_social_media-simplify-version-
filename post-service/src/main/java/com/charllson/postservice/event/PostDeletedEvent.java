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
public class PostDeletedEvent {
    private Long postId;
    private Long userId;
    private LocalDateTime eventTime;
}