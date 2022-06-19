package se.magnus.api.core.book;

import java.util.Date;

public class BookModel {
    private final int bookId;
    private final String name;
    private final Date releaseDate;
    public final String language;
    private String serviceAddress;

    public BookModel() {
        bookId = 0;
        name = null;
        releaseDate = null;
        language = null;
        serviceAddress = null;
    }

    public BookModel(int bookId, String name, Date releaseDate, String language, String serviceAddress) {
        this.bookId = bookId;
        this.name = name;
        this.releaseDate = releaseDate;
        this.language = language;
        this.serviceAddress = serviceAddress;
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

    public String getServiceAddress() {
        return serviceAddress;
    }

	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
    
    
}
