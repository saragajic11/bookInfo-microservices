package se.magnus.api.core.bookthemenight;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.publisher.Flux;

import java.util.List;

public interface BookThemeNightService {

    /**
     * Sample usage: curl $HOST:$PORT/book-theme-night?bookId=1
     *
     * @param bookId
     * @return
     */
    @GetMapping(
        value    = "/book-theme-night",
        produces = "application/json")
    Flux<BookThemeNight> getBookThemeNights(@RequestParam(value = "bookId", required = true) int bookId);
    
	@PostMapping(
	        value    = "/book-theme-night",
	        consumes = "application/json",
	        produces = "application/json")
	BookThemeNight createBookThemeNight(@RequestBody BookThemeNight body);
	
	@DeleteMapping(value = "/book-theme-night")
    void deleteBookThemeNight(@RequestParam(value = "bookId", required = true)  int bookId);
}
