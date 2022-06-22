package se.magnus.microservices.core.book;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import se.magnus.microservices.core.book.persistence.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Date;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

import reactor.test.StepVerifier;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {
    
    @Autowired
    private BookRepository repository;

	private BookEntity savedBookEntity;
	private static final Logger LOG = LoggerFactory.getLogger(PersistenceTests.class);

	@Before
	public void setupDb() {
		StepVerifier.create(repository.deleteAll()).verifyComplete();

		BookEntity entity = new BookEntity(1, "name1", new Date() , "language 1");
		StepVerifier.create(repository.save(entity)).expectNextMatches(createdEntity -> {
			savedBookEntity = createdEntity;
			return areBookEqual(entity, savedBookEntity);
		}).verifyComplete();
	}

	@Test
	public void createBook() {
		
		BookEntity newEntity = new BookEntity(2, "name2", new Date(), "language 2");
        StepVerifier.create(repository.save(newEntity)).expectNextMatches(createdEntity -> newEntity.getBookId() == createdEntity.getBookId()).verifyComplete();
        StepVerifier.create(repository.findById(newEntity.getId())).expectNextMatches(foundEntity -> areBookEqual(newEntity, foundEntity)).verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2l).verifyComplete();
	}

	@Test
	public void updateBook() {
		savedBookEntity.setName("name2");

		StepVerifier.create(repository.save(savedBookEntity))
				.expectNextMatches(updatedEntity -> updatedEntity.getName().equals("name2"))
				.verifyComplete();

		StepVerifier.create(repository.findById(savedBookEntity.getId())).expectNextMatches(
				foundEntity -> foundEntity.getVersion() == 1 && foundEntity.getName().equals("name2"))
				.verifyComplete();
	}

	@Test
	public void deleteBook() {
		StepVerifier.create(repository.delete(savedBookEntity)).verifyComplete();
		StepVerifier.create(repository.existsById(savedBookEntity.getId())).expectNext(false).verifyComplete();
	}

	@Test
	public void getByBookId() {

		StepVerifier.create(repository.findByBookId(savedBookEntity.getBookId()))
				.expectNextMatches(foundEntity -> areBookEqual(savedBookEntity, foundEntity)).verifyComplete();
	}

	@Test
	public void optimisticLockError() {
		
    	BookEntity entity1 = repository.findById(savedBookEntity.getId()).block();
    	BookEntity entity2 = repository.findById(savedBookEntity.getId()).block();

		entity1.setName("n1");
		repository.save(entity1).block();

		StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

		StepVerifier.create(repository.findById(savedBookEntity.getId()))
				.expectNextMatches(foundEntity -> foundEntity.getVersion() == 1 && foundEntity.getName().equals("n1"))
				.verifyComplete();
	}

	private boolean areBookEqual(BookEntity expectedEntity, BookEntity actualEntity) {
		return (expectedEntity.getId().equals(actualEntity.getId())) &&
		(expectedEntity.getVersion() == actualEntity.getVersion()) &&
		(expectedEntity.getBookId() == actualEntity.getBookId()) &&
		(expectedEntity.getName().equals(actualEntity.getName()) &&
		(expectedEntity.getReleaseDate().equals(actualEntity.getReleaseDate())) &&
		(expectedEntity.getLanguage().equals(actualEntity.getLanguage())));
	}

}
