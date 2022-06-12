package se.magnus.microservices.core.book.services;

import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.book.*;
import se.magnus.util.http.ServiceUtil;
import se.magnus.util.exceptions.*;

@RestController
public class BookServiceImpl implements BookService {

	private final ServiceUtil serviceUtil;
	
	@Autowired
	public BookServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}
	
	@Override
	public BookModel getBook(int bookId) {
		if(bookId < 1) {
			throw new InvalidInputException("Invalid book id: " + bookId);
		}
		if(bookId == 13) {
			throw new NotFoundException("No book with provided id: " + bookId);
		}
		return new BookModel(bookId, "Book 1", Date.valueOf("2021-08-13"), "Language 1", serviceUtil.getServiceAddress());
	}
	
}
