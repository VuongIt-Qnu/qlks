package com.example.hotel.service;

import com.example.hotel.dto.payment.PaymentResponse;
import com.example.hotel.dto.payment.ConfirmPaymentRequest;
import com.example.hotel.entity.Booking;
import com.example.hotel.entity.BookingStatus;
import com.example.hotel.entity.Payment;
import com.example.hotel.entity.PaymentStatus;
import com.example.hotel.repository.BookingRepository;
import com.example.hotel.repository.PaymentRepository;
import com.example.hotel.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.notificationService = notificationService;
    }

    public PaymentResponse getPaymentForUser(Long paymentId) {
        Payment p = paymentRepository.findById(paymentId).orElseThrow();
        Booking b = p.getBooking();
        Long uid = SecurityUtils.getCurrentUserId();
        if (b.getCustomer() == null || !b.getCustomer().getId().equals(uid)) {
            throw new RuntimeException("Forbidden");
        }
        return toDto(p);
    }

    @Transactional
    public PaymentResponse confirmPayment(Long paymentId, ConfirmPaymentRequest req) {
        Payment p = paymentRepository.findById(paymentId).orElseThrow();
        Booking b = p.getBooking();
        Long uid = SecurityUtils.getCurrentUserId();
        if (b.getCustomer() == null || !b.getCustomer().getId().equals(uid)) {
            throw new RuntimeException("Forbidden");
        }
        if (p.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment already processed");
        }
        if (Boolean.TRUE.equals(req.getSuccess())) {
            p.setStatus(PaymentStatus.SUCCESS);
            b.setStatus(BookingStatus.PENDING_APPROVAL);
        } else {
            p.setStatus(PaymentStatus.FAILED);
            b.setStatus(BookingStatus.PENDING_PAYMENT);
        }
        paymentRepository.save(p);
        bookingRepository.save(b);
        return toDto(p);
    }

    private PaymentResponse toDto(Payment p) {
        PaymentResponse d = new PaymentResponse();
        d.setId(p.getId());
        d.setBookingId(p.getBooking() != null ? p.getBooking().getId() : null);
        d.setAmount(p.getAmount());
        d.setStatus(p.getStatus() != null ? p.getStatus().name() : null);
        d.setInvoiceNumber(p.getInvoiceNumber());
        d.setCreatedAt(p.getCreatedAt());
        return d;
    }
}
