package se.magnus.microservices.core.book.services;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.book.*;
import se.magnus.microservices.core.book.persistence.*;
import se.magnus.util.http.ServiceUtil;
import se.magnus.util.exceptions.*;
import org.springframework.dao.DuplicateKeyException;

@RestController
public class BookServiceImpl implements BookService {

	private final ServiceUtil serviceUtil;
	private final BookRepository repository;
	private final BookMapper mapper;
	private static final Logger LOG = LoggerFactory.getLogger(BookServiceImpl.class);
	
	@Autowired
	public BookServiceImpl(BookRepository repository, BookMapper mapper, ServiceUtil serviceUtil) {
		this.repository = repository;
		this.mapper = mapper;
		this.serviceUtil = serviceUtil;
	}
	
	@Override
	public BookModel getBook(int bookId) {
		if(bookId < 1) {
			throw new InvalidInputException("Invalid book id: " + bookId);
		}
	    BookEntity entity = repository.findByBookId(bookId)
	    		.orElseThrow(() -> new NotFoundException("No book found for bookId: " + bookId));
	    BookModel response = mapper.entityToApi(entity);
	    response.setServiceAddress(serviceUtil.getServiceAddress());
	    return response;
	}
	
	@Override
	public BookModel createBook(BookModel body) {
		try {
			BookEntity entity = mapper.apiToEntity(body);
			BookEntity newEntity = repository.save(entity);
			return mapper.entityToApi(newEntity);
		} catch(DuplicateKeyException dke) {
			throw new InvalidInputException("Duplicate key, book id: " + body.getBookId());
		}
	}
	
    @Override
    public void deleteBook(int bookId) {
        LOG.debug("deleteBook: tries to delete an entity with bookId: {}", bookId);
        repository.findByBookId(bookId).ifPresent(e -> repository.delete(e));
    }
}
