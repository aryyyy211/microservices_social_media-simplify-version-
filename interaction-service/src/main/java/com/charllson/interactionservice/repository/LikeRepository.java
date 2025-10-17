package com.charllson.interactionservice.repository;

import com.charllson.interactionservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Long> {
    // Check if user already liked a post
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    // Find specific like
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);

    // Get all likes for a post
    List<Like> findByPostId(Long postId);

    // Get all likes by a user
    List<Like> findByUserId(Long userId);

    // Count likes for a post
    long countByPostId(Long postId);
}
