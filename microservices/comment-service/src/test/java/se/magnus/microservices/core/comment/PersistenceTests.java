package se.magnus.microservices.core.comment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import se.magnus.microservices.core.comment.persistence.*;
import static org.hamcrest.Matchers.hasSize;
import java.util.List;


import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {
	
	@Autowired
	private CommentRepository repository;

	private CommentEntity savedCommentEntity;

	@Before
	public void setupDb() {
		repository.deleteAll().block();

		CommentEntity entity = new CommentEntity(1, 1, "author1", "content1");
		savedCommentEntity = repository.save(entity).block();

		assertEqualsComment(entity, savedCommentEntity);
	}

	@Test
	public void createComment() {

		CommentEntity newEntity = new CommentEntity(1, 2, "author2", "content2");
		repository.save(newEntity).block();

		CommentEntity foundEntity = repository.findById(newEntity.getId()).block();
		assertEqualsComment(newEntity, foundEntity);

		assertEquals(2, (long)repository.count().block());
	}

	@Test
	public void updateComment() {
		savedCommentEntity.setAuthor("author2");
		repository.save(savedCommentEntity).block();

		CommentEntity foundEntity = repository.findById(savedCommentEntity.getId()).block();
		assertEquals(1, (long) foundEntity.getVersion());
		assertEquals("author2", foundEntity.getAuthor());
	}
	
	@Test
	public void deleteComment() {
		repository.delete(savedCommentEntity).block();
		assertFalse(repository.existsById(savedCommentEntity.getId()).block());
	}

	@Test
	public void getByBookId() {
		List<CommentEntity> entityList = repository.findByBookId(savedCommentEntity.getBookId()).collectList().block();

        assertThat(entityList, hasSize(1));
        assertEqualsComment(savedCommentEntity, entityList.get(0));
	}

	@Test(expected = DuplicateKeyException.class)
	public void duplicateError() {
		CommentEntity entity = new CommentEntity(1, 1, "author1", "content1");
		repository.save(entity);
	}
	
	@Test
	public void optimisticLockError() {

		CommentEntity entity1 = repository.findById(savedCommentEntity.getId()).block();
		CommentEntity entity2 = repository.findById(savedCommentEntity.getId()).block();

		entity1.setAuthor("author2");
		repository.save(entity1).block();

		try {
			entity2.setAuthor("author3");
			repository.save(entity2).block();

			fail("Expected an OptimisticLockingFailureException");
		} catch (OptimisticLockingFailureException e) {
		}
		CommentEntity updatedEntity = repository.findById(savedCommentEntity.getId()).block();
		assertEquals(1, (int) updatedEntity.getVersion());
		assertEquals("author2", updatedEntity.getAuthor());
	}
	
	private void assertEqualsComment(CommentEntity expectedEntity, CommentEntity actualEntity) {
		assertEquals(expectedEntity.getId(), actualEntity.getId());
		assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
		assertEquals(expectedEntity.getBookId(), actualEntity.getBookId());
		assertEquals(expectedEntity.getCommentId(), actualEntity.getCommentId());
		assertEquals(expectedEntity.getAuthor(), actualEntity.getAuthor());
		assertEquals(expectedEntity.getContent(), actualEntity.getContent());
	}

	
}
