package se.magnus.microservices.composite.book.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.composite.book.*;
import se.magnus.util.http.*;
import se.magnus.api.core.book.*;
import se.magnus.api.core.bookthemenight.*;
import se.magnus.api.core.comment.*;
import se.magnus.api.core.rating.*;
import se.magnus.util.http.ServiceUtil;

@RestController
public class BookCompositeServiceImpl implements BookCompositeService {

	private final ServiceUtil serviceUtil;
	private final BookCompositeIntegration bookCompositeIntegration;
	
	@Autowired
	public BookCompositeServiceImpl(ServiceUtil serviceUtil, BookCompositeIntegration bookCompositeIntegration) {
		this.serviceUtil = serviceUtil;
		this.bookCompositeIntegration = bookCompositeIntegration;
	}
	
	@Override
	public BookAggregateModel getBook(int bookId) {
		BookModel book = bookCompositeIntegration.getBook(bookId);
		List<BookThemeNight> bookThemeNights = bookCompositeIntegration.getBookThemeNights(bookId);
		List<Comment> comments = bookCompositeIntegration.getComments(bookId);
		List<Rating> ratings = bookCompositeIntegration.getRatings(bookId);
		
		return createBookAggregate(book, bookThemeNights, comments, ratings, serviceUtil.getServiceAddress());
	}
	
	private BookAggregateModel createBookAggregate(BookModel book,
			List<BookThemeNight> bookThemeNights, List<Comment> comments, List<Rating> ratings,
			String serviceAddress) {

		// 1. Setup product info
		int bookId = book.getBookId();
		String name = book.getName();
		Date releaseDate = book.getReleaseDate();
		String language = book.getLanguage();

		// 2. Copy summary recommendation info, if available
		List<BookThemeNightSummary> bookThemeNightSummary = (bookThemeNights == null) ? null
				: bookThemeNights.stream().map(r -> new BookThemeNightSummary(r.getBookThemeNightId(), r.getName(), r.getStartDate(),
						r.getLocation())).collect(Collectors.toList());

		// 3. Copy summary review info, if available
		List<CommentSummary> commentSummary = (comments == null) ? null
				: comments.stream().map(r -> new CommentSummary(r.getCommentId(), r.getAuthor(),
						r.getContent())).collect(Collectors.toList());

		// 4. Copy summary transactions info, if available
		List<RatingSummary> ratingSummary = (ratings == null) ? null
				: ratings.stream()
						.map(r -> new RatingSummary(r.getRatingId(), r.getAuthor(), r.getRating()))
						.collect(Collectors.toList());

		// 5. Create info regarding the involved microservices addresses
		String bookAddress = book.getServiceAddress();
		String bookThemeNightAddress = (bookThemeNights != null && bookThemeNights.size() > 0) ? bookThemeNights.get(0).getServiceAddress()
				: "";
		String commentAddress = (comments != null && comments.size() > 0)
				? comments.get(0).getServiceAddress()
				: "";
		String ratingAddress = (ratings != null && ratings.size() > 0)
				? ratings.get(0).getServiceAddress()
				: "";
		ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, bookAddress,
				bookThemeNightAddress, commentAddress, ratingAddress);

		return new BookAggregateModel(bookId, name, releaseDate, language, ratingSummary, commentSummary,
				bookThemeNightSummary, serviceAddresses);
	}
}
