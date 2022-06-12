package se.magnus.microservices.core.rating.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.rating.*;
import se.magnus.util.http.ServiceUtil;
import se.magnus.util.exceptions.*;

@RestController
public class RatingServiceImpl implements RatingService {

	private final ServiceUtil serviceUtil;
	
	@Autowired
	public RatingServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}
	
	@Override
	public List<Rating> getRatings(int bookId) {
		if(bookId < 1) {
			throw new InvalidInputException("Invalid book id: " + bookId);
		}
		if(bookId == 13) {
			throw new NotFoundException("No ratings with provided id: " + bookId);
		}
		return new ArrayList();
	}
	
}
