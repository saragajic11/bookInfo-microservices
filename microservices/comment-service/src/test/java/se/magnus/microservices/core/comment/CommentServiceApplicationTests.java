package se.magnus.microservices.core.comment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

import se.magnus.microservices.core.comment.persistence.*;
import se.magnus.api.core.comment.*;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.data.mongodb.port: 0" })
@RunWith(SpringRunner.class)
public class CommentServiceApplicationTests {
	
	@Autowired
	private WebTestClient client;
	
	@Autowired
	private CommentRepository repository;

	@Before
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getCommentsByBookId() {

		int bookId = 1;

		postAndVerifyComment(bookId, 1, OK);
		postAndVerifyComment(bookId, 2, OK);
		postAndVerifyComment(bookId, 3, OK);

		assertEquals(3, repository.findByBookId(bookId).size());
	}

	@Test
	public void deleteComment() {

		int bookId = 1;
		int commentId = 1;

		postAndVerifyComment(bookId, commentId, OK);
		assertEquals(1, repository.findByBookId(bookId).size());

		deleteAndVerifyCommentByBookId(bookId, OK);
		assertEquals(0, repository.findByBookId(bookId).size());

		deleteAndVerifyCommentByBookId(bookId, OK);
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

	private WebTestClient.BodyContentSpec getAndVerifyCommentByBookId(String bookIdQuery,
		HttpStatus expectedStatus) {
		return client.get()
				.uri("/comment" + bookIdQuery)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyComment(int bookId, int commentId,
		HttpStatus expectedStatus) {
		Comment comment = new Comment(bookId, commentId, "Author 1", "content 1", "SA");
		return client.post().uri("/comment").body(just(comment), Comment.class).accept(APPLICATION_JSON).exchange()
				.expectStatus().isEqualTo(expectedStatus).expectHeader().contentType(APPLICATION_JSON).expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyCommentByBookId(int bookId,
		HttpStatus expectedStatus) {
		return client.delete().uri("/comment?bookId=" + bookId).accept(APPLICATION_JSON)
				.exchange().expectStatus().isEqualTo(expectedStatus).expectBody();
	}
}
