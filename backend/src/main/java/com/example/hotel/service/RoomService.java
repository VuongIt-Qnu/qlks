package com.example.hotel.service;

import com.example.hotel.dto.room.RoomDto;
import com.example.hotel.entity.Hotel;
import com.example.hotel.entity.Room;
import com.example.hotel.entity.RoomType;
import com.example.hotel.repository.HotelRepository;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final OwnerHotelService ownerHotelService;

    public RoomService(
            RoomRepository roomRepository,
            HotelRepository hotelRepository,
            RoomTypeRepository roomTypeRepository,
            OwnerHotelService ownerHotelService) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.ownerHotelService = ownerHotelService;
    }

    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<RoomDto> getRoomsByHotelIds(List<Long> hotelIds) {
        return roomRepository.findByHotelIdIn(hotelIds).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public RoomDto createRoom(RoomDto dto, String role, Long userId) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId()).orElseThrow();
        assertHotelAccess(hotel, role, userId);
        RoomType rt = roomTypeRepository.findById(dto.getRoomTypeId()).orElseThrow();
        if (!rt.getHotel().getId().equals(hotel.getId())) {
            throw new IllegalArgumentException("Room type does not belong to hotel");
        }
        Room room = new Room();
        room.setName(dto.getName());
        room.setPrice(dto.getPrice());
        room.setHotel(hotel);
        room.setRoomType(rt);
        room.setMaxGuests(dto.getMaxGuests());
        return toDto(roomRepository.save(room));
    }

    @Transactional
    public RoomDto updateRoom(Long id, RoomDto dto, String role, Long userId) {
        Room room = roomRepository.findById(id).orElseThrow();
        assertHotelAccess(room.getHotel(), role, userId);
        if (dto.getName() != null) {
            room.setName(dto.getName());
        }
        if (dto.getPrice() != null) {
            room.setPrice(dto.getPrice());
        }
        if (dto.getMaxGuests() != null) {
            room.setMaxGuests(dto.getMaxGuests());
        }
        if (dto.getRoomTypeId() != null) {
            RoomType rt = roomTypeRepository.findById(dto.getRoomTypeId()).orElseThrow();
            if (!rt.getHotel().getId().equals(room.getHotel().getId())) {
                throw new IllegalArgumentException("Invalid room type");
            }
            room.setRoomType(rt);
        }
        return toDto(roomRepository.save(room));
    }

    public void deleteRoom(Long id, String role, Long userId) {
        Room room = roomRepository.findById(id).orElseThrow();
        assertHotelAccess(room.getHotel(), role, userId);
        roomRepository.delete(room);
    }

    private void assertHotelAccess(Hotel hotel, String role, Long userId) {
        if ("ADMIN".equals(role)) {
            return;
        }
        if ("OWNER".equals(role)) {
            List<Long> ids = ownerHotelService.getHotelIdsByOwner(userId);
            if (hotel != null && ids.contains(hotel.getId())) {
                return;
            }
        }
        throw new RuntimeException("Forbidden");
    }

    private RoomDto toDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setPrice(room.getPrice());
        dto.setHotelId(room.getHotel() != null ? room.getHotel().getId() : null);
        dto.setMaxGuests(room.getMaxGuests());
        if (room.getRoomType() != null) {
            dto.setRoomTypeId(room.getRoomType().getId());
            dto.setType(room.getRoomType().getName());
        }
        return dto;
    }
}
