package fit.se.paymentservice.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import fit.se.paymentservice.entity.Payment;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByBookingId(int bookingId);
    List<Payment> findByUserId(int userId);
    List<Payment> findByStatus(Payment.PaymentStatus status);
}
