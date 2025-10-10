package com.charllson.userservice.service;

import com.charllson.userservice.dto.UserProfileUpdateDto;
import com.charllson.userservice.dto.UserRegistrationDto;
import com.charllson.userservice.dto.UserResponseDto;
import com.charllson.userservice.model.User;
import com.charllson.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto userRegistrationDto) {
        log.info("Registering new user: {}", userRegistrationDto.getUsername());

        //check if user with email already exist
        if (userRepository.existsByEmail(userRegistrationDto.getEmail())) {
            log.info("User with email {} already exists", userRegistrationDto.getEmail());
            throw new RuntimeException("User with email " + userRegistrationDto.getEmail() + " already exists");
        }
        //check if username already exist
        if (userRepository.existsByUsername(userRegistrationDto.getUsername())) {
            throw new RuntimeException("User with username " + userRegistrationDto.getUsername() + " already exists");
        }

        // Creat a new user
        User user = User.builder()
                .username(userRegistrationDto.getUsername())
                .email(userRegistrationDto.getEmail())
                .password(userRegistrationDto.getPassword())
                .bio(userRegistrationDto.getBio())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return mapToResponseDto(savedUser);

    }

    @Transactional
    public UserResponseDto updateUserProfile(Long userId, UserProfileUpdateDto userProfileUpdateDto) {
        log.info("Updating user: {}", userProfileUpdateDto.getUsername());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        //update field if provided
        if (userProfileUpdateDto.getUsername() != null && !userProfileUpdateDto.getUsername().isEmpty()) {
            // Check if new username is already taken by another user
            if (userRepository.existsByUsername(userProfileUpdateDto.getUsername())
                    && !user.getUsername().equals(userProfileUpdateDto.getUsername())) {
                throw new RuntimeException("User with the username you are trying to update already exist!");
            }

            user.setUsername(userProfileUpdateDto.getUsername());
        }
        if (userProfileUpdateDto.getBio() != null) {
            user.setBio(userProfileUpdateDto.getBio());
        }

        if (userProfileUpdateDto.getProfileImageUrl() != null) {
            user.setProfileImageUrl(userProfileUpdateDto.getProfileImageUrl());
        }

        User updatedUser = userRepository.save(user);
        log.info("User profile updated successfully for ID: {}", userId);

        return mapToResponseDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deleting user: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User with ID " + userId + " not found");
        }
        userRepository.deleteById(userId);
        log.info("User deleted successfully for ID: {}", userId);
    }

    public UserResponseDto getUserByUsername(String username) {
        log.info("Getting user by username: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return mapToResponseDto(user);
    }

    public UserResponseDto getUserById(Long userId) {
        log.info("Fetching user by ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return mapToResponseDto(user);
    }

    public UserResponseDto getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return mapToResponseDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    //Helper method
    private UserResponseDto mapToResponseDto(User user) {
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
