package se.magnus.api.composite.book;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface BookCompositeService {

    /**
     * Sample usage: curl $HOST:$PORT/book-composite/1
     *
     * @param bookId
     * @return the composite book info, if found, else null
     */
    @GetMapping(
        value    = "/book-composite/{bookId}",
        produces = "application/json")
    BookAggregate getBook(@PathVariable int bookId);
}
