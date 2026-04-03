package com.example.hotel.service;

import com.example.hotel.dto.rule.HotelRuleDto;
import com.example.hotel.entity.Hotel;
import com.example.hotel.entity.HotelRule;
import com.example.hotel.repository.HotelRepository;
import com.example.hotel.repository.HotelRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelRuleService {

    private final HotelRuleRepository hotelRuleRepository;
    private final HotelRepository hotelRepository;
    private final OwnerHotelService ownerHotelService;

    public HotelRuleService(
            HotelRuleRepository hotelRuleRepository,
            HotelRepository hotelRepository,
            OwnerHotelService ownerHotelService) {
        this.hotelRuleRepository = hotelRuleRepository;
        this.hotelRepository = hotelRepository;
        this.ownerHotelService = ownerHotelService;
    }

    public List<HotelRuleDto> listByHotel(Long hotelId) {
        return hotelRuleRepository.findByHotel_IdAndActiveTrueOrderByMinHoursBeforeCheckinAsc(hotelId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<HotelRuleDto> listAllByHotelIncludingInactive(Long hotelId) {
        return hotelRuleRepository.findByHotel_Id(hotelId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public HotelRuleDto create(HotelRuleDto dto, String role, Long userId) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId()).orElseThrow();
        assertHotelAccess(hotel.getId(), role, userId);
        HotelRule r = new HotelRule();
        r.setHotel(hotel);
        r.setName(dto.getName());
        r.setMinHoursBeforeCheckin(dto.getMinHoursBeforeCheckin());
        r.setPenaltyPercent(dto.getPenaltyPercent());
        r.setActive(dto.getActive() != null ? dto.getActive() : true);
        return toDto(hotelRuleRepository.save(r));
    }

    @Transactional
    public HotelRuleDto update(Long id, HotelRuleDto dto, String role, Long userId) {
        HotelRule r = hotelRuleRepository.findById(id).orElseThrow();
        if (dto.getHotelId() != null && !dto.getHotelId().equals(r.getHotel().getId())) {
            throw new IllegalArgumentException("Hotel mismatch");
        }
        assertHotelAccess(r.getHotel().getId(), role, userId);
        if (dto.getName() != null) {
            r.setName(dto.getName());
        }
        if (dto.getMinHoursBeforeCheckin() != null) {
            r.setMinHoursBeforeCheckin(dto.getMinHoursBeforeCheckin());
        }
        if (dto.getPenaltyPercent() != null) {
            r.setPenaltyPercent(dto.getPenaltyPercent());
        }
        if (dto.getActive() != null) {
            r.setActive(dto.getActive());
        }
        return toDto(hotelRuleRepository.save(r));
    }

    public void delete(Long id, String role, Long userId) {
        HotelRule r = hotelRuleRepository.findById(id).orElseThrow();
        assertHotelAccess(r.getHotel().getId(), role, userId);
        hotelRuleRepository.delete(r);
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

    private HotelRuleDto toDto(HotelRule r) {
        HotelRuleDto d = new HotelRuleDto();
        d.setId(r.getId());
        d.setHotelId(r.getHotel() != null ? r.getHotel().getId() : null);
        d.setName(r.getName());
        d.setMinHoursBeforeCheckin(r.getMinHoursBeforeCheckin());
        d.setPenaltyPercent(r.getPenaltyPercent());
        d.setActive(r.getActive());
        return d;
    }
}
