package se.magnus.api.core.comment;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CommentService {

    /**
     * Sample usage: curl $HOST:$PORT/comment?bookId=1
     *
     * @param bookId
     * @return
     */
    @GetMapping(
        value    = "/comment",
        produces = "application/json")
    List<Comment> getComments(@RequestParam(value = "bookId", required = true) int bookId);
    
	@PostMapping(
	        value    = "/comment",
	        consumes = "application/json",
	        produces = "application/json")
	Comment createComment(@RequestBody Comment body);
	
	@DeleteMapping(value = "/comment")
    void deleteComment(@RequestParam(value = "bookId", required = true)  int bookId);
}
