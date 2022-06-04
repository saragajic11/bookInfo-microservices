package se.magnus.api.core.comment;

import org.springframework.web.bind.annotation.GetMapping;
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
}
