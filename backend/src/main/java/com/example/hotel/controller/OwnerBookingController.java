package com.example.hotel.controller;

import com.example.hotel.dto.booking.BookingResponse;
import com.example.hotel.security.SecurityUtils;
import com.example.hotel.service.BookingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/owner/bookings")
public class OwnerBookingController {

    private final BookingService bookingService;

    public OwnerBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping
    public List<BookingResponse> listForOwner() {
        return bookingService.getBookingsForHotelOwner(SecurityUtils.getCurrentUserId());
    }
}
