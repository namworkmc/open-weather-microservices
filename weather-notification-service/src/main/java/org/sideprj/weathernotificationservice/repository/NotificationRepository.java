package org.sideprj.weathernotificationservice.repository;

import org.sideprj.weathernotificationservice.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification<?>, String> {
}
