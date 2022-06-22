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
import reactor.core.scheduler.Scheduler;
import reactor.core.publisher.Flux;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import static java.util.logging.Level.FINE;

@RestController
public class RatingServiceImpl implements RatingService {

	private final ServiceUtil serviceUtil;
    private static final Logger LOG = LoggerFactory.getLogger(RatingServiceImpl.class);
    private RatingRepository repository;
    private RatingMapper mapper;
    private final Scheduler scheduler;
	
	@Autowired
	public RatingServiceImpl(Scheduler scheduler, ServiceUtil serviceUtil, RatingRepository repository, RatingMapper mapper) {
		this.scheduler = scheduler;
		this.serviceUtil = serviceUtil;
		this.repository = repository;
		this.mapper = mapper;
	}
	
	@Override
	public Flux<Rating> getRatings(int bookId) {
        if (bookId < 1) throw new InvalidInputException("Invalid bookId: " + bookId);

        LOG.info("Will get raatings for book with id={}", bookId);

        return asyncFlux(() -> Flux.fromIterable(getByBookId(bookId))).log(null, FINE);
	}
	
	protected List<Rating> getByBookId(int bookId) {

        List<RatingEntity> entityList = repository.findByBookId(bookId);
        List<Rating> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getRatings: response size: {}", list.size());

        return list;
    }

	
	@Override
	public Rating createRating(Rating body) {
		if (body.getBookId() < 1) throw new InvalidInputException("Invalid bookId: " + body.getBookId());

        try {
            RatingEntity entity = mapper.apiToEntity(body);
            RatingEntity newEntity = repository.save(entity);

            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, book Id: " + body.getBookId() + ", Rating Id:" + body.getRatingId());
        }
	}
	
	@Override
	public void deleteRating(int bookId) {
		LOG.debug("deleteRating: tries to delete rating for the book with bookId: {}",
				bookId);
		repository.deleteAll(repository.findByBookId(bookId));
	}
	
	private <T> Flux<T> asyncFlux(Supplier<Publisher<T>> publisherSupplier) {
        return Flux.defer(publisherSupplier).subscribeOn(scheduler);
    }
	
}
