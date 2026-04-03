package com.example.hotel.service;

import com.example.hotel.dto.room.RoomTypeDto;
import com.example.hotel.entity.Hotel;
import com.example.hotel.entity.RoomType;
import com.example.hotel.repository.HotelRepository;
import com.example.hotel.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;
    private final OwnerHotelService ownerHotelService;

    public RoomTypeService(
            RoomTypeRepository roomTypeRepository,
            HotelRepository hotelRepository,
            OwnerHotelService ownerHotelService) {
        this.roomTypeRepository = roomTypeRepository;
        this.hotelRepository = hotelRepository;
        this.ownerHotelService = ownerHotelService;
    }

    public List<RoomTypeDto> listByHotel(Long hotelId) {
        return roomTypeRepository.findByHotel_Id(hotelId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public RoomTypeDto create(RoomTypeDto dto, String role, Long userId) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId()).orElseThrow();
        assertHotelAccess(hotel.getId(), role, userId);
        RoomType rt = new RoomType();
        rt.setHotel(hotel);
        rt.setName(dto.getName());
        rt.setCapacity(dto.getCapacity());
        rt.setDescription(dto.getDescription());
        return toDto(roomTypeRepository.save(rt));
    }

    @Transactional
    public RoomTypeDto update(Long id, RoomTypeDto dto, String role, Long userId) {
        RoomType rt = roomTypeRepository.findById(id).orElseThrow();
        assertHotelAccess(rt.getHotel().getId(), role, userId);
        if (dto.getName() != null) {
            rt.setName(dto.getName());
        }
        if (dto.getCapacity() != null) {
            rt.setCapacity(dto.getCapacity());
        }
        if (dto.getDescription() != null) {
            rt.setDescription(dto.getDescription());
        }
        return toDto(roomTypeRepository.save(rt));
    }

    public void delete(Long id, String role, Long userId) {
        RoomType rt = roomTypeRepository.findById(id).orElseThrow();
        assertHotelAccess(rt.getHotel().getId(), role, userId);
        roomTypeRepository.delete(rt);
    }

    private void assertHotelAccess(Long hotelId, String role, Long userId) {
        if ("ADMIN".equals(role)) {
            return;
        }
        if ("OWNER".equals(role) && ownerHotelService.getHotelIdsByOwner(userId).contains(hotelId)) {
            return;
        }
        throw new RuntimeException("Forbidden");
    }

    private RoomTypeDto toDto(RoomType rt) {
        RoomTypeDto d = new RoomTypeDto();
        d.setId(rt.getId());
        d.setHotelId(rt.getHotel() != null ? rt.getHotel().getId() : null);
        d.setName(rt.getName());
        d.setCapacity(rt.getCapacity());
        d.setDescription(rt.getDescription());
        return d;
    }
}
