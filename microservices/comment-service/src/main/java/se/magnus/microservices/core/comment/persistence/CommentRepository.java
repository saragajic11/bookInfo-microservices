package se.magnus.microservices.core.comment.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<CommentEntity, String> {
	List<CommentEntity> findByBookId(int bookId);

}
