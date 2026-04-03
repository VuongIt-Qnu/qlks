package com.example.hotel.service;

import com.example.hotel.mongo.document.UserActivityDocument;
import com.example.hotel.mongo.repository.UserActivityRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserActivityService {

    private final UserActivityRepository userActivityRepository;

    public UserActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public void log(Long userId, String activity, String detail) {
        UserActivityDocument d = new UserActivityDocument();
        d.setUserId(userId);
        d.setActivity(activity);
        d.setDetail(detail);
        d.setCreatedAt(Instant.now());
        userActivityRepository.save(d);
    }
}
