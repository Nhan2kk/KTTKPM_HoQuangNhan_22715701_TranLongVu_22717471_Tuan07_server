package fit.se.paymentservice.event;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event: Payment completed - sent to Notification Service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bookingId;
    private int userId;
    private int paymentId;
    private Double amount;
    private String status; // SUCCESS, FAILED
    private LocalDateTime paidAt;
    private String eventTimestamp;
}
