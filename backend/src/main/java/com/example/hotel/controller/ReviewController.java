package com.example.hotel.controller;

import com.example.hotel.dto.review.CreateReviewRequest;
import com.example.hotel.dto.review.ReviewDto;
import com.example.hotel.security.SecurityUtils;
import com.example.hotel.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/reviews")
    public ReviewDto create(@Valid @RequestBody CreateReviewRequest req) {
        return reviewService.create(SecurityUtils.getCurrentUserId(), req);
    }

    @GetMapping("/public/hotels/{hotelId}/reviews")
    public List<ReviewDto> listPublic(@PathVariable Long hotelId) {
        return reviewService.listByHotel(hotelId);
    }
}
