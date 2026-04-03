package com.example.hotel.dto.payment;

import jakarta.validation.constraints.NotNull;

public class ConfirmPaymentRequest {
    @NotNull
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
