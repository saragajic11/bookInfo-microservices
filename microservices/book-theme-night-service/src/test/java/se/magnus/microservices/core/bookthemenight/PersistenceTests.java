package se.magnus.microservices.core.bookthemenight;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import static org.hamcrest.Matchers.hasSize;

import se.magnus.microservices.core.bookthemenight.persistence.*;
import java.util.List;


import static org.junit.Assert.*;
import java.util.Date;
@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {

	@Autowired
	private BookThemeNightRepository repository;

	private BookThemeNightEntity savedBookThemeNightEntity;
	
	@Before
	public void setupDb() {
		repository.deleteAll();

		BookThemeNightEntity entity = new BookThemeNightEntity(1, 1, "name1", new Date(), "location1");
		savedBookThemeNightEntity = repository.save(entity);

		assertEqualsBookThemeNight(entity, savedBookThemeNightEntity);
	}

	@Test
	public void createBookThemeNight() {

		BookThemeNightEntity newEntity = new BookThemeNightEntity(1, 2, "name2", new Date(), "location2");
		repository.save(newEntity);

		BookThemeNightEntity foundEntity = repository.findById(newEntity.getId()).get();
		assertEqualsBookThemeNight(newEntity, foundEntity);

		assertEquals(2, repository.count());
	}
	
	@Test
	public void updateBookThemeNight() {
		savedBookThemeNightEntity.setName("name2");
		repository.save(savedBookThemeNightEntity);

		BookThemeNightEntity foundEntity = repository.findById(savedBookThemeNightEntity.getId()).get();
		assertEquals(1, (long) foundEntity.getVersion());
		assertEquals("name2", foundEntity.getName());
	}

	@Test
	public void deleteBookThemeNight() {
		repository.delete(savedBookThemeNightEntity);
		assertFalse(repository.existsById(savedBookThemeNightEntity.getId()));
	}
	
	@Test
	public void getByBookId() {
		List<BookThemeNightEntity> entityList = repository.findByBookId(savedBookThemeNightEntity.getBookId());

        assertEquals(entityList.size(), 1);
        assertEqualsBookThemeNight(savedBookThemeNightEntity, entityList.get(0));
	}

	/*@Test(expected = DuplicateKeyException.class)
	public void duplicateError() {
		BookThemeNightEntity entity = new BookThemeNightEntity(1, 1, "name1", new Date(), "location1");
		repository.save(entity);
	}*/
	
	public void optimisticLockError() {

		BookThemeNightEntity entity1 = repository.findById(savedBookThemeNightEntity.getId()).get();
		BookThemeNightEntity entity2 = repository.findById(savedBookThemeNightEntity.getId()).get();

		entity1.setName("name2");
		repository.save(entity1);

		try {
			entity2.setName("name3");
			repository.save(entity2);

			fail("Expected an OptimisticLockingFailureException");
		} catch (OptimisticLockingFailureException e) {
		}
		BookThemeNightEntity updatedEntity = repository.findById(savedBookThemeNightEntity.getId()).get();
		assertEquals(1, (int) updatedEntity.getVersion());
		assertEquals("name2", updatedEntity.getName());
	}


	void assertEqualsBookThemeNight(BookThemeNightEntity expectedEntity, BookThemeNightEntity actualEntity) {
		assertEquals(expectedEntity.getId(), actualEntity.getId());
		assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
		assertEquals(expectedEntity.getBookId(), actualEntity.getBookId());
		assertEquals(expectedEntity.getBookThemeNightId(), actualEntity.getBookThemeNightId());
		assertEquals(expectedEntity.getName(), actualEntity.getName());
		assertEquals(expectedEntity.getStartDate(), actualEntity.getStartDate());
		assertEquals(expectedEntity.getLocation(), actualEntity.getLocation());
	}
}
