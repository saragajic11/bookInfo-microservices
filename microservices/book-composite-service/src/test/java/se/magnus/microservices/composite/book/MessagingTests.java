package se.magnus.microservices.composite.book;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.composite.book.*;
import se.magnus.api.core.book.BookModel;
import se.magnus.api.core.comment.Comment;
import se.magnus.api.core.bookthemenight.BookThemeNight;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.event.Event;
import se.magnus.microservices.composite.book.services.BookCompositeIntegration;

import java.util.concurrent.BlockingQueue;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;
import static org.springframework.http.HttpStatus.OK;
import static reactor.core.publisher.Mono.just;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;
import static se.magnus.microservices.composite.book.IsSameEvent.sameEventExceptCreatedAt;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"eureka.client.enabled=false"})
public class MessagingTests {
	
	private static final int BOOK_ID_OK = 1;
	private static final int BOOK_ID_NOT_FOUND = 2;
	private static final int BOOK_ID_INVALID = 3;

    @Autowired
    private WebTestClient client;
    
    @Autowired
	private BookCompositeIntegration.MessageSources channels;

	@Autowired
	private MessageCollector collector;
	
	BlockingQueue<Message<?>> queueBooks = null;
	BlockingQueue<Message<?>> queueComments = null;
	BlockingQueue<Message<?>> queueBookThemeNights = null;
	BlockingQueue<Message<?>> queueRatings = null;
	
	@Before
	public void setUp() {
		queueBooks = getQueue(channels.outputBooks());
		queueComments = getQueue(channels.outputComments());
		queueBookThemeNights = getQueue(channels.outputBookThemeNights());
		queueRatings = getQueue(channels.outputRatings());
	}
	
	@Test
	public void createCompositeBook1() {

		BookAggregateModel composite = new BookAggregateModel(1, "name", new Date(), "language", null, null, null, null);
		postAndVerifyBook(composite, OK);

		// Assert one expected new book events queued up
		//assertEquals(1, queueBooks.size());

		Event<Integer, BookModel> expectedEvent = new Event(CREATE, composite.getBookId(), new BookModel(composite.getBookId(), composite.getName(), composite.getReleaseDate(), composite.getLanguage(), null));
		//assertThat(queueBooks, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

		// Assert none comments, bookThemeNights and rating events
		assertEquals(0, queueComments.size());
		assertEquals(0, queueBookThemeNights.size());
		assertEquals(0, queueRatings.size());
	}
	
	@Test
	public void createCompositeBook2() {

		BookAggregateModel composite = new BookAggregateModel(1, "name", new Date(), "language",
			singletonList(new RatingSummary(1, "author", 1)),
			singletonList(new CommentSummary(1, "author", "content")),
			singletonList(new BookThemeNightSummary(1, "name", new Date(), "location")),
			null);

		postAndVerifyBook(composite, OK);

		// Assert one create book event queued up
		//assertEquals(1, queueBooks.size());

		Event<Integer, BookModel> expectedBookEvent = new Event(CREATE, composite.getBookId(), new BookModel(composite.getBookId(), composite.getName(), composite.getReleaseDate(), composite.getLanguage(), null));
		//assertThat(queueBooks, receivesPayloadThat(sameEventExceptCreatedAt(expectedBookEvent)));

		// Assert one create rating event queued up
		assertEquals(1, queueRatings.size());

		RatingSummary rat = composite.getRatings().get(0);
		Event<Integer, BookModel> expectedRatingEvent = new Event(CREATE, composite.getBookId(), new Rating(composite.getBookId(), rat.getRatingId(), rat.getAuthor(), rat.getRating(), null));
		//assertThat(queueRatings, receivesPayloadThat(sameEventExceptCreatedAt(expectedRatingEvent)));

		// Assert one create comment event queued up
		assertEquals(1, queueComments.size());

		CommentSummary com = composite.getComments().get(0);
		Event<Integer, BookModel> expectedCommentEvent = new Event(CREATE, composite.getBookId(), new Comment(composite.getBookId(), com.getCommentId(), com.getAuthor(), com.getContent(), null));
		//assertThat(queueComments, receivesPayloadThat(sameEventExceptCreatedAt(expectedCommentEvent)));
		
		// Assert one create bookThemeNight event queued up
		assertEquals(1, queueBookThemeNights.size());

		BookThemeNightSummary btn = composite.getBookThemeNights().get(0);
		Event<Integer, BookModel> expectedBookThemeNightEvent = new Event(CREATE, composite.getBookId(), new BookThemeNight(composite.getBookId(), btn.getBookThemeNightId(), btn.getName(), btn.getStartDate(), btn.getLocation(), null));
		//assertThat(queueBookThemeNights, receivesPayloadThat(sameEventExceptCreatedAt(expectedBookThemeNightEvent)));
	}
	
//	@Test
//	public void deleteCompositeBook() {
//
//		deleteAndVerifyBook(1, OK);
//
//		// Assert one delete book event queued up
//		//assertEquals(1, queueBooks.size());
//
//		Event<Integer, BookModel> expectedEvent = new Event(DELETE, 1, null);
//		//assertThat(queueBooks, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));
//
//		// Assert one delete rating event queued up
//		assertEquals(1, queueRatings.size());
//
//		Event<Integer, BookModel> expectedRatingEvent = new Event(DELETE, 1, null);
//		//assertThat(queueRatings, receivesPayloadThat(sameEventExceptCreatedAt(expectedRatingEvent)));
//
//		// Assert one delete comment event queued up
//		assertEquals(1, queueComments.size());
//
//		Event<Integer, BookModel> expectedCommentEvent = new Event(DELETE, 1, null);
//		//assertThat(queueComments, receivesPayloadThat(sameEventExceptCreatedAt(expectedCommentEvent)));
//		
//		// Assert one delete bookThemeNight event queued up
//		assertEquals(1, queueBookThemeNights.size());
//
//		Event<Integer, BookModel> expectedBookThemeNightEvent = new Event(DELETE, 1, null);
//		//assertThat(queueBookThemeNights, receivesPayloadThat(sameEventExceptCreatedAt(expectedBookThemeNightEvent)));
//	}
	
	private BlockingQueue<Message<?>> getQueue(MessageChannel messageChannel) {
		return collector.forChannel(messageChannel);
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
