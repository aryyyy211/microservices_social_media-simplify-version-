package com.charllson.notificationservice.service;

import com.charllson.notificationservice.event.PostCreatedEvent;
import com.charllson.notificationservice.event.UserFollowedEvent;
import com.charllson.notificationservice.event.PostLikedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final NotificationService notificationService;

    /**
     * 🔥 CONSUMER #1: Listen to user-followed-topic
     * When someone follows a user, send notification
     */
    @KafkaListener(topics = "user-followed-topic", groupId = "notification-service")
    public void consumeUserFollowedEvent(UserFollowedEvent event) {
        log.info("🔔 Received UserFollowedEvent: {} followed {}",
                event.getFollowerUsername(), event.getFollowingUsername());

        // Create notification for the user who was followed
        String message = String.format("%s started following you!", event.getFollowerUsername());

        notificationService.createNotification(
                event.getFollowingId(),  // Send to the user who was followed
                "FOLLOW",
                message,
                event.getFollowerId(),   // Who triggered this notification
                null                     // No related post
        );

        log.info("✅ Notification created for user {} (followed by {})",
                event.getFollowingId(), event.getFollowerUsername());
    }

    /**
     * 🔥 CONSUMER #2: Listen to post-created-topic
     * When someone creates a post, notify their followers (future enhancement)
     */
    @KafkaListener(topics = "post-created-topic", groupId = "notification-service")
    public void consumePostCreatedEvent(PostCreatedEvent event) {
        log.info("📝 Received PostCreatedEvent: {} created post ID {}",
                event.getUsername(), event.getPostId());

        // For now, just log it.
        // Later, we can notify followers when Feed Service is ready
        log.info("Post created by user {}: '{}'", event.getUsername(),
                event.getContent().substring(0, Math.min(50, event.getContent().length())));

        // Future: Get followers and notify them
        // notificationService.createNotification(...);
    }

    /**
    * 🔥🔥 CONSUMER #2: Listen to post-liked-topic
    * When someone like a post, notify their followers (future enhancement)
     */
    @KafkaListener(topics = "post-liked-topic", groupId = "notification-service")
    public void consumePostLikedEvent(PostLikedEvent event) {
        log.info("❤️ Received PostLikedEvent: {} liked post {}",
                event.getUsername(), event.getPostId());

        // Notify post owner that someone liked their post
        String message = String.format("%s liked your post!", event.getUsername());

        notificationService.createNotification(
                event.getPostOwnerId(),  // Notify the post owner
                "POST_LIKED",
                message,
                event.getUserId(),       // Who liked it
                event.getPostId()        // Which post
        );

        log.info("✅ Notification created for post owner {} (post liked by {})",
                event.getPostOwnerId(), event.getUsername());
    }

}
