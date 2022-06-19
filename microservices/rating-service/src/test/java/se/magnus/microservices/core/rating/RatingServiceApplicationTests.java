package se.magnus.microservices.core.rating;

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

import se.magnus.microservices.core.rating.persistence.*;
import se.magnus.api.core.rating.*;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.datasource.url=jdbc:h2:mem:review-db" })
@RunWith(SpringRunner.class)

class RatingServiceApplicationTests {
	
	
	@Autowired
	private WebTestClient client;
	
	@Autowired
	private RatingRepository repository;

	@Before
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getRatingsByBookId() {

		int bookId = 1;

		assertEquals(0, repository.findByBookId(bookId).size());

		postAndVerifyRating(bookId, 1, OK);
		postAndVerifyRating(bookId, 2, OK);
		postAndVerifyRating(bookId, 3, OK);

		assertEquals(3, repository.findByBookId(bookId).size());
	}
	
	@Test
	public void deleteRating() {

		int bookId = 1;
		int ratingId = 1;

		postAndVerifyRating(bookId, ratingId, OK);
		assertEquals(1, repository.findByBookId(bookId).size());

		deleteAndVerifyRatingByBookId(bookId, OK);
		assertEquals(0, repository.findByBookId(bookId).size());

		deleteAndVerifyRatingByBookId(bookId, OK);
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

		getAndVerifyRatingByBookId("?rating=" + bookIdInvalid,
				UNPROCESSABLE_ENTITY).jsonPath("$.path").isEqualTo("/rating").jsonPath("$.message")
						.isEqualTo("Invalid bookId: " + bookIdInvalid);
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
	
	private WebTestClient.BodyContentSpec postAndVerifyRating(int bookId, int ratingId,
			HttpStatus expectedStatus) {
		Rating rating = new Rating(bookId, ratingId, "author 1", 5, "SA");
		return client.post().uri("/rating").body(just(rating), Rating.class)
				.accept(APPLICATION_JSON).exchange().expectStatus().isEqualTo(expectedStatus).expectHeader()
				.contentType(APPLICATION_JSON).expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyRatingByBookId(int bookId,
			HttpStatus expectedStatus) {
		return client.delete().uri("/rating?bookId=" + bookId).accept(APPLICATION_JSON)
				.exchange().expectStatus().isEqualTo(expectedStatus).expectBody();
	}

}
