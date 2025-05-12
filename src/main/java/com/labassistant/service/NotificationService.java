package com.labassistant.service;

import com.labassistant.model.Notification;
import com.labassistant.model.User;
import com.labassistant.repository.NotificationRepository;
import com.labassistant.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public NotificationService(NotificationRepository notificationRepository, 
                              UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Notification createNotification(Long userId, Notification.NotificationType type, 
                                          String message, Long relatedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRelatedEntityId(relatedId);
        
        Notification saved = notificationRepository.save(notification);
        sendNotificationToUser(userId, saved);
        return saved;
    }

    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        emitters.put(userId, emitter);
        
        emitter.onCompletion(() -> {
            logger.info("SSE connection closed for user {}", userId);
            emitters.remove(userId);
        });
        
        emitter.onTimeout(() -> {
            logger.warn("SSE timeout for user {}", userId);
            emitters.remove(userId);
        });
        
        return emitter;
    }

    private void sendNotificationToUser(Long userId, Notification notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notification));
            } catch (IOException e) {
                logger.error("SSE send error for user {}: {}", userId, e.getMessage());
                emitters.remove(userId);
            }
        }
    }
}