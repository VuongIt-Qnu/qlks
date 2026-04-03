package com.example.hotel.repository;

import com.example.hotel.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByBooking_Id(Long bookingId);

    List<Review> findByHotel_IdOrderByCreatedAtDesc(Long hotelId);
}
