package se.magnus.microservices.core.rating;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import se.magnus.microservices.core.rating.persistence.*;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
public class PersistenceTests {
	
	@Autowired
	private RatingRepository repository;

	private RatingEntity savedRatingEntity;

	@Before
	public void setupDb() {
		repository.deleteAll();

		RatingEntity entity = new RatingEntity(1, 1, "Author 1", 5);
		savedRatingEntity = repository.save(entity);

		assertEqualsRating(entity, savedRatingEntity);
	}

	@Test
	public void createRating() {

		RatingEntity newEntity = new RatingEntity(1, 2, "Author 2", 6);
		repository.save(newEntity);

		RatingEntity foundEntity = repository.findById(newEntity.getId()).get();
		assertEqualsRating(newEntity, foundEntity);

		assertEquals(2, repository.count());
	}
	
	@Test
	public void updateRating() {
		savedRatingEntity.setAuthor("author2");
		repository.save(savedRatingEntity);

		RatingEntity foundEntity = repository.findById(savedRatingEntity.getId()).get();
		assertEquals(1, (long) foundEntity.getVersion());
		assertEquals("author2", foundEntity.getAuthor());
	}

	@Test
	public void deleteRating() {
		repository.delete(savedRatingEntity);
		assertFalse(repository.existsById(savedRatingEntity.getId()));
	}

	@Test
	public void getByBookId() {
		List<RatingEntity> entityList = repository.findByBookId(savedRatingEntity.getBookId());

        assertThat(entityList, hasSize(1));
        assertEqualsRating(savedRatingEntity, entityList.get(0));
	}
	
	@Test(expected = DuplicateKeyException.class)
	public void duplicateError() {
		RatingEntity entity = new RatingEntity(1, 1, "author2", 6);
		repository.save(entity);
	}


	@Test
	public void optimisticLockError() {

		RatingEntity entity1 = repository.findById(savedRatingEntity.getId()).get();
		RatingEntity entity2 = repository.findById(savedRatingEntity.getId()).get();

		entity1.setAuthor("author2");
		repository.save(entity1);

		try {
			entity2.setAuthor("author3");
			repository.save(entity2);

			fail("Expected an OptimisticLockingFailureException");
		} catch (OptimisticLockingFailureException e) {
		}
		RatingEntity updatedEntity = repository.findById(savedRatingEntity.getId()).get();
		assertEquals(1, (int) updatedEntity.getVersion());
		assertEquals("author2", updatedEntity.getAuthor());
	}
	
	

	void assertEqualsRating(RatingEntity expectedEntity, RatingEntity actualEntity) {
		assertEquals(expectedEntity.getId(), actualEntity.getId());
		assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
		assertEquals(expectedEntity.getBookId(), actualEntity.getBookId());
		assertEquals(expectedEntity.getRatingId(), actualEntity.getRatingId());
		assertEquals(expectedEntity.getAuthor(), actualEntity.getAuthor());
		assertEquals(expectedEntity.getRating(), actualEntity.getRating());
	}
}
