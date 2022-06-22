package se.magnus.microservices.core.comment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

import se.magnus.microservices.core.comment.persistence.*;
import se.magnus.api.core.comment.*;
import se.magnus.api.core.book.*;

import org.springframework.messaging.MessagingException;
import se.magnus.util.exceptions.*;
import se.magnus.api.event.Event;
import org.springframework.messaging.support.GenericMessage;

import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.data.mongodb.port: 0" })
@RunWith(SpringRunner.class)
public class CommentServiceApplicationTests {
	
	@Autowired
	private WebTestClient client;
	
	@Autowired
	private CommentRepository repository;
	
	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}

	@Test
	public void getCommentsByBookId() {

		int bookId = 1;

		sendCreateCommentEvent(bookId, 1);
		sendCreateCommentEvent(bookId, 2);
		sendCreateCommentEvent(bookId, 3);

		assertEquals(3, (long)repository.findByBookId(bookId).count().block());

		getAndVerifyCommentByBookId(bookId, OK)
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[2].bookId").isEqualTo(bookId)
			.jsonPath("$[2].commentId").isEqualTo(3);
	}
	
//	@Test
//	public void duplicateError() {
//
//		int bookId = 1;
//		int commentId = 1;
//
//		sendCreateCommentEvent(bookId, commentId);
//
//		assertEquals(1, (long)repository.count().block());
//
//		try {
//			sendCreateCommentEvent(bookId, commentId);
//			fail("Expected a MessagingException here!");
//		} catch (MessagingException me) {
//			if (me.getCause() instanceof InvalidInputException)	{
//				InvalidInputException iie = (InvalidInputException)me.getCause();
//				assertEquals("Duplicate key, Book Id: 1, Comment Id:1", iie.getMessage());
//			} else {
//				fail("Expected a InvalidInputException as the root cause!");
//			}
//		}
//
//		assertEquals(1, (long)repository.count().block());
//	}

	@Test
	public void deleteComment() {

		int bookId = 1;
		int commentId = 1;

		sendCreateCommentEvent(bookId, commentId);
		assertEquals(1, (long)repository.findByBookId(bookId).count().block());

		sendDeleteCommentEvent(bookId);
		assertEquals(0, (long)repository.findByBookId(bookId).count().block());

		sendDeleteCommentEvent(bookId);
	}

//	@Test
//	public void getCommentsMissingParameter() {
//
//		getAndVerifyCommentByBookId("", BAD_REQUEST).jsonPath("$.path").isEqualTo("/comment")
//				.jsonPath("$.message").isEqualTo("Required int parameter 'bookId' is not present");
//	}
//
//	@Test
//	public void getCommentInvalidParameter() {
//
//		getAndVerifyCommentByBookId("?bookId=no-integer", BAD_REQUEST).jsonPath("$.path")
//				.isEqualTo("/comment").jsonPath("$.message").isEqualTo("Type mismatch.");
//
//	}

	@Test
	public void getCommentNotFound() {

		getAndVerifyCommentByBookId("?bookId=113", OK).jsonPath("$.length()").isEqualTo(0);

	}

	@Test
	public void getCommentInvalidParameterNegativeValue() {

		int bookIdInvalid = -1;

		getAndVerifyCommentByBookId("?bookId=" + bookIdInvalid,
				UNPROCESSABLE_ENTITY).jsonPath("$.path").isEqualTo("/comment").jsonPath("$.message")
						.isEqualTo("Invalid bookId: " + bookIdInvalid);

	}

	private WebTestClient.BodyContentSpec getAndVerifyCommentByBookId(int bookId, HttpStatus expectedStatus) {
		return getAndVerifyCommentByBookId("?bookId=" + bookId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyCommentByBookId(String bookIdQuery, HttpStatus expectedStatus) {
		return client.get()
			.uri("/comment" + bookIdQuery)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}


	private void sendCreateCommentEvent(int bookId, int commentId) {
		Comment comment = new Comment(bookId, commentId, "Author " + commentId, "Content " + commentId, "SA");
		Event<Integer, BookModel> event = new Event(CREATE, bookId, comment);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteCommentEvent(int bookId) {
		Event<Integer, BookModel> event = new Event(DELETE, bookId, null);
		input.send(new GenericMessage<>(event));
	}
}
