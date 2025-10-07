package org.sideprj.weatheranalyticsservice.service;

public interface OutboxService {

    void createOutboxMessage(String source, Object key, Object message);

    void sendOutboxEvents();
}
