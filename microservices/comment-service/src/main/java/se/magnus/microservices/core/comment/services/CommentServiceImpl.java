package se.magnus.microservices.core.comment.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.comment.*;
import se.magnus.util.http.ServiceUtil;
import se.magnus.util.exceptions.*;

@RestController
public class CommentServiceImpl implements CommentService {

	private final ServiceUtil serviceUtil;
	
	@Autowired
	public CommentServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}
	
	@Override
	public List<Comment> getComments(int bookId) {
		if(bookId < 1) {
			throw new InvalidInputException("Invalid book id: " + bookId);
		}
		if(bookId == 13) {
			throw new NotFoundException("No comments with provided id: " + bookId);
		}
		return new ArrayList();
	}
	
}
