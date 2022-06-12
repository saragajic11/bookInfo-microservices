package se.magnus.microservices.composite.book;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.sql.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.mockito.Mockito.when;

import se.magnus.api.core.book.*;
import se.magnus.api.core.bookthemenight.BookThemeNight;
import se.magnus.api.core.comment.Comment;
import se.magnus.api.core.rating.Rating;
import se.magnus.microservices.composite.book.services.*;

import se.magnus.util.exceptions.*;

@SpringBootTest(classes=BookCompositeServiceApplicationTests.class)
@RunWith(SpringRunner.class)
class BookCompositeServiceApplicationTests {
	
	private static final int BOOK_ID_OK = 1;
	private static final int BOOK_ID_NOT_FOUND = 2;
	private static final int BOOK_ID_INVALID = 3;


	@Autowired
    private WebTestClient client;
	
	@MockBean
	private BookCompositeIntegration compositeIntegration;
	
	@Before
	public void setUp() {

		when(compositeIntegration.getBook(BOOK_ID_OK)).
			thenReturn(new BookModel(BOOK_ID_OK, "Test Name", Date.valueOf("2021-08-13"), "Test language", "mock-address"));
		when(compositeIntegration.getComments(BOOK_ID_OK)).
			thenReturn(singletonList(new Comment(BOOK_ID_OK, 1,"Test author", "Test content", "mock address")));
		when(compositeIntegration.getRatings(BOOK_ID_OK)).
			thenReturn(singletonList(new Rating(BOOK_ID_OK, 1, "Test author", 10, "mock address")));
		when(compositeIntegration.getBookThemeNights(BOOK_ID_OK)).
			thenReturn(singletonList(new BookThemeNight(BOOK_ID_OK, 1, "Test name",Date.valueOf("2021-08-13"), "Test location", "mock address")));

		when(compositeIntegration.getBook(BOOK_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + BOOK_ID_NOT_FOUND));

		when(compositeIntegration.getBook(BOOK_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + BOOK_ID_INVALID));
	}
	
	@Test
	public void contextLoads() {
		
	}
	
	@Test
	public void getBookById() {
		client.get()
	    .uri("/book-composite/" + BOOK_ID_OK)
	    .accept(APPLICATION_JSON)
	    .exchange()
	    .expectStatus().isOk()
	    .expectHeader().contentType(APPLICATION_JSON)
	    .expectBody()
	    .jsonPath("$.bookId").isEqualTo(BOOK_ID_OK)
	    .jsonPath("$.ratings.length()").isEqualTo(1)
	    .jsonPath("$.comments.length()").isEqualTo(1)
		.jsonPath("$.bookThemeNight.length()").isEqualTo(1);
	}
	
	@Test
	public void getBookNotFound() {
	    client.get()
	        .uri("/book-composite/" + BOOK_ID_NOT_FOUND)
	        .accept(APPLICATION_JSON)
	        .exchange()
	        .expectStatus().isNotFound()
	        .expectHeader().contentType(APPLICATION_JSON)
	        .expectBody()
	        .jsonPath("$.path").isEqualTo("/book-composite/" + BOOK_ID_NOT_FOUND)
	        .jsonPath("$.message").isEqualTo("NOT FOUND: " + BOOK_ID_NOT_FOUND);
	}
	
	@Test
	public void getBookInvalidInput() {
	    client.get()
	        .uri("/book-composite/" + BOOK_ID_INVALID)
	        .accept(APPLICATION_JSON)
	        .exchange()
	        .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
	        .expectHeader().contentType(APPLICATION_JSON)
	        .expectBody()
	        .jsonPath("$.path").isEqualTo("/book-composite/" + BOOK_ID_INVALID)
	        .jsonPath("$.message").isEqualTo("INVALID: " + BOOK_ID_INVALID);
	}
}
