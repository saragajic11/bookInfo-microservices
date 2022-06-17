package se.magnus.microservices.core.book.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface BookRepository extends PagingAndSortingRepository<BookEntity, String>{

	Optional<BookEntity> findByBookId(int bookId);
}
