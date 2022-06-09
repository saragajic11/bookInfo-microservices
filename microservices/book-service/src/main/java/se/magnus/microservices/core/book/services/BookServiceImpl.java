package se.magnus.microservices.core.book.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.book.*;
import se.magnus.util.http.ServiceUtil;

@RestController
public class BookServiceImpl implements BookService {

	private final ServiceUtil serviceUtil;
	
	@Autowired
	public BookServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}
	
	@Override
	public BookModel getBook(int bookId) {
		return new BookModel();
	}
	
}
