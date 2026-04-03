package com.example.hotel.controller;

import com.example.hotel.dto.rule.HotelRuleDto;
import com.example.hotel.security.SecurityUtils;
import com.example.hotel.service.HotelRuleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels/{hotelId}/rules")
public class HotelRuleController {

    private final HotelRuleService hotelRuleService;

    public HotelRuleController(HotelRuleService hotelRuleService) {
        this.hotelRuleService = hotelRuleService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @GetMapping
    public List<HotelRuleDto> list(@PathVariable Long hotelId, @RequestParam(defaultValue = "false") boolean all) {
        if (all) {
            return hotelRuleService.listAllByHotelIncludingInactive(hotelId);
        }
        return hotelRuleService.listByHotel(hotelId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @PostMapping
    public HotelRuleDto create(@PathVariable Long hotelId, @RequestBody HotelRuleDto dto) {
        dto.setHotelId(hotelId);
        return hotelRuleService.create(dto, SecurityUtils.getCurrentRole(), SecurityUtils.getCurrentUserId());
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @PutMapping("/{ruleId}")
    public HotelRuleDto update(
            @PathVariable Long hotelId, @PathVariable Long ruleId, @RequestBody HotelRuleDto dto) {
        dto.setHotelId(hotelId);
        return hotelRuleService.update(ruleId, dto, SecurityUtils.getCurrentRole(), SecurityUtils.getCurrentUserId());
    }

    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @DeleteMapping("/{ruleId}")
    public void delete(@PathVariable Long hotelId, @PathVariable Long ruleId) {
        hotelRuleService.delete(ruleId, SecurityUtils.getCurrentRole(), SecurityUtils.getCurrentUserId());
    }
}
