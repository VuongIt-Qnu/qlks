package com.example.hotel.controller;

import com.example.hotel.dto.booking.BookingResponse;
import com.example.hotel.dto.booking.CreateBookingRequest;
import com.example.hotel.security.SecurityUtils;
import com.example.hotel.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/bookings")
public class UserBookingController {

    private final BookingService bookingService;

    public UserBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<BookingResponse> getMyBookings() {
        Long userId = SecurityUtils.getCurrentUserId();
        return bookingService.getBookingsByUserId(userId);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public BookingResponse create(@Valid @RequestBody CreateBookingRequest req) {
        return bookingService.createBooking(SecurityUtils.getCurrentUserId(), req);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public BookingResponse getOne(@PathVariable Long id) {
        return bookingService.getBooking(id, SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentRole());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id) {
        return bookingService.cancelBooking(id, SecurityUtils.getCurrentUserId());
    }
}
