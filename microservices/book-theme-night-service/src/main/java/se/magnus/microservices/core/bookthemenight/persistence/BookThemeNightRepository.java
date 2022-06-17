package se.magnus.microservices.core.bookthemenight.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface BookThemeNightRepository extends CrudRepository<BookThemeNightEntity, String> {
	List<BookThemeNightEntity> findByBookId(int bookId);

}
