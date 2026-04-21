package fit.se.bookingservice.repository;

import fit.se.bookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(int userId);
    List<Booking> findByMovieId(int movieId);
    List<Booking> findByStatus(Booking.BookingStatus status);
}
