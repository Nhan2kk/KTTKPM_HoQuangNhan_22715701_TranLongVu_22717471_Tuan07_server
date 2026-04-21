package fit.se.bookingservice.dto;

import fit.se.bookingservice.entity.Booking;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private int id;
    private int userId;
    private int movieId;
    private List<String> seatNumbers;
    private Double totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BookingResponse fromEntity(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setUserId(booking.getUserId());
        response.setMovieId(booking.getMovieId());
        response.setSeatNumbers(booking.getSeatNumbers());
        response.setTotalPrice(booking.getTotalPrice());
        response.setStatus(booking.getStatus().toString());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());
        return response;
    }
}
