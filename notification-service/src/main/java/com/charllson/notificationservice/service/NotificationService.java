package com.charllson.notificationservice.service;

import com.charllson.notificationservice.dto.NotificationResponseDto;
import com.charllson.notificationservice.model.Notification;
import com.charllson.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(Long userId, String type, String message,
                                           Long relatedUserId, Long relatedPostId) {
        log.info("Creating notification for user {}: {}", userId, message);

        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .message(message)
                .isRead(false)
                .relatedUserId(relatedUserId)
                .relatedPostId(relatedPostId)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created with ID: {}", savedNotification.getId());

        return savedNotification;
    }

    public List<NotificationResponseDto> getNotificationsByUserId(Long userId) {
        log.info("Fetching notifications for user: {}", userId);
        List<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        return notifications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDto> getUnreadNotifications(Long userId) {
        log.info("Fetching unread notifications for user: {}", userId);
        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);

        return notifications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationResponseDto markAsRead(Long notificationId) {
        log.info("Marking notification {} as read", notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));

        notification.setIsRead(true);
        Notification updatedNotification = notificationRepository.save(notification);

        return mapToDto(updatedNotification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);

        notifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        log.info("Deleting notification: {}", notificationId);
        if (!notificationRepository.existsById(notificationId)) {
            throw new RuntimeException("Notification not found with ID: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    private NotificationResponseDto mapToDto(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .relatedUserId(notification.getRelatedUserId())
                .relatedPostId(notification.getRelatedPostId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
