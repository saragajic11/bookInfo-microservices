package se.magnus.microservices.core.bookthemenight;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes= BookThemeNightServiceApplicationTests.class, webEnvironment=RANDOM_PORT)
@RunWith(SpringRunner.class)
class BookThemeNightServiceApplicationTests {
	
	@Autowired
	private WebTestClient client;

	@Test
	void contextLoads() {
	}
	
	@Test
	public void getBookThemeNightsByBookId() {

		int bookId = 1;

		client.get()
			.uri("/book-theme-night?bookId=" + bookId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[0].bookID").isEqualTo(bookId);
	}
	
	@Test
	public void getBookThemeNightsMissingParameter() {

		client.get()
			.uri("/book-theme-night")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/book-theme-night")
			.jsonPath("$.message").isEqualTo("Required int parameter 'bookId' is not present");
	}
	
	@Test
	public void getBookThemeNightsInvalidParameter() {

		client.get()
			.uri("/book-theme-night?bookId=no-integer")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/book-theme-night")
			.jsonPath("$.message").isEqualTo("Type mismatch.");
	}
	
	@Test
	public void  getBooksNotFound() {

		int bookIdNotFound = 113;

		client.get()
			.uri("/book-theme-night?bookId=" + bookIdNotFound)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.length()").isEqualTo(0);
	}
	
	@Test
	public void getBookThemeNightsInvalidParameterNegativeValue() {

		int bookIdInvalid = -1;

		client.get()
			.uri("/book-theme-night?bookId=" + bookIdInvalid)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/book-theme-night")
			.jsonPath("$.message").isEqualTo("Invalid bookId: " + bookIdInvalid);
	}
	
	

}
