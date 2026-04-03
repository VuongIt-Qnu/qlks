package com.example.hotel.mongo.repository;

import com.example.hotel.mongo.document.AuditLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends MongoRepository<AuditLogDocument, String> {
}
