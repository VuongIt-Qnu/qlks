package com.example.hotel.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "hotel_rules")
public class HotelRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private String name;

    /** Hours before check-in; if cancel after this window, penalty applies */
    @Column(nullable = false)
    private Integer minHoursBeforeCheckin;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal penaltyPercent;

    @Column(nullable = false)
    private Boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
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
