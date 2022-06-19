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
import se.magnus.microservices.core.rating.persistence.*;

import org.springframework.dao.DataIntegrityViolationException;

@RestController
public class RatingServiceImpl implements RatingService {

	private final ServiceUtil serviceUtil;
    private static final Logger LOG = LoggerFactory.getLogger(RatingServiceImpl.class);
    private RatingRepository repository;
    private RatingMapper mapper;
	
	@Autowired
	public RatingServiceImpl(ServiceUtil serviceUtil, RatingRepository repository, RatingMapper mapper) {
		this.serviceUtil = serviceUtil;
		this.repository = repository;
		this.mapper = mapper;
	}
	
	@Override
	public List<Rating> getRatings(int bookId) {
		if (bookId < 1)
			throw new InvalidInputException("Invalid bookId: " + bookId);

		List<RatingEntity> entityList = repository.findByBookId(bookId);
		List<Rating> list = mapper.entityListToApiList(entityList);
		list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

		LOG.debug("getRating: response size: {}", list.size());

		return list;
	}
	
	@Override
	public Rating createRating(Rating body) {
		try {
			RatingEntity entity = mapper.apiToEntity(body);
			RatingEntity newEntity = repository.save(entity);

			LOG.debug("createRating: created a rating entity: {}/{}", body.getBookId(),
					body.getRatingId());
			return mapper.entityToApi(newEntity);

		} catch (DataIntegrityViolationException dive) {
			throw new InvalidInputException("Duplicate key, book Id: " + body.getBookId() + ", Rating Id:"
					+ body.getRatingId());
        }
	}
	
	@Override
	public void deleteRating(int bookId) {
		LOG.debug("deleteRating: tries to delete rating for the book with bookId: {}",
				bookId);
		repository.deleteAll(repository.findByBookId(bookId));
	}
	
}
