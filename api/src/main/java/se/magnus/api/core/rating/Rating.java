package se.magnus.api.core.rating;

public class Rating {
    private final int bookId;
    private final int ratingId;
    private final int rating;
    private final String author;
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
        return rating;
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
}
