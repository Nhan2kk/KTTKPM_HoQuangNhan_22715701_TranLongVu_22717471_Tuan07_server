package fit.se.bookingservice.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private int userId;
    private int movieId;
    private List<String> seatNumbers;
    private Double totalPrice;
}
