package se.magnus.microservices.composite.book;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

import se.magnus.api.core.book.*;
import se.magnus.api.core.bookthemenight.BookThemeNight;
import se.magnus.api.core.comment.Comment;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.composite.book.*;
import se.magnus.microservices.composite.book.services.*;

import se.magnus.util.exceptions.*;
import java.util.Date;

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
			thenReturn(new BookModel(BOOK_ID_OK, "Test Name", new Date(), "Test language", "mock-address"));
		when(compositeIntegration.getComments(BOOK_ID_OK)).
			thenReturn(singletonList(new Comment(BOOK_ID_OK, 1,"Test author", "Test content", "mock address")));
		when(compositeIntegration.getRatings(BOOK_ID_OK)).
			thenReturn(singletonList(new Rating(BOOK_ID_OK, 1, "Test author", 10, "mock address")));
		when(compositeIntegration.getBookThemeNights(BOOK_ID_OK)).
			thenReturn(singletonList(new BookThemeNight(BOOK_ID_OK, 1, "Test name",new Date(), "Test location", "mock address")));

		when(compositeIntegration.getBook(BOOK_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + BOOK_ID_NOT_FOUND));

		when(compositeIntegration.getBook(BOOK_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + BOOK_ID_INVALID));
	}
	
	@Test
	public void contextLoads() {
		
	}
	
	@Test
	public void createCompositeBook1() {

		BookAggregateModel compositeBook = new BookAggregateModel(1, "name", new Date(), "language", null, null, null, null);

		postAndVerifyBook(compositeBook, OK);
	}

	@Test
	public void createCompositeBook2() {
		BookAggregateModel compositeBook = new BookAggregateModel(1, "name", new Date(), "language",
				singletonList(new RatingSummary(1, "author", 1)),
				singletonList(new CommentSummary(1, "author", "content")),
				singletonList(new BookThemeNightSummary(1, "name", new Date(), "location")), null);

		postAndVerifyBook(compositeBook, OK);
	}
	
	@Test
	public void deleteCompositeBook() {
		BookAggregateModel compositeBook = new BookAggregateModel(1, "name", new Date(), "language",
				singletonList(new RatingSummary(1, "author", 1)),
				singletonList(new CommentSummary(1, "author", "content")),
				singletonList(new BookThemeNightSummary(1, "name", new Date(), "location")), null);

		postAndVerifyBook(compositeBook, OK);

		deleteAndVerifyBook(compositeBook.getBookId(), OK);
		deleteAndVerifyBook(compositeBook.getBookId(), OK);
	}

	@Test
	public void getBookById() {

		getAndVerifyBook(BOOK_ID_OK, OK)
            .jsonPath("$.bookId").isEqualTo(BOOK_ID_OK)
            .jsonPath("$.ratings.length()").isEqualTo(1)
            .jsonPath("$.comments.length()").isEqualTo(1)
			.jsonPath("$.bookThemeNights.length()").isEqualTo(1);
	}

	@Test
	public void getBookNotFound() {

		getAndVerifyBook(BOOK_ID_NOT_FOUND, NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/book-composite/" + BOOK_ID_NOT_FOUND)
            .jsonPath("$.message").isEqualTo("NOT FOUND: " + BOOK_ID_NOT_FOUND);
	}

	@Test
	public void getProductInvalidInput() {

		getAndVerifyBook(BOOK_ID_INVALID, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/book-composite/" + BOOK_ID_INVALID)
            .jsonPath("$.message").isEqualTo("INVALID: " + BOOK_ID_INVALID);
	}

	private WebTestClient.BodyContentSpec getAndVerifyBook(int bookId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/book-composite/" + bookId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}
	
	private void postAndVerifyBook(BookAggregateModel compositeBook, HttpStatus expectedStatus) {
		client.post()
			.uri("/book-composite")
			.body(just(compositeBook), BookAggregateModel.class)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus);
	}

	private void deleteAndVerifyBook(int bookId, HttpStatus expectedStatus) {
		client.delete()
			.uri("/book-composite/" + bookId)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus);
	}
}
