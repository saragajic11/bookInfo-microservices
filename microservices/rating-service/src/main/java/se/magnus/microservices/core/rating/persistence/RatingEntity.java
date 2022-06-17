package se.magnus.microservices.core.rating.persistence;

import javax.persistence.*;

@Entity
@Table(name = "ratings", indexes = {
		@Index(name = "ratings_unique_idx", unique = true, columnList = "bookId,ratingId") })
public class RatingEntity {
	
	@Id
	@GeneratedValue
	private int id;

	@Version
	private int version;
	
    private int bookId;
    private int ratingId;
    private int rating;
    private String author;
    
    public RatingEntity() {
    	
    }
    
    public RatingEntity(int bookId, int ratingId, String author, int rating) {
        this.bookId = bookId;
        this.ratingId = ratingId;
        this.author = author;
        this.rating = rating;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public int getRatingId() {
		return ratingId;
	}

	public void setRatingId(int ratingId) {
		this.ratingId = ratingId;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
    
    

}
