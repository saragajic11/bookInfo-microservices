package se.magnus.microservices.core.book.services;

import se.magnus.microservices.core.book.persistence.BookEntity;
import se.magnus.api.core.book.BookModel
import se.magnus.microservices.core.comment.services.Mapping;
import se.magnus.microservices.core.comment.services.Mappings;
import se.magnus.microservices.core.rating.services.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    BookModel entityToApi(BookEntity entityBook);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    BookEntity apiToEntity(BookModel apiModel);
	
}
