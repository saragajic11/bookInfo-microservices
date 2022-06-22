package se.magnus.microservices.core.comment.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.util.http.ServiceUtil;
import se.magnus.util.exceptions.*;
import se.magnus.api.core.comment.*;
import se.magnus.util.exceptions.*;
import se.magnus.microservices.core.comment.persistence.*;
import org.springframework.dao.DuplicateKeyException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class CommentServiceImpl implements CommentService {

	private final ServiceUtil serviceUtil;
	private CommentRepository repository;
	private CommentMapper mapper;
	private static final Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);
	
	@Autowired
	public CommentServiceImpl(ServiceUtil serviceUtil, CommentRepository repository, CommentMapper mapper) {
		this.serviceUtil = serviceUtil;
		this.repository = repository;
		this.mapper = mapper;
	}
	
	@Override
	public Flux<Comment> getComments(int bookId) {
		if (bookId < 1)
			throw new InvalidInputException("Invalid bookId: " + bookId);
		
		return repository.findByBookId(bookId)
				.log()
				.map(e -> mapper.entityToApi(e))
				.map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
	}
	
	@Override
	public Comment createComment(Comment body) {
		if (body.getCommentId() < 1) throw new InvalidInputException("Invalid commentId: " + body.getCommentId());
		CommentEntity entity = mapper.apiToEntity(body);
        Mono<Comment> newEntity = repository.save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex -> new InvalidInputException("Duplicate key,book Id: " + body.getBookId() + ", Comment Id:" + body.getCommentId()))
            .map(e -> mapper.entityToApi(e));
        return newEntity.block();
	}
	

	@Override
	public void deleteComment(int bookId) {
		if (bookId < 1)
			throw new InvalidInputException("Invalid bookId: " + bookId);

        LOG.debug("deleteComment: tries to delete comments for the book with bookId: {}", bookId);
        repository.deleteAll(repository.findByBookId(bookId)).block();  
	}
}
