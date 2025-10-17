package com.charllson.interactionservice.controller;

import com.charllson.interactionservice.dto.LikeResponseDto;
import com.charllson.interactionservice.service.InteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
@Slf4j
public class InteractionController {
    private final InteractionService interactionService;

    @PostMapping("/like")
    public ResponseEntity<LikeResponseDto> likePost(
            @RequestParam Long userId,
            @RequestParam Long postId
    ) {
        log.info("POST /api/interactions/like - User {} liking post {}", userId, postId);
        LikeResponseDto likeResponse = interactionService.likePost(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(likeResponse);
    }

    @DeleteMapping("/unlike")
    public ResponseEntity<Map<String, String>> unlikePost(
            @RequestParam Long userId,
            @RequestParam Long postId) {
        log.info("DELETE /api/interactions/unlike - User {} unliking post {}", userId, postId);
        interactionService.unlikePost(userId, postId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post unliked successfully");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/post/{postId}/likes")
    public ResponseEntity<List<LikeResponseDto>> getLikesByPost(@PathVariable Long postId) {
        log.info("GET /api/interactions/post/{}/likes - Fetching likes", postId);
        List<LikeResponseDto> likes = interactionService.getLikesByPostId(postId);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Map<String, Long>> getLikeCount(@PathVariable Long postId) {
        log.info("GET /api/interactions/post/{}/count - Getting like count", postId);
        long count = interactionService.getLikeCount(postId);
        Map<String, Long> response = new HashMap<>();
        response.put("likeCount", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> hasUserLikedPost(
            @RequestParam Long userId,
            @RequestParam Long postId) {
        log.info("GET /api/interactions/check - Checking if user {} liked post {}", userId, postId);
        boolean hasLiked = interactionService.hasUserLikedPost(userId, postId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasLiked", hasLiked);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/likes")
    public ResponseEntity<List<LikeResponseDto>> getLikesByUser(@PathVariable Long userId) {
        log.info("GET /api/interactions/user/{}/likes - Fetching posts liked by user", userId);
        List<LikeResponseDto> likes = interactionService.getLikesByUserId(userId);
        return ResponseEntity.ok(likes);
    }
}
