package fit.se.movieservice.service;

import fit.se.movieservice.config.RabbitMQConfig;
import fit.se.movieservice.dto.MovieEvent;
import fit.se.movieservice.dto.MovieRequest;
import fit.se.movieservice.entity.Movie;
import fit.se.movieservice.repository.MovieRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    public Movie saveMovie(Movie movie) {
        Movie savedMovie = movieRepository.save(movie);

        // Gửi event CREATED
        sendMovieEvent(savedMovie, "CREATED");

        return savedMovie;
    }

    @Transactional
    public Movie updateMovie(String id, MovieRequest request) {
        Movie existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));

        // Cập nhật thông tin
        existingMovie.setTitle(request.getTitle());
        existingMovie.setDescription(request.getDescription());
        existingMovie.setDuration(request.getDuration());
        existingMovie.setGenre(request.getGenre());
        existingMovie.setPoster(request.getPoster());
        existingMovie.setPrice(request.getPrice());
        existingMovie.setStatus(request.getStatus());
        existingMovie.setReleaseDate(request.getReleaseDate());

        Movie updatedMovie = movieRepository.save(existingMovie);

        // Gửi event UPDATED
        sendMovieEvent(updatedMovie, "UPDATED");

        return updatedMovie;
    }

    @Transactional
    public void deleteMovie(String id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));

        movieRepository.deleteById(id);

        // Gửi event DELETED
        MovieEvent event = new MovieEvent(id, movie.getTitle(), movie.getPrice(), "DELETED");
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);

        System.out.println(">>> Event DELETED đã bắn: " + movie.getTitle());
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieById(String id) {
        return movieRepository.findById(id);
    }

    // Hàm hỗ trợ gửi event
    private void sendMovieEvent(Movie movie, String eventType) {
        MovieEvent event = new MovieEvent(
                movie.getId(),
                movie.getTitle(),
                movie.getPrice(),
                eventType
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);
        System.out.println(">>> Event " + eventType + " đã bắn: " + movie.getTitle());
    }
}