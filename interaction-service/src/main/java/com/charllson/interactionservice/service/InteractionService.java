package com.charllson.interactionservice.service;

import com.charllson.interactionservice.dto.LikeResponseDto;
import com.charllson.interactionservice.dto.PostResponseDto;
import com.charllson.interactionservice.dto.UserResponseDto;
import com.charllson.interactionservice.event.PostLikedEvent;
import com.charllson.interactionservice.event.PostUnlikedEvent;
import com.charllson.interactionservice.model.Like;
import com.charllson.interactionservice.repository.LikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InteractionService {
    private final LikeRepository likeRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, PostLikedEvent> postLikedKafkaTemplate;
    private final KafkaTemplate<String, PostUnlikedEvent> postUnlikedKafkaTemplate;

    @Transactional
    public LikeResponseDto likePost(Long userId, Long postId) {
        log.info("User {} attempting to like post {}", userId, postId);

        // Check if already liked
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new RuntimeException("You already liked this post");
        }

        // Verify user exists
        UserResponseDto user = getUserById(userId);

        // Verify post exists and get post details
        PostResponseDto post = getPostById(postId);

        // Create like
        Like like = Like.builder()
                .userId(userId)
                .postId(postId)
                .build();

        Like savedLike = likeRepository.save(like);
        log.info("User {} successfully liked post {}", userId, postId);

        // Publish PostLikedEvent to Kafka
        PostLikedEvent event = PostLikedEvent.builder()
                .postId(postId)
                .userId(userId)
                .username(user.getUsername())
                .postOwnerId(post.getUserId())
                .eventTime(LocalDateTime.now())
                .build();

        postLikedKafkaTemplate.send("post-liked-topic", event);
        log.info("Published PostLikedEvent to Kafka: {} liked post {}", user.getUsername(), postId);

        return mapToDto(savedLike, user.getUsername());
    }

    @Transactional
    public void unlikePost(Long userId, Long postId) {
        log.info("User {} attempting to unlike post {}", userId, postId);

        Like like = likeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        likeRepository.delete(like);
        log.info("User {} successfully unliked post {}", userId, postId);

        // Publish PostUnlikedEvent to Kafka
        PostUnlikedEvent event = PostUnlikedEvent.builder()
                .postId(postId)
                .userId(userId)
                .eventTime(LocalDateTime.now())
                .build();

        postUnlikedKafkaTemplate.send("post-unliked-topic", event);
        log.info("Published PostUnlikedEvent to Kafka: user {} unliked post {}", userId, postId);
    }

    public List<LikeResponseDto> getLikesByPostId(Long postId) {
        log.info("Fetching likes for post: {}", postId);

        // Verify post exists
        getPostById(postId);

        List<Like> likes = likeRepository.findByPostId(postId);

        return likes.stream()
                .map(like -> {
                    UserResponseDto user = getUserById(like.getUserId());
                    return mapToDto(like, user.getUsername());
                })
                .collect(Collectors.toList());
    }

    public boolean hasUserLikedPost(Long userId, Long postId) {
        log.info("Checking if user {} liked post {}", userId, postId);
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }

    public long getLikeCount(Long postId) {
        log.info("Getting like count for post: {}", postId);
        return likeRepository.countByPostId(postId);
    }

    public List<LikeResponseDto> getLikesByUserId(Long userId) {
        log.info("Fetching posts liked by user: {}", userId);

        // Verify user exists
        UserResponseDto user = getUserById(userId);

        List<Like> likes = likeRepository.findByUserId(userId);

        return likes.stream()
                .map(like -> mapToDto(like, user.getUsername()))
                .collect(Collectors.toList());
    }


    // Helper method to call User Service
    private UserResponseDto getUserById(Long userId) {
        log.info("Calling User Service to get user with ID: {}", userId);

        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/users/{userId}", userId)
                .retrieve()
                .bodyToMono(UserResponseDto.class)
                .block();
    }

    // Helper method to call Post Service
    private PostResponseDto getPostById(Long postId) {
        log.info("Calling Post Service to get post with ID: {}", postId);

        return webClientBuilder.build()
                .get()
                .uri("http://post-service/api/posts/{postId}", postId)
                .retrieve()
                .bodyToMono(PostResponseDto.class)
                .block();
    }

    // Helper method to map Like to LikeResponseDto
    private LikeResponseDto mapToDto(Like like, String username) {
        return LikeResponseDto.builder()
                .id(like.getId())
                .userId(like.getUserId())
                .username(username)
                .postId(like.getPostId())
                .createdAt(like.getLikedAt())
                .build();
    }
}
