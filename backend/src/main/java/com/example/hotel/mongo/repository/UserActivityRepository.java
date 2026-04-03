package com.example.hotel.mongo.repository;

import com.example.hotel.mongo.document.UserActivityDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserActivityRepository extends MongoRepository<UserActivityDocument, String> {
}
