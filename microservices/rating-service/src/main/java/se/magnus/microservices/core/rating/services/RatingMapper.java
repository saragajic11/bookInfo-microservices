package se.magnus.microservices.core.rating.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import se.magnus.microservices.core.rating.persistence.RatingEntity;
import se.magnus.api.core.rating.Rating;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    Rating entityToApi(RatingEntity entityRating);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    RatingEntity apiToEntity(Rating apiRating);
}
