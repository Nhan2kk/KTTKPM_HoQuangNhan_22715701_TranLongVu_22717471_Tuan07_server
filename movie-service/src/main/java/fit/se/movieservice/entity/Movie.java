package fit.se.movieservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    private Integer duration;
    private String genre;
    private String poster;
    private Double price;
    private String status; // "NOW_SHOWING" hoặc "COMING_SOON"
    private LocalDate releaseDate;
}