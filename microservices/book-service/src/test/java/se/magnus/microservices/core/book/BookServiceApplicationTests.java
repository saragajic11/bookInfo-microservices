package se.magnus.microservices.core.book;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes= BookServiceApplicationTests.class, webEnvironment=RANDOM_PORT)
@RunWith(SpringRunner.class)

class BookServiceApplicationTests {
	@Autowired
	private WebTestClient client;
	
	@Test
	public void getBook() {

		int bookId = 1;

		client.get()
			.uri("/book/" + bookId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.bookId").isEqualTo(bookId);
	}
	
	@Test
	public void getBookInvalidParameter() {

		client.get()
			.uri("/book/invalid-param-type")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/book/invalid-param-type")
			.jsonPath("$.message").isEqualTo("Type mismatch.");
	}
	
	@Test
	public void  getBookNotFound() {

		int bookIdNotFound = 13;

		client.get()
			.uri("/book/" + bookIdNotFound)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isNotFound()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
            .jsonPath("$.path").isEqualTo("/book/" + bookIdNotFound)
            .jsonPath("$.message").isEqualTo("No book found for bookId: " + bookIdNotFound);
	}
	
	@Test
	public void getBookInvalidParameterNegativeValue() {

		int bookId = -1;

		client.get()
			.uri("/book/" + bookId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/book/" + bookId)
			.jsonPath("$.message").isEqualTo("Invalid bookId: " + bookId);
	}
}
