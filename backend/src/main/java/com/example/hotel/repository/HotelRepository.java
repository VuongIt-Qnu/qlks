package com.example.hotel.repository;

import com.example.hotel.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("select h.id from Hotel h where h.owner.id = :ownerId")
    List<Long> findIdsByOwnerId(@Param("ownerId") Long ownerId);
}
