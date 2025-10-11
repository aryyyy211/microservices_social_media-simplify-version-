package com.charllson.postservice.service;

import com.charllson.postservice.dto.PostCreateDto;
import com.charllson.postservice.dto.PostResponseDto;
import com.charllson.postservice.dto.PostUpdateDto;
import com.charllson.postservice.dto.UserResponseDto;
import com.charllson.postservice.event.PostCreatedEvent;
import com.charllson.postservice.event.PostDeletedEvent;
import com.charllson.postservice.model.Post;
import com.charllson.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, PostCreatedEvent> postCreatedKafkaTemplate;
    private final KafkaTemplate<String, PostDeletedEvent> postDeletedKafkaTemplate;

    @Transactional
    public PostResponseDto createPost(PostCreateDto postCreateDto) {
        log.info("Creating post for user: {}", postCreateDto.getUserId());

        //Get user info from user service
        UserResponseDto user = getUserById(postCreateDto.getUserId());
        log.info("Successfully got user details from user service: {}", user);

        //Create Post
        Post post = Post.builder()
                .userId(postCreateDto.getUserId())
                .content(postCreateDto.getImageUrl())
                .imageUrl(postCreateDto.getImageUrl())
                .build();

        Post savedPost = postRepository.save(post);
        log.info("Post created with ID: {}", savedPost.getId());

        // Publish PostCreatedEvent to Kafka
        PostCreatedEvent event = PostCreatedEvent.builder()
                .postId(savedPost.getId())
                .userId(savedPost.getUserId())
                .username(user.getUsername())
                .content(savedPost.getContent())
                .imageUrl(savedPost.getImageUrl())
                .eventTime(LocalDateTime.now())
                .build();

        postCreatedKafkaTemplate.send("post-created-topic", event);
        log.info("Published PostCreatedEvent to Kafka for post ID: {}", savedPost.getId());

        return mapToResponseDto(savedPost, user.getUsername());
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostUpdateDto updateDto) {
        log.info("Updating post with ID: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        // Update fields if provided
        if (updateDto.getContent() != null && !updateDto.getContent().isEmpty()) {
            post.setContent(updateDto.getContent());
        }

        if (updateDto.getImageUrl() != null) {
            post.setImageUrl(updateDto.getImageUrl());
        }

        Post updatedPost = postRepository.save(post);
        log.info("Post updated successfully with ID: {}", postId);

        UserResponseDto user = getUserById(updatedPost.getUserId());
        return mapToResponseDto(updatedPost, user.getUsername());
    }


    @Transactional
    public void deletePost(Long postId) {
        log.info("Deleting post with ID: {}", postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
        postRepository.delete(post);
        log.info("Post deleted successfully with ID: {}", postId);

        // Publish PostDeletedEvent to Kafka
        PostDeletedEvent event = PostDeletedEvent.builder()
                .postId(postId)
                .userId(post.getUserId())
                .eventTime(LocalDateTime.now())
                .build();

        postDeletedKafkaTemplate.send("post-deleted-topic", event);
        log.info("Published PostDeletedEvent to Kafka for post ID: {}", postId);
    }

    public PostResponseDto getPostById(Long postId) {
        log.info("Fetching post by ID: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        // Get user info
        UserResponseDto user = getUserById(post.getUserId());

        return mapToResponseDto(post, user.getUsername());
    }

    public List<PostResponseDto> getPostsByUserId(Long userId) {
        log.info("Fetching posts for user: {}", userId);

        // Verify user exists
        getUserById(userId);

        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // Get user info once
        UserResponseDto user = getUserById(userId);

        return posts.stream()
                .map(post -> mapToResponseDto(post, user.getUsername()))
                .collect(Collectors.toList());
    }

    public List<PostResponseDto> getAllPosts() {
        log.info("Fetching all posts");
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        return posts.stream()
                .map(post -> {
                    UserResponseDto user = getUserById(post.getUserId());
                    return mapToResponseDto(post, user.getUsername());
                })
                .collect(Collectors.toList());
    }


    private UserResponseDto getUserById(Long userId) {
        log.info("Calling User Service to get user with ID: {}", userId);

        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/users/{userId}", userId)
                .retrieve()
                .bodyToMono(UserResponseDto.class)
                .block(); // ensure synchronous communication

    }

    private PostResponseDto mapToResponseDto(Post post, String username) {
        return PostResponseDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .username(username)
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
