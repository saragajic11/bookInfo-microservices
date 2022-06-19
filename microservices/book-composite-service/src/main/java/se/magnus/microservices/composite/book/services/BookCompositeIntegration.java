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
	
	@Override
	public BookModel getBook(int bookId) {
        try {
            String url = bookServiceUrl + "/" + bookId;
            LOG.debug("Will call the getBook API by URL: {}", url);

            BookModel book = restTemplate.getForObject(url, BookModel.class);
            LOG.debug("Found a book with id: {}", book.getBookId());

            return book;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
	}
	
	@Override
	public BookModel createBook(BookModel body) {

		try {
			String url = bookServiceUrl;
			LOG.debug("Will post a new book to URL: {}", url);

			BookModel book = restTemplate.postForObject(url, body, BookModel.class);
			LOG.debug("Created a book with id: {}", book.getBookId());

			return book;

		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}
	
	@Override
	public void deleteBook(int bookId) {
		try {
			String url = bookServiceUrl + "?bookId=" + bookId;
			LOG.debug("Will call the deleteBook API on URL: {}", url);

			restTemplate.delete(url);

		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}
	
	@Override
    public BookThemeNight createBookThemeNight(BookThemeNight body) {

        try {
            String url = bookThemeNightServiceUrl;
            LOG.debug("Will post a new bookThemeNight to URL: {}", url);

            BookThemeNight bookThemeNight = restTemplate.postForObject(url, body, BookThemeNight.class);
            LOG.debug("Created a book theme night with id: {}", bookThemeNight.getBookId());

            return bookThemeNight;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
	
	@Override
    public List<BookThemeNight> getBookThemeNights(int bookId) {

        try {
            String url = bookThemeNightServiceUrl + "?bookId=" + bookId;

            LOG.debug("Will call the getBookThemeNights API on URL: {}", url);
            List<BookThemeNight> bookThemeNights = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<BookThemeNight>>() {}).getBody();

            LOG.debug("Found {} bookThemeNights for a book with id: {}", bookThemeNights.size(), bookId);
            return bookThemeNights;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting bookThemeNights, return zero bookThemeNights: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteBookThemeNight(int bookId) {
        try {
            String url = bookThemeNightServiceUrl + "?bookId=" + bookId;
            LOG.debug("Will call the deleteBookThemeNights API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    
	
	@Override
    public Comment createComment(Comment body) {

        try {
            String url = commentServiceUrl;
            LOG.debug("Will post a new comment to URL: {}", url);

            Comment comment = restTemplate.postForObject(url, body, Comment.class);
            LOG.debug("Created a comment with id: {}", comment.getBookId());

            return comment;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
	
	@Override
    public List<Comment> getComments(int bookId) {

        try {
            String url = commentServiceUrl + "?bookId=" + bookId;

            LOG.debug("Will call the getComments API on URL: {}", url);
            List<Comment> comments = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Comment>>() {}).getBody();

            LOG.debug("Found {} bookThemeNights for a book with id: {}", comments.size(), bookId);
            return comments;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting bookThemeNights, return zero bookThemeNights: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteComment(int bookId) {
        try {
            String url = commentServiceUrl + "?bookId=" + bookId;
            LOG.debug("Will call the deleteComment API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
    
	@Override
    public Rating createRating(Rating body) {

        try {
            String url = ratingServiceUrl;
            LOG.debug("Will post a new comment to URL: {}", url);

            Rating rating = restTemplate.postForObject(url, body, Rating.class);
            LOG.debug("Created a rating with id: {}", rating.getBookId());

            return rating;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
	
	@Override
    public List<Rating> getRatings(int bookId) {

        try {
            String url = ratingServiceUrl + "?bookId=" + bookId;

            LOG.debug("Will call the getRatings API on URL: {}", url);
            List<Rating> ratings = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Rating>>() {}).getBody();

            LOG.debug("Found {} ratings for a book with id: {}", ratings.size(), bookId);
            return ratings;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting ratings, return zero ratings: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteRating(int bookId) {
        try {
            String url = ratingServiceUrl + "?bookId=" + bookId;
            LOG.debug("Will call the deleteRating API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }
	
	private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {

        case NOT_FOUND:
            return new NotFoundException(getErrorMessage(ex));

        case UNPROCESSABLE_ENTITY :
            return new InvalidInputException(getErrorMessage(ex));

        default:
            LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
            LOG.warn("Error body: {}", ex.getResponseBodyAsString());
            return ex;
        }
    }

	
	private String getErrorMessage(HttpClientErrorException ex) {
		try {
			return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
		} catch (IOException ioex) {
			return ex.getMessage();
		}
	}
	
	
}
