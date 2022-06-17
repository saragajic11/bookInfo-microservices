package se.magnus.microservices.core.bookthemenight.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.api.core.bookthemenight.BookThemeNight;
import se.magnus.microservices.core.bookthemenight.persistence.BookThemeNightEntity;

@Mapper(componentModel = "spring")
public interface BookThemeNightMapper {

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    BookThemeNight entityToApi(BookThemeNightEntity entityEmployee);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    BookThemeNightEntity apiToEntity(BookThemeNight apiBookThemeNight);
	
}
