package se.magnus.microservices.core.bookthemenight;

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

import se.magnus.microservices.core.bookthemenight.persistence.*;
import se.magnus.api.core.bookthemenight.*;

import java.util.Date;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.data.mongodb.port: 0" })
@RunWith(SpringRunner.class)
class BookThemeNightServiceApplicationTests {
	
	@Autowired
	private WebTestClient client;

	@Autowired
	private BookThemeNightRepository repository;

	@Before
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getBookThemeNightsByBookId() {

		int bookId = 1;

		postAndVerifyBookThemeNight(bookId, 1, OK);
		postAndVerifyBookThemeNight(bookId, 2, OK);
		postAndVerifyBookThemeNight(bookId, 3, OK);

		assertEquals(3, repository.findByBookId(bookId).size());
	}

	@Test
	public void deleteBookThemeNight() {

		int bookId = 1;
		int bookThemeNightId = 1;

		postAndVerifyBookThemeNight(bookId, bookThemeNightId, OK);
		assertEquals(1, repository.findByBookId(bookId).size());

		deleteAndVerifyBookThemeNightByBookId(bookId, OK);
		assertEquals(0, repository.findByBookId(bookId).size());

		deleteAndVerifyBookThemeNightByBookId(bookId, OK);
	}

	@Test
	public void getBookThemeNightsMissingParameter() {

		getAndVerifyBookThemeNightsByBookId("", BAD_REQUEST).jsonPath("$.path").isEqualTo("/book-theme-night")
				.jsonPath("$.message").isEqualTo("Required int parameter 'bookId' is not present");
	}

	@Test
	public void getEmployeesInvalidParameter() {

		getAndVerifyBookThemeNightsByBookId("?bookId=no-integer", BAD_REQUEST).jsonPath("$.path")
				.isEqualTo("/book-theme-night").jsonPath("$.message").isEqualTo("Type mismatch.");

	}

	@Test
	public void getBookThemeNightNotFound() {

		getAndVerifyBookThemeNightsByBookId("?bookId=113", OK).jsonPath("$.length()").isEqualTo(0);

	}

	@Test
	public void getBookThemeNightsInvalidParameterNegativeValue() {

		int bookIdInvalid = -1;

		getAndVerifyBookThemeNightsByBookId("?bookId=" + bookIdInvalid,
				UNPROCESSABLE_ENTITY).jsonPath("$.path").isEqualTo("/book-theme-night").jsonPath("$.message")
						.isEqualTo("Invalid bookId: " + bookIdInvalid);

	}

	private WebTestClient.BodyContentSpec getAndVerifyBookThemeNightsByBookId(String bookIdQuery,
			HttpStatus expectedStatus) {
		return client.get().uri("/book-theme-night" + bookIdQuery).accept(APPLICATION_JSON).exchange()
				.expectStatus().isEqualTo(expectedStatus).expectHeader().contentType(APPLICATION_JSON).expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyBookThemeNight(int bookId, int bookThemeNightId,
			HttpStatus expectedStatus) {
		BookThemeNight bookThemeNight = new BookThemeNight(bookId, bookThemeNightId, "name 1", new Date(), "location 1", "SA");
		return client.post().uri("/book-theme-night").body(just(bookThemeNight), BookThemeNight.class).accept(APPLICATION_JSON).exchange()
				.expectStatus().isEqualTo(expectedStatus).expectHeader().contentType(APPLICATION_JSON).expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyBookThemeNightByBookId(int bookId,
			HttpStatus expectedStatus) {
		return client.delete().uri("/book-theme-night?bookId=" + bookId).accept(APPLICATION_JSON)
				.exchange().expectStatus().isEqualTo(expectedStatus).expectBody();
	}

}
