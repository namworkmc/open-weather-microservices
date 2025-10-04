package org.sideprj.weathernotificationservice.service;

public interface NotificationService {

    <T> void saveNotification(T message);
}
