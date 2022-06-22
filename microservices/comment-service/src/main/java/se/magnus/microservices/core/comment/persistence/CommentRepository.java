package se.magnus.microservices.core.comment.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CommentRepository extends ReactiveCrudRepository<CommentEntity, String> {
	Flux<CommentEntity> findByBookId(int bookId);

}
