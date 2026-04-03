package com.example.hotel.repository;

import com.example.hotel.entity.HotelRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRuleRepository extends JpaRepository<HotelRule, Long> {
    List<HotelRule> findByHotel_IdAndActiveTrueOrderByMinHoursBeforeCheckinAsc(Long hotelId);

    List<HotelRule> findByHotel_Id(Long hotelId);
}
