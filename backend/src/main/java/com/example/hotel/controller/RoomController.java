package com.example.hotel.controller;

import com.example.hotel.dto.room.RoomDto;
import com.example.hotel.security.SecurityUtils;
import com.example.hotel.service.OwnerHotelService;
import com.example.hotel.service.RoomService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final OwnerHotelService ownerHotelService;

    public RoomController(RoomService roomService, OwnerHotelService ownerHotelService) {
        this.roomService = roomService;
        this.ownerHotelService = ownerHotelService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @GetMapping
    public List<RoomDto> getRooms() {
        String role = SecurityUtils.getCurrentRole();
        Long userId = SecurityUtils.getCurrentUserId();

        if ("ADMIN".equals(role)) {
            return roomService.getAllRooms();
        } else if ("OWNER".equals(role)) {
            var hotelIds = ownerHotelService.getHotelIdsByOwner(userId);
            return roomService.getRoomsByHotelIds(hotelIds);
        }
        throw new RuntimeException("Unauthorized");
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @PostMapping
    public RoomDto createRoom(@RequestBody RoomDto dto) {
        String role = SecurityUtils.getCurrentRole();
        Long userId = SecurityUtils.getCurrentUserId();
        return roomService.createRoom(dto, role, userId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @PutMapping("/{id}")
    public RoomDto updateRoom(@PathVariable Long id, @RequestBody RoomDto dto) {
        return roomService.updateRoom(id, dto, SecurityUtils.getCurrentRole(), SecurityUtils.getCurrentUserId());
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id, SecurityUtils.getCurrentRole(), SecurityUtils.getCurrentUserId());
    }
}
