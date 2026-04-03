package com.example.hotel.repository;

import com.example.hotel.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    List<RoomType> findByHotel_Id(Long hotelId);
}
