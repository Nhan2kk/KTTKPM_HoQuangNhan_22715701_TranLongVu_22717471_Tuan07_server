package fit.se.bookingservice.service;

import fit.se.bookingservice.dto.BookingRequest;
import fit.se.bookingservice.dto.BookingResponse;
import fit.se.bookingservice.entity.Booking;
import fit.se.bookingservice.event.BookingCreatedEvent;
import fit.se.bookingservice.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EventPublisher eventPublisher;

    /**
     * Create a new booking
     */
    public BookingResponse createBooking(BookingRequest request) {
        try {
            log.info("Creating new booking for user ID: {}, movie ID: {}", request.getUserId(), request.getMovieId());

            // Create booking entity
            Booking booking = new Booking();
            booking.setUserId(request.getUserId());
            booking.setMovieId(request.getMovieId());
            booking.setSeatNumbers(request.getSeatNumbers());
            booking.setTotalPrice(request.getTotalPrice());
            booking.setStatus(Booking.BookingStatus.PENDING);

            // Save to database
            Booking savedBooking = bookingRepository.save(booking);
            log.info("Booking created successfully! Booking ID: {}, Status: PENDING", savedBooking.getId());

            // Publish BOOKING_CREATED event
            publishBookingEvent(savedBooking);

            return BookingResponse.fromEntity(savedBooking);

        } catch (Exception e) {
            log.error("Error creating booking", e);
            throw new RuntimeException("Failed to create booking", e);
        }
    }

    /**
     * Get all bookings
     */
    public List<BookingResponse> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll()
                .stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get booking by ID
     */
    public Optional<BookingResponse> getBookingById(int id) {
        log.info("Fetching booking by ID: {}", id);
        return bookingRepository.findById(id)
                .map(BookingResponse::fromEntity);
    }

    /**
     * Get bookings by user ID
     */
    public List<BookingResponse> getBookingsByUserId(int userId) {
        log.info("Fetching bookings for user ID: {}", userId);
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get bookings by movie ID
     */
    public List<BookingResponse> getBookingsByMovieId(int movieId) {
        log.info("Fetching bookings for movie ID: {}", movieId);
        return bookingRepository.findByMovieId(movieId)
                .stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update booking status (for Payment Service)
     */
    public BookingResponse updateBookingStatus(int bookingId, Booking.BookingStatus newStatus) {
        log.info("Updating booking ID: {} to status: {}", bookingId, newStatus);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        booking.setStatus(newStatus);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking status updated! ID: {}, New Status: {}", bookingId, newStatus);
        return BookingResponse.fromEntity(updatedBooking);
    }

    /**
     * Publish BOOKING_CREATED event to message broker
     */
    private void publishBookingEvent(Booking booking) {
        BookingCreatedEvent event = new BookingCreatedEvent();
        event.setBookingId(booking.getId());
        event.setUserId(booking.getUserId());
        event.setMovieId(booking.getMovieId());
        event.setSeatNumbers(booking.getSeatNumbers());
        event.setTotalPrice(booking.getTotalPrice());
        event.setCreatedAt(booking.getCreatedAt());
        event.setEventTimestamp(LocalDateTime.now().toString());

        eventPublisher.publishBookingCreatedEvent(event);
    }
}
