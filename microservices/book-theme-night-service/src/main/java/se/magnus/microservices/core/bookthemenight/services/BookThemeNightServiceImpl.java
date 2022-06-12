package se.magnus.microservices.core.bookthemenight;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.bookthemenight.*;
import se.magnus.util.http.ServiceUtil;
import java.util.List;
import se.magnus.util.exceptions.*;

@RestController
public class BookThemeNightServiceImpl implements BookThemeNightService {

	@Autowired
	private final ServiceUtil serviceUtil;
	
	public BookThemeNightServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}
	
	@Override
	public List<BookThemeNight> getBookThemeNights(int bookId) {
		if(bookId < 1) {
			throw new InvalidInputException("Invalid book id: " + bookId);
		}
		if(bookId == 13) {
			throw new NotFoundException("No book theme nights for provided bookId: " + bookId);
		}
		return new ArrayList();
	}

}
