package se.magnus.api.core.rating;

public class Rating {
    private int bookId;
    private int ratingId;
    private int rating;
    private String author;
    private String serviceAddress;

    public Rating() {
        bookId = 0;
        ratingId = 0;
        author = null;
        rating = 0;
        serviceAddress = null;
    }

    public Rating(int bookId, int ratingId, String author, int rating, String serviceAddress) {
        this.bookId = bookId;
        this.ratingId = ratingId;
        this.author = author;
        this.rating = rating;
        this.serviceAddress = serviceAddress;
    }

    public int getBookId() {
        return bookId;
    }

    public int getRatingId() {
        return ratingId;
    }

    public String getAuthor() {
        return author;
    }

    public int getRating() {
        return rating;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }
    
	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public void setRatingId(int ratingId) {
		this.ratingId = ratingId;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	
}
