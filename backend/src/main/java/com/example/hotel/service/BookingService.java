package com.example.hotel.service;

import com.example.hotel.dto.booking.BookingResponse;
import com.example.hotel.dto.booking.CreateBookingRequest;
import com.example.hotel.dto.room.AvailableRoomDto;
import com.example.hotel.entity.*;
import com.example.hotel.repository.BookingRepository;
import com.example.hotel.repository.HotelRuleRepository;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final OwnerHotelService ownerHotelService;
    private final HotelRuleRepository hotelRuleRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public BookingService(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            OwnerHotelService ownerHotelService,
            HotelRuleRepository hotelRuleRepository,
            UserRepository userRepository,
            NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.ownerHotelService = ownerHotelService;
        this.hotelRuleRepository = hotelRuleRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<AvailableRoomDto> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, Integer guests, Long hotelId) {
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("checkOut must be after checkIn");
        }
        List<Room> rooms = hotelId == null ? roomRepository.findAll() : roomRepository.findByHotel_Id(hotelId);
        return rooms.stream()
                .filter(r -> guestsFit(r, guests))
                .filter(r -> bookingRepository.countOverlapping(r.getId(), checkIn, checkOut) == 0)
                .map(this::toAvailableDto)
                .collect(Collectors.toList());
    }

    private boolean guestsFit(Room r, int guests) {
        int cap =
                r.getMaxGuests() != null
                        ? r.getMaxGuests()
                        : (r.getRoomType() != null ? r.getRoomType().getCapacity() : 0);
        return cap >= guests;
    }

    private AvailableRoomDto toAvailableDto(Room r) {
        AvailableRoomDto d = new AvailableRoomDto();
        d.setId(r.getId());
        d.setName(r.getName());
        d.setHotelId(r.getHotel() != null ? r.getHotel().getId() : null);
        d.setHotelName(r.getHotel() != null ? r.getHotel().getName() : null);
        if (r.getRoomType() != null) {
            d.setRoomTypeId(r.getRoomType().getId());
            d.setRoomTypeName(r.getRoomType().getName());
            d.setCapacity(r.getRoomType().getCapacity());
        }
        d.setPricePerNight(r.getPrice());
        d.setMaxGuests(r.getMaxGuests());
        return d;
    }

    @Transactional
    public BookingResponse createBooking(Long userId, CreateBookingRequest req) {
        Room room = roomRepository.findById(req.getRoomId()).orElseThrow();
        if (!guestsFit(room, req.getGuests())) {
            throw new IllegalArgumentException("Too many guests for this room");
        }
        if (!req.getCheckOut().isAfter(req.getCheckIn())) {
            throw new IllegalArgumentException("Invalid dates");
        }
        if (bookingRepository.countOverlapping(room.getId(), req.getCheckIn(), req.getCheckOut()) > 0) {
            throw new IllegalStateException("Room not available for these dates");
        }
        long nights = ChronoUnit.DAYS.between(req.getCheckIn(), req.getCheckOut());
        if (nights < 1) {
            nights = 1;
        }
        BigDecimal total =
                BigDecimal.valueOf(room.getPrice()).multiply(BigDecimal.valueOf(nights)).setScale(2, RoundingMode.HALF_UP);

        User customer = userRepository.findById(userId).orElseThrow();
        Booking b = new Booking();
        b.setRoom(room);
        b.setCustomer(customer);
        b.setHotel(room.getHotel());
        b.setStatus(BookingStatus.PENDING_PAYMENT);
        b.setStartDate(req.getCheckIn());
        b.setEndDate(req.getCheckOut());
        b.setGuestCount(req.getGuests());
        b.setTotalAmount(total);

        Payment p = new Payment();
        p.setBooking(b);
        p.setAmount(total);
        p.setStatus(PaymentStatus.PENDING);
        p.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        b.setPayment(p);

        Booking saved = bookingRepository.save(b);
        notificationService.sendBookingCreated(saved);
        return toDto(saved);
    }

    public BookingResponse getBooking(Long id, Long userId, String role) {
        Booking b = bookingRepository.findById(id).orElseThrow();
        assertCanView(b, userId, role);
        return toDto(b);
    }

    public List<BookingResponse> getBookingsByUserId(Long userId) {
        return bookingRepository.findByCustomer_IdOrderByStartDateDesc(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsForHotelOwner(Long ownerUserId) {
        List<Long> hotelIds = ownerHotelService.getHotelIdsByOwner(ownerUserId);
        return bookingRepository.findAll().stream()
                .filter(b -> b.getHotel() != null && hotelIds.contains(b.getHotel().getId()))
                .sorted((a, c) -> c.getStartDate().compareTo(a.getStartDate()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getAllBookingsAdmin() {
        return bookingRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private void assertCanView(Booking b, Long userId, String role) {
        if ("ADMIN".equals(role)) {
            return;
        }
        if ("OWNER".equals(role)) {
            List<Long> ids = ownerHotelService.getHotelIdsByOwner(userId);
            if (b.getHotel() != null && ids.contains(b.getHotel().getId())) {
                return;
            }
        }
        if (b.getCustomer() != null && b.getCustomer().getId().equals(userId)) {
            return;
        }
        throw new RuntimeException("Forbidden");
    }

    public void approveBooking(Long bookingId, String role, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (booking.getStatus() != BookingStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Booking cannot be approved in current status");
        }
        if ("OWNER".equals(role)) {
            Long hotelId = booking.getHotel().getId();
            List<Long> allowedHotelIds = ownerHotelService.getHotelIdsByOwner(userId);
            if (!allowedHotelIds.contains(hotelId)) {
                throw new RuntimeException("Bạn không có quyền duyệt booking này");
            }
        } else if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Forbidden");
        }
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        notificationService.sendBookingApproved(booking);
    }

    public void rejectBooking(Long bookingId, String role, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (booking.getStatus() != BookingStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Booking cannot be rejected in current status");
        }
        if ("OWNER".equals(role)) {
            Long hotelId = booking.getHotel().getId();
            List<Long> allowedHotelIds = ownerHotelService.getHotelIdsByOwner(userId);
            if (!allowedHotelIds.contains(hotelId)) {
                throw new RuntimeException("Forbidden");
            }
        } else if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Forbidden");
        }
        booking.setStatus(BookingStatus.REJECTED);
        if (booking.getPayment() != null) {
            booking.getPayment().setStatus(PaymentStatus.FAILED);
        }
        bookingRepository.save(booking);
        notificationService.sendBookingRejected(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, Long userId) {
        Booking b = bookingRepository.findById(bookingId).orElseThrow();
        if (b.getCustomer() == null || !b.getCustomer().getId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }
        if (b.getStatus() == BookingStatus.CANCELLED || b.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel");
        }
        BigDecimal penalty = BigDecimal.ZERO;
        List<HotelRule> rules = hotelRuleRepository.findByHotel_IdAndActiveTrueOrderByMinHoursBeforeCheckinAsc(b.getHotel().getId());
        LocalDateTime checkInAtStart = b.getStartDate().atStartOfDay();
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), checkInAtStart);
        if (b.getTotalAmount() != null) {
            for (HotelRule rule : rules) {
                if (hours < rule.getMinHoursBeforeCheckin()) {
                    penalty =
                            b.getTotalAmount()
                                    .multiply(rule.getPenaltyPercent())
                                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    break;
                }
            }
        }
        b.setPenaltyAmount(penalty);
        b.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(b);
        notificationService.sendBookingCancelled(b);
        return toDto(b);
    }

    public void markCompletedIfPastCheckout() {
        LocalDate today = LocalDate.now();
        for (Booking b : bookingRepository.findAll()) {
            if (b.getStatus() == BookingStatus.APPROVED && b.getEndDate() != null && !b.getEndDate().isAfter(today)) {
                b.setStatus(BookingStatus.COMPLETED);
                bookingRepository.save(b);
            }
        }
    }

    private BookingResponse toDto(Booking booking) {
        BookingResponse dto = new BookingResponse();
        dto.setId(booking.getId());
        dto.setRoomId(booking.getRoom() != null ? booking.getRoom().getId() : null);
        dto.setHotelId(booking.getHotel() != null ? booking.getHotel().getId() : null);
        dto.setUserId(booking.getCustomer() != null ? booking.getCustomer().getId() : null);
        dto.setStatus(booking.getStatus() != null ? booking.getStatus().name() : null);
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setGuestCount(booking.getGuestCount());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setPenaltyAmount(booking.getPenaltyAmount());
        dto.setPaymentId(booking.getPayment() != null ? booking.getPayment().getId() : null);
        return dto;
    }
}
