package se.magnus.microservices.core.book.services;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.book.*;
import se.magnus.microservices.core.book.persistence.BookRepository;
import se.magnus.microservices.core.book.persistence.BookEntity;
import se.magnus.util.http.ServiceUtil;
import se.magnus.util.exceptions.*;
import org.springframework.dao.DuplicateKeyException;

import reactor.core.publisher.Mono;
import static reactor.core.publisher.Mono.error;

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
	public Mono<BookModel> getBook(int bookId) {
		if(bookId < 1) {
			throw new InvalidInputException("Invalid book id: " + bookId);
		}
		return repository.findByBookId(bookId)
				.switchIfEmpty(error(new NotFoundException("No book found for bookId: " + bookId)))
				.log()
				.map(e-> mapper.entityToApi(e))
				.map(e-> { e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
	}
	
	@Override
	public BookModel createBook(BookModel body) {
		if (body.getBookId() < 1) throw new InvalidInputException("Invalid bookId: " + body.getBookId());
		BookEntity entity = mapper.apiToEntity(body);
		Mono<BookModel> newEntity = repository.save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex -> new InvalidInputException("Duplicate key, bookId: " + body.getBookId()))
            .map(e -> mapper.entityToApi(e));;
        return newEntity.block();     
	}
	
    @Override
    public void deleteBook(int bookId) {
    	if (bookId < 1) throw new InvalidInputException("Invalid bookId: " + bookId);
    	
        LOG.debug("deleteBook: tries to delete an entity with bookId: {}", bookId);
        repository.findByBookId(bookId).log().map(e -> repository.delete(e)).flatMap(e -> e).block(); 
    }
}
