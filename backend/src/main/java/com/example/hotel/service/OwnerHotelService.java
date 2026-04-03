package com.example.hotel.service;

import com.example.hotel.entity.Hotel;
import com.example.hotel.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerHotelService {

    private final HotelRepository hotelRepository;

    public OwnerHotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<Long> getHotelIdsByOwner(Long ownerId) {
        return hotelRepository.findIdsByOwnerId(ownerId);
    }
}
