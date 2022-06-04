package se.magnus.api.core.book;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface BookService {

    /**
     * Sample usage: curl $HOST:$PORT/book/1
     *
     * @param productId
     * @return the product, if found, else null
     */
    @GetMapping(
        value    = "/book/{bookId}",
        produces = "application/json")
     Book getBook(@PathVariable int bookId);
}
