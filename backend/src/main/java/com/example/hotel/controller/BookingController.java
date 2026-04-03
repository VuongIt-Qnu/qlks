package com.example.hotel.controller;

import com.example.hotel.security.SecurityUtils;
import com.example.hotel.service.BookingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @PostMapping("/{id}/approve")
    public void approveBooking(@PathVariable Long id) {
        String role = SecurityUtils.getCurrentRole();
        Long userId = SecurityUtils.getCurrentUserId();
        bookingService.approveBooking(id, role, userId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @PostMapping("/{id}/reject")
    public void rejectBooking(@PathVariable Long id) {
        bookingService.rejectBooking(id, SecurityUtils.getCurrentRole(), SecurityUtils.getCurrentUserId());
    }
}
