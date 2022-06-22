package se.magnus.microservices.core.book.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BookRepository extends ReactiveCrudRepository<BookEntity, String>{

	Mono<BookEntity> findByBookId(int bookId);
}
