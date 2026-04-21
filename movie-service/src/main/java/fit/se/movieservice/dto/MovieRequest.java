package fit.se.movieservice.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MovieRequest {
    private String title;
    private String description;
    private Integer duration;
    private String genre;
    private String poster;
    private Double price;
    private String status;
    private LocalDate releaseDate;
}