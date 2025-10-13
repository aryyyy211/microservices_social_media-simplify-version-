package com.charllson.notificationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;  // User who receives the notification

    @Column(nullable = false)
    private String type;  // FOLLOW, POST_CREATED, POST_LIKED, COMMENT_ADDED

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private Boolean isRead;

    private Long relatedUserId;  // User who triggered the notification (e.g., who followed)

    private Long relatedPostId;  // Related post ID (if applicable)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRead == null) {
            isRead = false;
        }
    }
}
