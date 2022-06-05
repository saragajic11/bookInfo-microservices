package se.magnus.api.composite.book;

public class RatingSummary {

	private final int ratingId;
    private final int rating;
    private final String author;

    public RatingSummary(int ratingId, String author, int rating) {
        this.ratingId = ratingId;
        this.author = author;
        this.rating = rating;
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
}
