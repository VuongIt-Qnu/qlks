package com.example.hotel.dto.rule;

import java.math.BigDecimal;

public class HotelRuleDto {
    private Long id;
    private Long hotelId;
    private String name;
    private Integer minHoursBeforeCheckin;
    private BigDecimal penaltyPercent;
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMinHoursBeforeCheckin() {
        return minHoursBeforeCheckin;
    }

    public void setMinHoursBeforeCheckin(Integer minHoursBeforeCheckin) {
        this.minHoursBeforeCheckin = minHoursBeforeCheckin;
    }

    public BigDecimal getPenaltyPercent() {
        return penaltyPercent;
    }

    public void setPenaltyPercent(BigDecimal penaltyPercent) {
        this.penaltyPercent = penaltyPercent;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
