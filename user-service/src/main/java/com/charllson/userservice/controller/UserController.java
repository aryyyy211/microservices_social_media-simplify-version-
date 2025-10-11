package com.charllson.userservice.controller;


import com.charllson.userservice.dto.FollowDto;
import com.charllson.userservice.dto.UserProfileUpdateDto;
import com.charllson.userservice.dto.UserRegistrationDto;
import com.charllson.userservice.dto.UserResponseDto;
import com.charllson.userservice.service.FollowService;
import com.charllson.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final FollowService followService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        log.info("POST /api/users/register - Registering user: {}", userRegistrationDto.getUsername());
        UserResponseDto userResponseDto = userService.registerUser(userRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        log.info("GET /api/users/{} - Fetching user", userId);
        Object serverPort;
//        log.info("🔥 Instance on port {} handling request for user {}",
//                serverPort, userId);
        UserResponseDto userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        log.info("GET /api/users/username/{} - Fetching user", username);
        UserResponseDto userResponse = userService.getUserByUsername(username);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileUpdateDto userProfileUpdateDto) {
        log.info("PUT /api/users/{} - Updating user profile", userId);
        UserResponseDto userResponse = userService.updateUserProfile(userId, userProfileUpdateDto);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        log.info("DELETE /api/users/{} - Deleting user", userId);
        userService.deleteUser(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User has been deleted");
        return ResponseEntity.ok(response);

    }

    // ========== FOLLOW/UNFOLLOW ENDPOINTS ==========
    @PostMapping("/{userId}/follow/{targetUserId}")
    public ResponseEntity<FollowDto> followUser(
            @PathVariable Long userId,
            @PathVariable Long targetUserId) {
        log.info("POST /api/users/{}/follow/{} - Following user", userId, targetUserId);
        FollowDto followDto = followService.followUser(userId, targetUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(followDto);
    }

    @DeleteMapping("/{userId}/unfollow/{targetUserId}")
    public ResponseEntity<Map<String, String>> unfollowUser(
            @PathVariable Long userId,
            @PathVariable Long targetUserId) {
        log.info("DELETE /api/users/{}/unfollow/{} - Unfollowing user", userId, targetUserId);
        followService.unfollowUser(userId, targetUserId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully unfollowed user");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserResponseDto>> getFollowers(@PathVariable Long userId) {
        log.info("GET /api/users/{}/followers - Fetching followers", userId);
        List<UserResponseDto> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserResponseDto>> getFollowing(@PathVariable Long userId) {
        log.info("GET /api/users/{}/following - Fetching following", userId);
        List<UserResponseDto> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/{userId}/is-following/{targetUserId}")
    public ResponseEntity<Map<String, Boolean>> isFollowing(
            @PathVariable Long userId,
            @PathVariable Long targetUserId) {
        log.info("GET /api/users/{}/is-following/{} - Checking follow status", userId, targetUserId);
        boolean isFollowing = followService.isFollowing(userId, targetUserId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFollowing", isFollowing);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<Map<String, Long>> getUserStats(@PathVariable Long userId) {
        log.info("GET /api/users/{}/stats - Fetching user stats", userId);
        Map<String, Long> stats = new HashMap<>();
        stats.put("followersCount", followService.getFollowersCount(userId));
        stats.put("followingCount", followService.getFollowingCount(userId));
        return ResponseEntity.ok(stats);
    }

}
