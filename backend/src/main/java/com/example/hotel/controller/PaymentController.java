package com.example.hotel.controller;

import com.example.hotel.dto.payment.ConfirmPaymentRequest;
import com.example.hotel.dto.payment.PaymentResponse;
import com.example.hotel.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public PaymentResponse get(@PathVariable Long id) {
        return paymentService.getPaymentForUser(id);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/confirm")
    public PaymentResponse confirm(@PathVariable Long id, @Valid @RequestBody ConfirmPaymentRequest req) {
        return paymentService.confirmPayment(id, req);
    }
}
