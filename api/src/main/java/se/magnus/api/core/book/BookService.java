package se.magnus.api.core.book;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


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
     BookModel getBook(@PathVariable int bookId);
    
	@PostMapping(
	        value    = "/book",
	        consumes = "application/json",
	        produces = "application/json")
	    BookModel createBook(@RequestBody BookModel body);
	
	@DeleteMapping(value = "/book/{bookId}}")
    void deleteBook(@PathVariable int bookId);
}
