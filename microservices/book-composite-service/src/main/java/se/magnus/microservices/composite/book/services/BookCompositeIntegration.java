package se.magnus.microservices.composite.book.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import static org.springframework.http.HttpMethod.GET;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.magnus.api.core.book.*;
import se.magnus.api.core.bookthemenight.*;
import se.magnus.api.core.comment.*;
import se.magnus.api.core.rating.*;

import se.magnus.util.http.ServiceUtil;
import se.magnus.util.http.*;
import se.magnus.util.exceptions.*;

@Component
public class BookCompositeIntegration implements BookService, BookThemeNightService, CommentService, RatingService {
	
	private final RestTemplate restTemplate;
	private final ObjectMapper mapper;
	
	private final String bookServiceUrl;
	private final String bookThemeNightServiceUrl;
	private final String commentServiceUrl;
	private final String ratingServiceUrl;
	

	private static final Logger LOG = LoggerFactory.getLogger(BookCompositeIntegration.class);
	
	@Autowired
	public BookCompositeIntegration(RestTemplate restTemplate, ObjectMapper mapper, @Value("${app.book-service.host}") String bookServiceHost,  @Value("${app.book-service.port}") int bookServicePort, @Value("${app.book-theme-night-service.host}") String bookThemeNightServiceHost,  @Value("${app.book-theme-night-service.port}") int bookThemeNightServicePort, @Value("${app.comment-service.host}") String commentServiceHost,  @Value("${app.comment-service.port}") int commentServicePort, @Value("${app.rating-service.host}") String ratingServiceHost,  @Value("${app.rating-service.port}") int ratingServicePort) {
		this.restTemplate = restTemplate;
		this.mapper = mapper;
		
		bookServiceUrl = "http://" + bookServiceHost + ":" +   bookServicePort + "/book/";
		bookThemeNightServiceUrl = "http://" + bookThemeNightServiceHost  + ":" + bookThemeNightServicePort + "/book-theme-night?bookId=";
		commentServiceUrl = "http://" + commentServiceHost +   ":" + commentServicePort + "/comment?bookId=";
		ratingServiceUrl = "http://" + ratingServiceHost +   ":" + ratingServicePort + "/rating?bookId=";
	}
	
	public BookModel getBook(int bookId) {
		try {
			String url = bookServiceUrl + bookId;
			BookModel book = restTemplate.getForObject(url, BookModel.class);
			LOG.warn("Aha ovde sam, bookId je: ");
			return book;
		} catch (HttpClientErrorException ex) {

			switch (ex.getStatusCode()) {

			case NOT_FOUND:
				throw new NotFoundException(getErrorMessage(ex));

			case UNPROCESSABLE_ENTITY:
				throw new InvalidInputException(getErrorMessage(ex));

			default:
				LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
				LOG.warn("Error body: {}", ex.getResponseBodyAsString());
				throw ex;
			}
		}
	}
	
	private String getErrorMessage(HttpClientErrorException ex) {
		try {
			return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
		} catch (IOException ioex) {
			return ex.getMessage();
		}
	}
	
	public List<BookThemeNight> getBookThemeNights(int bookId) {
		try {
			String url = bookThemeNightServiceUrl + bookId;
			List<BookThemeNight> bookThemeNights = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<BookThemeNight>>() {
				
			}).getBody();
			return bookThemeNights;
		} catch (Exception ex) {
			LOG.warn("Got an exception while requesting book theme nights, return zero book theme nights: {}",
					ex.getMessage());
			return new ArrayList<>();
		}
	}
	
	public List<Comment> getComments(int bookId) {
		try {
			String url = commentServiceUrl + bookId;
			List<Comment> comments = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Comment>>() {
			}).getBody();
			
			return comments;
		} catch (Exception ex) {
			LOG.warn("Got an exception while requesting comments, return zero comments: {}",
					ex.getMessage());
			return new ArrayList<>();
		}
	}
	
	public List<Rating> getRatings(int bookId) {
		try {
			String url = ratingServiceUrl + bookId;
			List<Rating> ratings = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Rating>>() {
			}).getBody();
			
			return ratings;
		} catch (Exception ex) {
			LOG.warn("Got an exception while requesting ratings, return zero ratings: {}",
					ex.getMessage());
			return new ArrayList<>();
		}
	}
}
