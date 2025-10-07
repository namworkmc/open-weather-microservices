package org.sideprj.weatheranalyticsservice.repository;

import java.util.List;

import org.sideprj.weatheranalyticsservice.model.entity.OutboxEvent;
import org.sideprj.weatheranalyticsservice.model.enums.OutboxStatusEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends MongoRepository<OutboxEvent, String> {

    List<OutboxEvent> findAllByStatusOrderByCreatedAtAsc(OutboxStatusEnum status);
}
