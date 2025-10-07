package org.sideprj.weatheranalyticsservice.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OutboxProcessor {

    private final OutboxService outboxService;

    @Scheduled(cron = "${outbox.processor.cron}")
    public void sendOutbox() {
        outboxService.sendOutboxEvents();
    }
}
