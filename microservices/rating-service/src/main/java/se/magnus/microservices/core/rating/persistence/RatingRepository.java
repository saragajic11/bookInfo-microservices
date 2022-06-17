package se.magnus.microservices.core.rating.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;


public interface RatingRepository extends CrudRepository<RatingEntity, Integer> {
	
    @Transactional(readOnly = true)
    List<RatingEntity> findByBookId(int bookId);

}
