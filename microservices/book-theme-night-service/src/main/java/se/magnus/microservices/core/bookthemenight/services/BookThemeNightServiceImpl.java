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
	public List<BookThemeNight> getBookThemeNights(int bookId) {
		if (bookId < 1)
			throw new InvalidInputException("Invalid bookId: " + bookId);

		List<BookThemeNightEntity> entityList = repository.findByBookId(bookId);
		List<BookThemeNight> list = mapper.entityListToApiList(entityList);
		list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

		LOG.debug("getBookThemeNights: response size: {}", list.size());

		return list;
	}
	
	
	@Override
	public BookThemeNight createBookThemeNight(BookThemeNight body) {
		try {
			BookThemeNightEntity entity = mapper.apiToEntity(body);
			BookThemeNightEntity newEntity = repository.save(entity);
			LOG.debug("createBookThemeNight: created a bookThemeNight entity: {}/{}", body.getBookId(),
						body.getBookThemeNightId());
			return mapper.entityToApi(newEntity);

			} catch (DuplicateKeyException dke) {
				throw new InvalidInputException("Duplicate key, BookId Id: " + body.getBookId() + ", BookThemeNight Id:"
						+ body.getBookThemeNightId());
			}
		}
	
	@Override
	public void deleteBookThemeNight(int bookId) {
		LOG.debug("deleteBookThemeNights: tries to delete bookThemeNight for the book with bookId: {}",
				bookId);
		repository.deleteAll(repository.findByBookId(bookId));
	}

}
