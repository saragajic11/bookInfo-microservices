package se.magnus.microservices.core.rating;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import se.magnus.microservices.core.rating.persistence.*;
import se.magnus.api.core.rating.*;
import se.magnus.api.core.book.*;

import org.springframework.messaging.MessagingException;
import se.magnus.util.exceptions.*;
import se.magnus.api.event.Event;
import org.springframework.messaging.support.GenericMessage;

import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "logging.level.com.example=DEBUG", "eureka.client.enabled=false", "spring.datasource.url=jdbc:h2:mem:review-db" })
@RunWith(SpringRunner.class)

public class RatingServiceApplicationTests {
	
	
	@Autowired
	private WebTestClient client;
	
	@Autowired
	private RatingRepository repository;
	
	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;
	
	private static final Logger LOG = LoggerFactory.getLogger(RatingServiceApplicationTests.class);

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll();
	}

	@Test
	public void getRatingsByBookId() {

		int bookId = 1;

		assertEquals(0, repository.findByBookId(bookId).size());

		sendCreateRatingEvent(bookId, 1);
		sendCreateRatingEvent(bookId, 2);
		sendCreateRatingEvent(bookId, 3);
		assertEquals(3, repository.findByBookId(bookId).size());
		
		getAndVerifyRatingByBookId(bookId, OK)
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[2].bookId").isEqualTo(bookId)
			.jsonPath("$[2].ratingId").isEqualTo(3);
	}
	
	@Test
	public void duplicateError() {

		int bookId = 1;
		int ratingId = 1;

		assertEquals(0, repository.count());

		sendCreateRatingEvent(bookId, ratingId);

		assertEquals(1, repository.count());

		try {
			sendCreateRatingEvent(bookId, ratingId);
			fail("Expected a MessagingException here!");
		} catch (MessagingException me) {
			if (me.getCause() instanceof InvalidInputException)	{
				InvalidInputException iie = (InvalidInputException)me.getCause();
				assertEquals("Duplicate key, book Id: 1, rating Id:1", iie.getMessage());
			} else {
				fail("Expected a InvalidInputException as the root cause!");
			}
		}

		assertEquals(1, repository.count());
	}
	
	@Test
	public void deleteRating() {

		int bookId = 1;
		int ratingId = 1;

		sendCreateRatingEvent(bookId, ratingId);
		assertEquals(1, repository.findByBookId(bookId).size());

		sendDeleteRatingEvent(bookId);
		assertEquals(0, repository.findByBookId(bookId).size());

		sendDeleteRatingEvent(bookId);
	}

	@Test
	public void getRatingMissingParameter() {
		getAndVerifyRatingByBookId("", BAD_REQUEST).jsonPath("$.path").isEqualTo("/rating")
				.jsonPath("$.message").isEqualTo("Required int parameter 'bookId' is not present");
	}

	@Test
	public void getRatingInvalidParameter() {

		getAndVerifyRatingByBookId("?bookId=no-integer", BAD_REQUEST).jsonPath("$.path")
				.isEqualTo("/rating").jsonPath("$.message").isEqualTo("Type mismatch.");
	}
	
	@Test
	public void getRatingsNotFound() {

		getAndVerifyRatingByBookId("?bookId=213", OK).jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getRatingsInvalidParameterNegativeValue() {

		int bookIdInvalid = -1;

		getAndVerifyRatingByBookId("?bookId=" + bookIdInvalid,
				UNPROCESSABLE_ENTITY).jsonPath("$.path").isEqualTo("/rating").jsonPath("$.message")
						.isEqualTo("Invalid bookId: " + bookIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVerifyRatingByBookId(int bookId, HttpStatus expectedStatus) {
		return getAndVerifyRatingByBookId("?bookId=" + bookId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyRatingByBookId(String bookIdQuery, HttpStatus expectedStatus) {
		return client.get()
			.uri("/rating" + bookIdQuery)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}
	
	private void sendCreateRatingEvent(int bookId, int ratingId) {
		Rating rating = new Rating(1, ratingId, "Author", ratingId, "SA");
		Event<Integer, BookModel> event = new Event(CREATE, bookId, rating);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteRatingEvent(int bookId) {
		Event<Integer, BookModel> event = new Event(DELETE, bookId, null);
		input.send(new GenericMessage<>(event));
	}

}
