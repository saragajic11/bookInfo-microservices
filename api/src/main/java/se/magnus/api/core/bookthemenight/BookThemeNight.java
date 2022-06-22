package se.magnus.api.core.bookthemenight;

import java.util.Date;

public class BookThemeNight {
    private int bookId;
    private int bookThemeNightId;
    private String name;
    private Date startDate;
    public String location;
    private String serviceAddress;

    public BookThemeNight() {
        bookId = 0;
        bookThemeNightId = 0;
        name = null;
        startDate = null;
        location = null;
        serviceAddress = null;
    }

    public BookThemeNight(int bookId, int bookThemeNightId, String name, Date startDate, String location, String serviceAddress) {
        this.bookId = bookId;
        this.bookThemeNightId = bookThemeNightId;
        this.name = name;
        this.startDate = startDate;
        this.location = location;
        this.serviceAddress = serviceAddress;
    }
    
    public int getBookId() {
        return bookId;
    }
    
    public int getBookThemeNightId() {
        return bookThemeNightId;
    }

    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }
    
    public String getLocation() {
        return location;
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

	public void setBookThemeNightId(int bookThemeNightId) {
		this.bookThemeNightId = bookThemeNightId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	
}
