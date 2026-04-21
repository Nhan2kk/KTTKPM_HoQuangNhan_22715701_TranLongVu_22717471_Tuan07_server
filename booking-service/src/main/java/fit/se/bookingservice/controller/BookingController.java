package fit.se.bookingservice.controller;

import fit.se.bookingservice.dto.BookingRequest;
import fit.se.bookingservice.dto.BookingResponse;
import fit.se.bookingservice.entity.Booking;
import fit.se.bookingservice.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // ==================== CREATE ====================
    /**
     * Create a new booking
     * POST /api/bookings
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        log.info("Received booking creation request from user: {}", request.getUserId());
        try {
            BookingResponse response = bookingService.createBooking(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== READ ====================
    /**
     * Get all bookings
     * GET /api/bookings
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        log.info("Fetching all bookings");
        try {
            List<BookingResponse> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Error fetching all bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get booking by ID
     * GET /api/bookings/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable int id) {
        log.info("Fetching booking with ID: {}", id);
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get bookings by user ID
     * GET /api/bookings/by-user/{userId}
     */
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserId(@PathVariable int userId) {
        log.info("Fetching bookings for user ID: {}", userId);
        try {
            List<BookingResponse> bookings = bookingService.getBookingsByUserId(userId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Error fetching user bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get bookings by movie ID
     * GET /api/bookings/movie/{movieId}
     */
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByMovieId(@PathVariable int movieId) {
        log.info("Fetching bookings for movie ID: {}", movieId);
        try {
            List<BookingResponse> bookings = bookingService.getBookingsByMovieId(movieId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Error fetching movie bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== UPDATE STATUS ====================
    /**
     * Update booking status (called by Payment Service)
     * PUT /api/bookings/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable int id,
            @RequestParam String status) {
        log.info("Updating booking ID: {} to status: {}", id, status);
        try {
            Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status.toUpperCase());
            BookingResponse response = bookingService.updateBookingStatus(id, bookingStatus);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid booking status: {}", status);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating booking status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
