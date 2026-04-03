package com.example.hotel.service;

import com.example.hotel.dto.review.CreateReviewRequest;
import com.example.hotel.dto.review.ReviewDto;
import com.example.hotel.entity.Booking;
import com.example.hotel.entity.BookingStatus;
import com.example.hotel.entity.Review;
import com.example.hotel.repository.BookingRepository;
import com.example.hotel.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public ReviewService(ReviewRepository reviewRepository, BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ReviewDto create(Long userId, CreateReviewRequest req) {
        Booking b = bookingRepository.findById(req.getBookingId()).orElseThrow();
        if (b.getCustomer() == null || !b.getCustomer().getId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }
        if (b.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalStateException("Only completed bookings can be reviewed");
        }
        if (reviewRepository.findByBooking_Id(b.getId()).isPresent()) {
            throw new IllegalStateException("Review already exists");
        }
        Review r = new Review();
        r.setBooking(b);
        r.setUser(b.getCustomer());
        r.setHotel(b.getHotel());
        r.setRating(req.getRating());
        r.setComment(req.getComment());
        return toDto(reviewRepository.save(r));
    }

    public List<ReviewDto> listByHotel(Long hotelId) {
        return reviewRepository.findByHotel_IdOrderByCreatedAtDesc(hotelId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ReviewDto toDto(Review r) {
        ReviewDto d = new ReviewDto();
        d.setId(r.getId());
        d.setBookingId(r.getBooking() != null ? r.getBooking().getId() : null);
        d.setHotelId(r.getHotel() != null ? r.getHotel().getId() : null);
        d.setHotelName(r.getHotel() != null ? r.getHotel().getName() : null);
        d.setRating(r.getRating());
        d.setComment(r.getComment());
        d.setCreatedAt(r.getCreatedAt());
        return d;
    }
}
