package com.charllson.postservice.controller;

import com.charllson.postservice.dto.PostCreateDto;
import com.charllson.postservice.dto.PostResponseDto;
import com.charllson.postservice.dto.PostUpdateDto;
import com.charllson.postservice.service.PostService;
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
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostCreateDto postCreateDto) {
        log.info("POST /api/posts - Creating post for user: {}", postCreateDto.getUserId());
        PostResponseDto postResponseDto = postService.createPost(postCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponseDto);

    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long postId) {
        log.info("GET /api/posts/{} - Fetching post", postId);
        PostResponseDto postResponse = postService.getPostById(postId);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponseDto>> getPostsByUserId(@PathVariable Long userId) {
        log.info("GET /api/posts/user/{} - Fetching posts for user", userId);
        List<PostResponseDto> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        log.info("GET /api/posts - Fetching all posts");
        List<PostResponseDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateDto updateDto) {
        log.info("PUT /api/posts/{} - Updating post", postId);
        PostResponseDto postResponse = postService.updatePost(postId, updateDto);
        return ResponseEntity.ok(postResponse);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId) {
        log.info("DELETE /api/posts/{} - Deleting post", postId);
        postService.deletePost(postId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post deleted successfully");
        return ResponseEntity.ok(response);
    }

}
