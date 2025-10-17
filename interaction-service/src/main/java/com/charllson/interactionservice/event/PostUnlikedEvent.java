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
public class PostUnlikedEvent {

    private Long postId;
    private Long userId;
    private LocalDateTime eventTime;
}
