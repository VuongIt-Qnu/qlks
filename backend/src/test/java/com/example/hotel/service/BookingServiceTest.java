package com.example.hotel.service;

import com.example.hotel.dto.booking.CreateBookingRequest;
import com.example.hotel.entity.*;
import com.example.hotel.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private OwnerHotelService ownerHotelService;
    @Mock
    private HotelRuleRepository hotelRuleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService =
                new BookingService(
                        bookingRepository,
                        roomRepository,
                        ownerHotelService,
                        hotelRuleRepository,
                        userRepository,
                        notificationService);
    }

    @Test
    void createBooking_setsPendingPaymentAndCreatesPayment() {
        RoomType rt = new RoomType();
        rt.setCapacity(2);
        Hotel h = new Hotel();
        h.setId(1L);
        Room room = new Room();
        room.setId(10L);
        room.setHotel(h);
        room.setRoomType(rt);
        room.setPrice(100_000d);
        User user = new User();
        user.setId(5L);
        when(roomRepository.findById(10L)).thenReturn(Optional.of(room));
        when(bookingRepository.countOverlapping(any(), any(), any())).thenReturn(0L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(inv -> {
                    Booking b = inv.getArgument(0);
                    b.setId(99L);
                    if (b.getPayment() != null) {
                        b.getPayment().setId(100L);
                    }
                    return b;
                });

        CreateBookingRequest req = new CreateBookingRequest();
        req.setRoomId(10L);
        req.setCheckIn(LocalDate.now().plusDays(1));
        req.setCheckOut(LocalDate.now().plusDays(3));
        req.setGuests(2);

        bookingService.createBooking(5L, req);

        ArgumentCaptor<Booking> cap = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(cap.capture());
        Booking saved = cap.getValue();
        assertThat(saved.getStatus()).isEqualTo(BookingStatus.PENDING_PAYMENT);
        assertThat(saved.getPayment()).isNotNull();
        assertThat(saved.getPayment().getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(saved.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(200_000));
    }
}
