package se.magnus.microservices.core.book;

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
import se.magnus.microservices.core.book.persistence.BookRepository;
import se.magnus.microservices.core.book.services.BookServiceImpl;
import se.magnus.api.core.book.*;

import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.MessagingException;
import se.magnus.util.exceptions.*;
import se.magnus.api.event.Event;
import org.springframework.messaging.support.GenericMessage;

import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

import java.util.Date;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.data.mongodb.port: 0",  "eureka.client.enabled=false"})
@RunWith(SpringRunner.class)

public class BookServiceApplicationTests {
	@Autowired
	private WebTestClient client;
	
	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Autowired
	private BookRepository repository;
	
	private static final Logger LOG = LoggerFactory.getLogger(BookServiceApplicationTests.class);

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}
	
	@Test
	public void getBookById() {

		int bookId = 1;

		assertNull(repository.findByBookId(bookId).block());
		assertEquals(0, (long)repository.count().block());

		sendCreateBookEvent(bookId);

		assertNotNull(repository.findByBookId(bookId).block());
		assertEquals(1, (long)repository.count().block());
		
		getAndVerifyBook(bookId, OK)
            .jsonPath("$.bookId").isEqualTo(bookId);
	}
	
	@Test
	public void duplicateError() {

		int bookId = 1;

		assertNull(repository.findByBookId(bookId).block());

		sendCreateBookEvent(bookId);

		assertNotNull(repository.findByBookId(bookId).block());

		try {
			sendCreateBookEvent(bookId);
			fail("Expected a MessagingException here!");
		} catch (MessagingException me) {
			if (me.getCause() instanceof InvalidInputException)	{
				InvalidInputException iie = (InvalidInputException)me.getCause();
				assertEquals("Duplicate key, book Id: " + bookId, iie.getMessage());
			} else {
				fail("Expected a InvalidInputException as the root cause!");
			}
		}
	}

	@Test
	public void deleteBook() {

		int bookId = 1;

		sendCreateBookEvent(bookId);
		assertNotNull(repository.findByBookId(bookId).block());

		sendDeleteBookEvent(bookId);
		assertNull(repository.findByBookId(bookId).block());

		sendDeleteBookEvent(bookId);
	}
	
	@Test
	public void getBookInvalidParameterString() {

		getAndVerifyBook("/no-integer", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/book/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getBookNotFound() {

		int bookIdNotFound = 13;
		getAndVerifyBook(bookIdNotFound, NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/book/" + bookIdNotFound)
            .jsonPath("$.message").isEqualTo("No book found for bookId: " + bookIdNotFound);
	}
	
	@Test
	public void getBookInvalidParameterNegativeValue() {

		int bookIdInvalid = -1;

		getAndVerifyBook(String.valueOf(bookIdInvalid), UNPROCESSABLE_ENTITY).jsonPath("$.path")
				.isEqualTo("/book/" + bookIdInvalid).jsonPath("$.message")
				.isEqualTo("Invalid bookId: " + bookIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVerifyBook(int bookId, HttpStatus expectedStatus) {
		return getAndVerifyBook("/" + bookId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyBook(String bookIdPath, HttpStatus expectedStatus) {
		return client.get()
			.uri("/book" + bookIdPath)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}
	
	private void sendCreateBookEvent(int bookId) {
		BookModel book = new BookModel(bookId, "Name " + bookId, new Date(), "Language " + bookId, "SA");
		Event<Integer, BookModel> event = new Event(CREATE, bookId, book);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteBookEvent(int bookId) {
		Event<Integer, BookModel> event = new Event(DELETE, bookId, null);
		input.send(new GenericMessage<>(event));
	}
}
