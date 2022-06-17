package se.magnus.microservices.core.book;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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


import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {


	@Autowired
	private BookRepository repository;

	private BookEntity savedBookEntity;
	
	@Before
	public void setupDb() {
		repository.deleteAll();

		BookEntity entity = new BookEntity(1, "name1",new Date(), "language 1");
		savedBookEntity = repository.save(entity);

		assertEqualsBook(entity, savedBookEntity);
	}
	
	@Test
	public void createBook() {

		BookEntity newEntity = new BookEntity(2, "name2", new Date(), "language 2");
		repository.save(newEntity);

		BookEntity foundEntity = repository.findById(newEntity.getId()).get();
		assertEqualsBook(newEntity, foundEntity);

		assertEquals(2, repository.count());
	}

	@Test
	public void updateBook() {
		savedBookEntity.setName("name2");
		repository.save(savedBookEntity);

		BookEntity foundEntity = repository.findById(savedBookEntity.getId()).get();
		assertEquals(1, (long) foundEntity.getVersion());
		assertEquals("name2", foundEntity.getName());
	}

	@Test
	public void deleteBook() {
		repository.delete(savedBookEntity);
		assertFalse(repository.existsById(savedBookEntity.getId()));
	}
	
	@Test
   	public void getByBookId() {
        Optional<BookEntity> entity = repository.findByBookId(savedBookEntity.getBookId());

        assertTrue(entity.isPresent());
        assertEqualsBook(savedBookEntity, entity.get());
    }

//	@Test(expected = DuplicateKeyException.class)
//	public void duplicateError() {
//		BookEntity entity = new BookEntity(1, "name1", new Date(), "language 1");
//		repository.save(entity);
//	}
	
	@Test
   	public void optimisticLockError() {

    	BookEntity entity1 = repository.findById(savedBookEntity.getId()).get();
    	BookEntity entity2 = repository.findById(savedBookEntity.getId()).get();

        entity1.setName("name1");
        repository.save(entity1);

        try {
            entity2.setName("name2");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        BookEntity updatedEntity = repository.findById(savedBookEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("name1", updatedEntity.getName());
    }
	
	@Test
    public void paging() {

        repository.deleteAll();

        List<BookEntity> newBooks = rangeClosed(1001, 1010)
            .mapToObj(i -> new BookEntity(i, "book"+i, new Date(), "language"+i))
            .collect(Collectors.toList());
        repository.saveAll(newBooks);

        Pageable nextPage = PageRequest.of(0, 4, ASC, "bookId");
        nextPage = testNextPage(nextPage, "[1001, 1002, 1003, 1004]", true);
        nextPage = testNextPage(nextPage, "[1005, 1006, 1007, 1008]", true);
        nextPage = testNextPage(nextPage, "[1009, 1010]", false);
    }
	
	private Pageable testNextPage(Pageable nextPage, String expectedBookIds, boolean expectsNextPage) {
        Page<BookEntity> bookPage = repository.findAll(nextPage);
        assertEquals(expectedBookIds, bookPage.getContent().stream().map(p -> p.getBookId()).collect(Collectors.toList()).toString());
        assertEquals(expectsNextPage, bookPage.hasNext());
        return bookPage.nextPageable();
    }

    private void assertEqualsBook(BookEntity expectedEntity, BookEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getBookId(),        actualEntity.getBookId());
        assertEquals(expectedEntity.getName(),           actualEntity.getName());
        assertEquals(expectedEntity.getReleaseDate(),           actualEntity.getReleaseDate());
        assertEquals(expectedEntity.getLanguage(),           actualEntity.getLanguage());
    }

}
