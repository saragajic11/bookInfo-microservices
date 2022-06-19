package se.magnus.microservices.core.comment.services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.util.http.ServiceUtil;
import se.magnus.util.exceptions.*;
import se.magnus.api.core.comment.*;
import se.magnus.microservices.core.comment.persistence.*;
import org.springframework.dao.DuplicateKeyException;

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
	public List<Comment> getComments(int bookId) {
		if (bookId < 1)
			throw new InvalidInputException("Invalid bookId: " + bookId);

		List<CommentEntity> entityList = repository.findByBookId(bookId);
		List<Comment> list = mapper.entityListToApiList(entityList);
		list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

		LOG.debug("getComments: response size: {}", list.size());

		return list;
	}
	
	@Override
	public Comment createComment(Comment body) {
		try {
			CommentEntity entity = mapper.apiToEntity(body);
			CommentEntity newEntity = repository.save(entity);
			LOG.debug("createComment: created a comment entity: {}/{}", body.getBookId(),
						body.getCommentId());
			return mapper.entityToApi(newEntity);

			} catch (DuplicateKeyException dke) {
				throw new InvalidInputException("Duplicate key, BookId Id: " + body.getBookId() + ", Comment Id:"
						+ body.getCommentId());
			}
		}
	

	@Override
	public void deleteComment(int bookId) {
		LOG.debug("deleteComments: tries to delete comments for the book with bookId: {}",
				bookId);
		repository.deleteAll(repository.findByBookId(bookId));
	}
}
