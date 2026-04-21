package fit.se.userservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequest {
    private String title;
    private String description;
    private Integer duration;
    private String genre;
    private String poster;
    private Double price;
    private String status;
    private LocalDate releaseDate;
}