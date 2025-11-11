package org.sideprj.weatheranalyticsservice.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OutboxProcessor {

    private final OutboxService outboxService;

    @Scheduled(cron = "${outbox.processor.cron}")
    public void sendOutbox() {
        outboxService.sendOutboxEvents();
    }
}
