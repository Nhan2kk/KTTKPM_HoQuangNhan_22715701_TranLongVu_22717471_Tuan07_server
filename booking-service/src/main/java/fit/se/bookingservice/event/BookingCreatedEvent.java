package fit.se.bookingservice.event;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bookingId;
    private int userId;
    private int movieId;
    private List<String> seatNumbers;
    private Double totalPrice;
    private LocalDateTime createdAt;
    private String eventTimestamp;
}
