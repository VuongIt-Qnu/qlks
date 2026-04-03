package com.example.hotel.controller;

import com.example.hotel.dto.room.RoomTypeDto;
import com.example.hotel.security.SecurityUtils;
import com.example.hotel.service.RoomTypeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room-types")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @GetMapping
    public List<RoomTypeDto> list(@RequestParam Long hotelId) {
        return roomTypeService.listByHotel(hotelId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @PostMapping
    public RoomTypeDto create(@RequestBody RoomTypeDto dto) {
        return roomTypeService.create(dto, SecurityUtils.getCurrentRole(), SecurityUtils.getCurrentUserId());
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @PutMapping("/{id}")
    public RoomTypeDto update(@PathVariable Long id, @RequestBody RoomTypeDto dto) {
        return roomTypeService.update(id, dto, SecurityUtils.getCurrentRole(), SecurityUtils.getCurrentUserId());
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        roomTypeService.delete(id, SecurityUtils.getCurrentRole(), SecurityUtils.getCurrentUserId());
    }
}
