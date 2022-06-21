package se.magnus.microservices.core.book;

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
import se.magnus.microservices.core.book.persistence.BookRepository;
import se.magnus.api.core.book.*;

import java.util.Date;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.data.mongodb.port: 0" })
@RunWith(SpringRunner.class)

public class BookServiceApplicationTests {
	@Autowired
	private WebTestClient client;

	@Autowired
	private BookRepository repository;

	@Before
	public void setupDb() {
		repository.deleteAll();
	}
	
	@Test
	public void getBookById() {

		int bookId = 1;

		postAndVerifyBook(bookId, OK);

		assertTrue(repository.findByBookId(bookId).isPresent());
	}
	
	@Test
	public void postBook() {

		int bookId = 1;
		postAndVerifyBook(bookId, OK);
		assertTrue(repository.findByBookId(bookId).isPresent());
	}

	@Test
	public void deleteBook() {

		int bookId = 1;

		postAndVerifyBook(bookId, OK);
		assertTrue(repository.findByBookId(bookId).isPresent());

		deleteAndVerifyBook(bookId, OK);
		assertFalse(repository.findByBookId(bookId).isPresent());

		deleteAndVerifyBook(bookId, OK);
	}
	
//	@Test
//	public void getBookInvalidParameter() {
//
//		getAndVerifyBook("/invalid-param-type", BAD_REQUEST).jsonPath("$.path")
//				.isEqualTo("/book/invalid-param-type").jsonPath("$.message").isEqualTo("Type mismatch.");
//	}

	@Test
	public void getBookNotFound() {

		int bookIdNotFound = 13;

		getAndVerifyBook(String.valueOf(bookIdNotFound), NOT_FOUND).jsonPath("$.path")
				.isEqualTo("/book/" + bookIdNotFound).jsonPath("$.message")
				.isEqualTo("No book found for bookId: " + bookIdNotFound);
	}
	
//	@Test
//	public void getBookInvalidParameterNegativeValue() {
//
//		int bookIdInvalid = -1;
//
//		getAndVerifyBook(String.valueOf(bookIdInvalid), UNPROCESSABLE_ENTITY).jsonPath("$.path")
//				.isEqualTo("/book/" + bookIdInvalid).jsonPath("$.message")
//				.isEqualTo("Invalid bookId: " + bookIdInvalid);
//	}

	private WebTestClient.BodyContentSpec getAndVerifyBook(String bookIdPath,
			HttpStatus expectedStatus) {
		return client.get().uri("/book/" + bookIdPath).accept(APPLICATION_JSON).exchange()
				.expectStatus().isEqualTo(expectedStatus).expectHeader().contentType(APPLICATION_JSON).expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyBook(int bookId,
			HttpStatus expectedStatus) {

		BookModel book = new BookModel(bookId, "name 1", new Date(), "language 1", "SA");

		return client.post().uri("/book").body(just(book), BookModel.class)
				.accept(APPLICATION_JSON).exchange().expectStatus().isEqualTo(expectedStatus).expectHeader()
				.contentType(APPLICATION_JSON).expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyBook(int bookId,
			HttpStatus expectedStatus) {
		return client.delete().uri("/book/" + bookId).accept(APPLICATION_JSON).exchange()
				.expectStatus().isEqualTo(expectedStatus).expectBody();
	}
}
