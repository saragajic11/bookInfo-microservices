package se.magnus.api.composite.book;

import java.util.List;
import java.util.Date;

public class BookAggregateModel {
    private final int bookId;
    private final String name;
    private final Date releaseDate;
    private final String language;
    private final List<RatingSummary> ratings;
    private final List<CommentSummary> comments;
    private final List<BookThemeNightSummary> bookThemeNights;
    private final ServiceAddresses serviceAddresses;
    
    public BookAggregateModel() {
        bookId = 0;
        name = null;
        releaseDate = null;
        language = null;
        ratings = null;
        comments = null;
        bookThemeNights = null;
        serviceAddresses = null;
    }
    
    public BookAggregateModel(int bookId, String name, Date releaseDate, String language, List<RatingSummary> ratings, List<CommentSummary> comments, List<BookThemeNightSummary> bookThemeNights, ServiceAddresses serviceAddresses) {
        this.bookId = bookId;
        this.name = name;
        this.releaseDate = releaseDate;
        this.language = language;
        this.ratings = ratings;
        this.comments = comments;
        this.bookThemeNights = bookThemeNights;
        this.serviceAddresses = serviceAddresses;
    }
    
    public int getBookId() {
        return bookId;
    }

    public String getName() {
        return name;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }
    
    public String getLanguage() {
        return language;
    }

    public ServiceAddresses getServiceAddresses() {
        return serviceAddresses;
    }

    public List<RatingSummary> getRatings() {
        return ratings;
    }

    public List<CommentSummary> getComments() {
        return comments;
    }
    
    public List<BookThemeNightSummary> getBookThemeNights() {
        return bookThemeNights;
    }
}
