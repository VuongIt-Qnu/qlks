package com.example.hotel.mongo.repository;

import com.example.hotel.mongo.document.RequestLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RequestLogRepository extends MongoRepository<RequestLogDocument, String> {
}
