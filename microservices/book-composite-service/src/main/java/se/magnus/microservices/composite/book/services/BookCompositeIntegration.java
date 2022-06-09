package se.magnus.microservices.composite.book.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import static org.springframework.http.HttpMethod.GET;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.magnus.api.core.book.*;
import se.magnus.api.core.bookthemenight.*;
import se.magnus.api.core.comment.*;
import se.magnus.api.core.rating.*;

@Component
public class BookCompositeIntegration implements BookService, BookThemeNightService, CommentService, RatingService {
	
	private final RestTemplate restTemplate;
	private final ObjectMapper mapper;
	
	private final String bookServiceUrl;
	private final String bookThemeNightServiceUrl;
	private final String commentServiceUrl;
	private final String ratingServiceUrl;
	
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
		String url = bookServiceUrl + bookId;
		BookModel book = restTemplate.getForObject(url, BookModel.class);
		return book;
	}
	
	public List<BookThemeNight> getBookThemeNights(int bookId) {
		String url = bookThemeNightServiceUrl + bookId;
		List<BookThemeNight> bookThemeNights = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<BookThemeNight>>() {
			
		}).getBody();
		return bookThemeNights;
	}
	
	public List<Comment> getComments(int bookId) {
		String url = commentServiceUrl + bookId;
		List<Comment> comments = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Comment>>() {
		}).getBody();
		
		return comments;
	}
	
	public List<Rating> getRatings(int bookId) {
		String url = ratingServiceUrl + bookId;
		List<Rating> ratings = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Rating>>() {
		}).getBody();
		
		return ratings;
	}
}
