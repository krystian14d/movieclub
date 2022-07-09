package pl.javastart.movieclub.domain.movie;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.javastart.movieclub.domain.exception.MovieNotFoundException;
import pl.javastart.movieclub.domain.genre.Genre;
import pl.javastart.movieclub.domain.genre.GenreRepository;
import pl.javastart.movieclub.domain.movie.dto.MovieDto;
import pl.javastart.movieclub.domain.movie.dto.MovieEditDto;
import pl.javastart.movieclub.domain.movie.dto.MovieSaveDto;
import pl.javastart.movieclub.storage.FileStorageService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final FileStorageService fileStorageService;

    public Page<MovieDto> findAllPagedPromotedMovies(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Movie> pagedMovie = movieRepository.findAllByPromotedIsTrue(pageable);
        Page<MovieDto> pagedMovieDto = pagedMovie
                .map(MovieDtoMapper::map);
        return pagedMovieDto;
    }

    public Optional<MovieDto> findMovieById(Long id) {
        return movieRepository.findById(id).map(MovieDtoMapper::map);
    }

    public List<MovieDto> findMoviesByGenreName(String genre) {
        return movieRepository.findAllByGenre_NameIgnoreCase(genre)
                .stream()
                .map(MovieDtoMapper::map)
                .toList();
    }

    public void addMovie(MovieSaveDto movieToSave) {
        Movie movie = new Movie();
        movie.setTitle(movieToSave.getTitle());
        movie.setOriginalTitle(movieToSave.getOriginalTitle());
        movie.setPromoted(movieToSave.isPromoted());
        movie.setReleaseYear(movieToSave.getReleaseYear());
        movie.setShortDescription(movieToSave.getShortDescription());
        movie.setDescription(movieToSave.getDescription());
        movie.setYoutubeTrailerId(movieToSave.getYoutubeTrailerId());
        Genre genre = genreRepository.findByNameIgnoreCase(movieToSave.getGenre()).orElseThrow();
        movie.setGenre(genre);
        if (movieToSave.getPoster() != null) {
            String savedFileName = fileStorageService.saveImage(movieToSave.getPoster());
            movie.setPoster(savedFileName);
        }
        movieRepository.save(movie);
    }

    public List<MovieDto> findTopMovies(int size) {
        Pageable page = Pageable.ofSize(size);
        return movieRepository.findTopByRating(page).stream()
                .map(MovieDtoMapper::map)
                .toList();
    }

    @Transactional
    public void updateMovie(Long movieId, MovieEditDto movieWithEdit) throws MovieNotFoundException {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() ->
                new MovieNotFoundException(String.format("Movie with ID %s not found", movieId)));
        movie.setTitle(movieWithEdit.getTitle());
        movie.setOriginalTitle(movieWithEdit.getOriginalTitle());
        movie.setPromoted(movieWithEdit.isPromoted());
        movie.setReleaseYear(movieWithEdit.getReleaseYear());
        movie.setShortDescription(movieWithEdit.getShortDescription());
        movie.setDescription(movieWithEdit.getDescription());
        movie.setYoutubeTrailerId(movieWithEdit.getYoutubeTrailerId());
        Genre genre = genreRepository.findByNameIgnoreCase(movieWithEdit.getGenre()).orElseThrow();
        movie.setGenre(genre);
        if (movieWithEdit.getPoster() != null) {
            String savedFileName = fileStorageService.saveImage(movieWithEdit.getPoster());
            movie.setPoster(savedFileName);
        }
        movieRepository.save(movie);
    }

    public void deleteMovie(Long id){
        movieRepository.deleteById(id);
    }


}

