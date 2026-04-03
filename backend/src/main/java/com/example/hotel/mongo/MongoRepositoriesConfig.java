package com.example.hotel.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.example.hotel.mongo.repository")
public class MongoRepositoriesConfig {
}
