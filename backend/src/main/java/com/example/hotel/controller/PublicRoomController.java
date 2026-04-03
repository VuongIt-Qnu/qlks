package com.example.hotel.controller;

import com.example.hotel.dto.room.AvailableRoomDto;
import com.example.hotel.security.SecurityUtils;
import com.example.hotel.service.BookingService;
import com.example.hotel.service.UserActivityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/public/rooms")
public class PublicRoomController {

    private final BookingService bookingService;
    private final UserActivityService userActivityService;

    public PublicRoomController(BookingService bookingService, UserActivityService userActivityService) {
        this.bookingService = bookingService;
        this.userActivityService = userActivityService;
    }

    @GetMapping("/available")
    public List<AvailableRoomDto> available(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "1") int guests,
            @RequestParam(required = false) Long hotelId) {
        Long uid = SecurityUtils.getCurrentUserId();
        if (uid != null) {
            userActivityService.log(
                    uid,
                    "SEARCH_ROOMS",
                    "checkIn=" + checkIn + ",checkOut=" + checkOut + ",guests=" + guests + ",hotelId=" + hotelId);
        }
        return bookingService.findAvailableRooms(checkIn, checkOut, guests, hotelId);
    }
}
