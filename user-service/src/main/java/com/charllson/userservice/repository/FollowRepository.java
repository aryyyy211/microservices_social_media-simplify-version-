package com.charllson.userservice.repository;

import com.charllson.userservice.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    //Find people who follow this user
    List<Follow> findByFollowingId(Long followingId);

    //Find people that the user is following
    List<Follow> findByFollowerId(Long followerId);

    //Check if follower follows following
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // Find specific follow relationship
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    //count follower
    long countByFollowerId(Long followerId);

    //count following
    long countByFollowingId(Long followingId);
}
