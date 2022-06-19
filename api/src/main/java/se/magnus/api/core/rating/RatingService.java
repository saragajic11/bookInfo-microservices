package se.magnus.api.core.rating;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RatingService {

    /**
     * Sample usage: curl $HOST:$PORT/rating?bookId=1
     *
     * @param bookId
     * @return
     */
    @GetMapping(
        value    = "/rating",
        produces = "application/json")
    List<Rating> getRatings(@RequestParam(value = "bookId", required = true) int bookId);
    
	@PostMapping(
	        value    = "/rating",
	        consumes = "application/json",
	        produces = "application/json")
	Rating createRating(@RequestBody Rating body);
	
	@DeleteMapping(value = "/rating")
    void deleteRating(@RequestParam(value = "bookId", required = true)  int bookId);
}