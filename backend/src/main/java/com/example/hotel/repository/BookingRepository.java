package com.example.hotel.repository;

import com.example.hotel.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomer_IdOrderByStartDateDesc(Long userId);

    List<Booking> findByHotelIdOrderByStartDateDesc(Long hotelId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.room.id = :roomId AND b.status NOT IN ('CANCELLED','REJECTED') AND b.startDate < :endDate AND b.endDate > :startDate")
    long countOverlapping(@Param("roomId") Long roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
