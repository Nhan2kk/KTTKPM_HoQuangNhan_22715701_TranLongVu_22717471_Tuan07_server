package fit.se.movieservice.dto;

import lombok.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieEvent implements Serializable {
    private int movieId;
    private String title;
    private Double price;
    private String eventType; // "CREATED", "UPDATED", "DELETED"
}