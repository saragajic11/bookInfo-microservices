package se.magnus.api.core.bookthemenight;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestParam;

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
    List<BookThemeNight> getBookThemeNights(@RequestParam(value = "bookId", required = true) int bookId);
}
