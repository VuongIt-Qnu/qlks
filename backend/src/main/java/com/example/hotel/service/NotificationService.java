package com.example.hotel.service;

import com.example.hotel.entity.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromMail;

    public NotificationService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingCreated(Booking b) {
        String body = "Booking #" + b.getId() + " created. Total: " + b.getTotalAmount();
        sendEmail(getEmail(b), "Booking created", body);
    }

    public void sendBookingApproved(Booking b) {
        sendEmail(getEmail(b), "Booking approved", "Booking #" + b.getId() + " approved.");
    }

    public void sendBookingRejected(Booking b) {
        sendEmail(getEmail(b), "Booking rejected", "Booking #" + b.getId() + " rejected.");
    }

    public void sendBookingCancelled(Booking b) {
        sendEmail(getEmail(b), "Booking cancelled", "Booking #" + b.getId() + " cancelled. Penalty: " + b.getPenaltyAmount());
    }

    private String getEmail(Booking b) {
        return b.getCustomer() != null ? b.getCustomer().getEmail() : null;
    }

    private void sendEmail(String to, String subject, String text) {
        log.info("[EMAIL] to={} subject={} body={}", to, subject, text);
        if (to == null || mailSender == null || fromMail == null || fromMail.isBlank()) {
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromMail);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
        } catch (Exception e) {
            log.warn("Mail send failed: {}", e.getMessage());
        }
    }
}
