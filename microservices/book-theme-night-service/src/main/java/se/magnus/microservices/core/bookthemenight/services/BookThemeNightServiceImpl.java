package se.magnus.microservices.core.bookthemenight.services;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.bookthemenight.*;
import se.magnus.microservices.core.bookthemenight.persistence.*;
import se.magnus.util.http.ServiceUtil;
import se.magnus.util.exceptions.*;
import org.springframework.dao.DuplicateKeyException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class BookThemeNightServiceImpl implements BookThemeNightService {

	private final ServiceUtil serviceUtil;
	private BookThemeNightRepository repository;
	private BookThemeNightMapper mapper;
	private static final Logger LOG = LoggerFactory.getLogger(BookThemeNightServiceImpl.class);
	

	@Autowired
	public BookThemeNightServiceImpl(ServiceUtil serviceUtil, BookThemeNightRepository repository, BookThemeNightMapper mapper) {
		this.serviceUtil = serviceUtil;
		this.repository = repository;
		this.mapper = mapper;
	}
	
	@Override
	public Flux<BookThemeNight> getBookThemeNights(int bookId) {
		if (bookId < 1)
			throw new InvalidInputException("Invalid bookId: " + bookId);
		
		return repository.findByBookId(bookId)
				.log()
				.map(e -> mapper.entityToApi(e))
				.map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
	}
	
	@Override
	public BookThemeNight createBookThemeNight(BookThemeNight body) {
		if (body.getBookThemeNightId() < 1) throw new InvalidInputException("Invalid bookThemeNightId: " + body.getBookThemeNightId());
		BookThemeNightEntity entity = mapper.apiToEntity(body);
        Mono<BookThemeNight> newEntity = repository.save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex -> new InvalidInputException("Duplicate key,book Id: " + body.getBookId() + ", Book Theme Night Id:" + body.getBookThemeNightId()))
            .map(e -> mapper.entityToApi(e));
        return newEntity.block();
	}
	
	@Override
	public void deleteBookThemeNight(int bookId) {
		if (bookId < 1)
			throw new InvalidInputException("Invalid bookId: " + bookId);

        LOG.debug("deleteBookThemeNight: tries to delete bookThemeNights for the book with bookId: {}", bookId);
        repository.deleteAll(repository.findByBookId(bookId)).block();    
	}

}
