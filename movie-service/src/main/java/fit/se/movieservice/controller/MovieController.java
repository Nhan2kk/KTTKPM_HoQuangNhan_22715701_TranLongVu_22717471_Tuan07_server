package fit.se.movieservice.controller;

import fit.se.movieservice.dto.MovieRequest;
import fit.se.movieservice.entity.Movie;
import fit.se.movieservice.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    // ==================== READ ====================
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable String id) {
        return movieService.getMovieById(id)
                .map(movie -> ResponseEntity.ok(movie))
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== CREATE ====================
    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody MovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDuration(request.getDuration());
        movie.setGenre(request.getGenre());
        movie.setPoster(request.getPoster());
        movie.setPrice(request.getPrice());
        movie.setStatus(request.getStatus());
        movie.setReleaseDate(request.getReleaseDate());

        Movie savedMovie = movieService.saveMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovie);
    }

    // ==================== UPDATE ====================
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable String id,
                                             @RequestBody MovieRequest request) {
        try {
            Movie updatedMovie = movieService.updateMovie(id, request);
            return ResponseEntity.ok(updatedMovie);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== DELETE ====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.noContent().build();   // 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}