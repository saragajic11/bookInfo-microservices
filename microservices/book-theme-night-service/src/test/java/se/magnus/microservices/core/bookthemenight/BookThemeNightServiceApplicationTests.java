package se.magnus.microservices.core.bookthemenight;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import se.magnus.microservices.core.bookthemenight.persistence.*;
import se.magnus.api.core.bookthemenight.*;
import se.magnus.api.core.book.*;

import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.cloud.stream.messaging.Sink;

import se.magnus.util.exceptions.*;
import org.springframework.messaging.MessagingException;
import se.magnus.api.event.Event;
import org.springframework.messaging.support.GenericMessage;

import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

import java.util.Date;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.data.mongodb.port: 0" })
@RunWith(SpringRunner.class)
public class BookThemeNightServiceApplicationTests {
	
	@Autowired
	private WebTestClient client;

	@Autowired
	private BookThemeNightRepository repository;

	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}
	
	
	private static final Logger LOG = LoggerFactory.getLogger(BookThemeNightServiceApplicationTests.class);

	@Test
	public void getBookThemeNightsByBookId() {

		int bookId = 1;

		sendCreateBookThemeNightEvent(bookId, 1);
		sendCreateBookThemeNightEvent(bookId, 2);
		sendCreateBookThemeNightEvent(bookId, 3);
		
		assertEquals(3, (long)repository.findByBookId(bookId).count().block());
		
		getAndVerifyBookThemeNightByBookId(bookId, OK)
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[2].bookId").isEqualTo(bookId)
			.jsonPath("$[2].bookThemeNightId").isEqualTo(3);
	}
	
//	@Test
//	public void duplicateError() {
//
//		int bookId = 1;
//		int bookThemeNightId = 1;
//
//		sendCreateBookThemeNightEvent(bookId, bookThemeNightId);
//
//		assertEquals(1, (long)repository.count().block());
//
//		try {
//			sendCreateBookThemeNightEvent(bookId, bookThemeNightId);
//			fail("Expected a MessagingException here!");
//		} catch (MessagingException me) {
//			if (me.getCause() instanceof InvalidInputException)	{
//				InvalidInputException iie = (InvalidInputException)me.getCause();
//				assertEquals("Duplicate key, Book Id: 1, Book theme night Id:1", iie.getMessage());
//			} else {
//				fail("Expected a InvalidInputException as the root cause!");
//			}
//		}
//
//		assertEquals(1, (long)repository.count().block());
//	}

	@Test
	public void deleteBookThemeNight() {

		int bookId = 1;
		int bookThemeNightId = 1;

		sendCreateBookThemeNightEvent(bookId, bookThemeNightId);
		assertEquals(1, (long)repository.findByBookId(bookId).count().block());

		sendDeleteBookThemeNightEvent(bookId);
		assertEquals(0, (long)repository.findByBookId(bookId).count().block());

		sendDeleteBookThemeNightEvent(bookId);
	}

//	@Test
//	public void getBookThemeNightsMissingParameter() {
//
//		getAndVerifyBookThemeNightByBookId("", BAD_REQUEST).jsonPath("$.path").isEqualTo("/book-theme-night")
//				.jsonPath("$.message").isEqualTo("Required int parameter 'bookId' is not present");
//	}
//
//	@Test
//	public void getBookThemeNightInvalidParameter() {
//
//		getAndVerifyBookThemeNightByBookId("?bookId=no-integer", BAD_REQUEST).jsonPath("$.path")
//				.isEqualTo("/book-theme-night").jsonPath("$.message").isEqualTo("Type mismatch.");
//
//	}

	@Test
	public void getBookThemeNightNotFound() {

		getAndVerifyBookThemeNightByBookId("?bookId=113", OK).jsonPath("$.length()").isEqualTo(0);

	}

	@Test
	public void getBookThemeNightsInvalidParameterNegativeValue() {

		int bookIdInvalid = -1;

		getAndVerifyBookThemeNightByBookId("?bookId=" + bookIdInvalid,
				UNPROCESSABLE_ENTITY).jsonPath("$.path").isEqualTo("/book-theme-night").jsonPath("$.message")
						.isEqualTo("Invalid bookId: " + bookIdInvalid);

	}

	private WebTestClient.BodyContentSpec getAndVerifyBookThemeNightByBookId(int bookId, HttpStatus expectedStatus) {
		return getAndVerifyBookThemeNightByBookId("?bookId=" + bookId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyBookThemeNightByBookId(String bookIdQuery, HttpStatus expectedStatus) {
		return client.get()
			.uri("/book-theme-night" + bookIdQuery)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}
	
	private void sendCreateBookThemeNightEvent(int bookId, int bookThemeNightId) {
		BookThemeNight bookThemeNight = new BookThemeNight(bookId, bookThemeNightId, "Name " + bookThemeNightId, new Date(), "Location " + bookThemeNightId, "SA");
		Event<Integer, BookModel> event = new Event(CREATE, bookId, bookThemeNight);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteBookThemeNightEvent(int bookId) {
		Event<Integer, BookModel> event = new Event(DELETE, bookId, null);
		input.send(new GenericMessage<>(event));
	}

}
