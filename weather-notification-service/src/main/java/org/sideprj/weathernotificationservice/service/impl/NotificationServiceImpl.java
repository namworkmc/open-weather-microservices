package org.sideprj.weathernotificationservice.service.impl;

import org.sideprj.weathernotificationservice.entity.Notification;
import org.sideprj.weathernotificationservice.repository.NotificationRepository;
import org.sideprj.weathernotificationservice.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Throwable.class)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public <T> void saveNotification(T message) {
        var notification = new Notification<T>();
        notification.setMessage(message);
        notificationRepository.save(notification);
    }
}
