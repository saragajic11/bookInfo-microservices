package se.magnus.microservices.core.comment.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import se.magnus.microservices.core.comment.persistence.CommentEntity;
import se.magnus.api.core.comment.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
	
    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    Comment entityToApi(CommentEntity entityComment);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    CommentEntity apiToEntity(Comment apiComment);
    
    List<Comment> entityListToApiList(List<CommentEntity> entity);
    List<CommentEntity> apiListToEntityList(List<Comment> api);
	

}
