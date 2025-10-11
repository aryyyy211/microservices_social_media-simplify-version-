package com.charllson.postservice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateDto {
    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    private String content;

    private String imageUrl;
}
