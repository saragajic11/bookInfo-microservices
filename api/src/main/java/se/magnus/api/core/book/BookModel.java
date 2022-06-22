package se.magnus.api.core.book;

import java.util.Date;

public class BookModel {
    private int bookId;
    private String name;
    private Date releaseDate;
    public String language;
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

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
    
    
}
