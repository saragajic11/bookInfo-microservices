package se.magnus.microservices.core.bookthemenight.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BookThemeNightRepository extends ReactiveCrudRepository<BookThemeNightEntity, String> {
	Flux<BookThemeNightEntity> findByBookId(int bookId);

}
