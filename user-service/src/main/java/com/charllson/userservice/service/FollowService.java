package com.charllson.userservice.service;

import com.charllson.userservice.dto.FollowDto;
import com.charllson.userservice.dto.UserResponseDto;
import com.charllson.userservice.event.UserFollowedEvent;
import com.charllson.userservice.model.Follow;
import com.charllson.userservice.model.User;
import com.charllson.userservice.repository.FollowRepository;
import com.charllson.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserFollowedEvent> kafkaTemplate;

    @Transactional
    public FollowDto followUser(Long followerId, Long followingId) {
        log.info("User {} attempting to follow user {}", followerId, followingId);

        //validate users exists
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found with ID: " + followerId));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User to follow not found with ID: " + followingId));

        // Check if user is trying to follow themselves
        if (followerId.equals(followingId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        // Check if already following
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new RuntimeException("Already following this user");
        }

        //Create a follow relationship
        Follow follow = Follow.builder().followerId(followerId).followingId(followingId).build();

        // Save follower
        Follow savedFollow = followRepository.save(follow);
        log.info("User {} successfully followed user {}", followerId, followingId);

        //publish kafka event
        UserFollowedEvent event = UserFollowedEvent.builder()
                .followerId(followerId)
                .followingId(followingId)
                .followerUsername(follower.getUsername())
                .followingUsername(following.getUsername())
                .eventTime(LocalDateTime.now())
                .build();

        kafkaTemplate.send("user-followed-topic", event);
        log.info("Published UserFollowedEvent to Kafka: {} followed {}",
                follower.getUsername(), following.getUsername());

        return mapToFollowDto(savedFollow, follower.getUsername(), following.getUsername());

    }

    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        log.info("User {} attempting to unfollow user {}", followerId, followingId);

        Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new RuntimeException("Follow relationship not found"));

        followRepository.delete(follow);
        log.info("User {} successfully unfollowed user {}", followerId, followingId);
    }

    public List<UserResponseDto> getFollowers(Long userId) {
        log.info("Fetching followers for user {}", userId);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        List<Follow> follows = followRepository.findByFollowingId(userId);

        return follows.stream()
                .map(follow -> {
                    User follower = userRepository.findById(follow.getFollowerId())
                            .orElseThrow(() -> new RuntimeException("Follower not found"));
                    return mapToUserResponseDto(follower);
                })
                .collect(Collectors.toList());
    }


    public List<UserResponseDto> getFollowing(Long userId) {
        log.info("Fetching users that user {} follows", userId);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        List<Follow> follows = followRepository.findByFollowerId(userId);

        return follows.stream()
                .map(follow -> {
                    User following = userRepository.findById(follow.getFollowingId())
                            .orElseThrow(() -> new RuntimeException("Following user not found"));
                    return mapToUserResponseDto(following);
                })
                .collect(Collectors.toList());
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        log.info("Checking if user {} follows user {}", followerId, followingId);
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public long getFollowersCount(Long userId) {
        return followRepository.countByFollowingId(userId);
    }

    public long getFollowingCount(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    private FollowDto mapToFollowDto(Follow follow, String followerUsername, String followingUsername) {
        return FollowDto.builder()
                .id(follow.getId())
                .followerId(follow.getFollowerId())
                .followingId(follow.getFollowingId())
                .followerUsername(followerUsername)
                .followingUsername(followingUsername)
                .createdAt(follow.getCreatedAt())
                .build();
    }

    private UserResponseDto mapToUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
