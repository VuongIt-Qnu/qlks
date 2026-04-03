package com.example.hotel.controller;

import com.example.hotel.dto.booking.BookingResponse;
import com.example.hotel.service.BookingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<BookingResponse> listAll() {
        return bookingService.getAllBookingsAdmin();
    }
}
