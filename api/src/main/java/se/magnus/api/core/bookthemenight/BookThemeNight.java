package se.magnus.api.core.bookthemenight;

import java.util.Date;

public class BookThemeNight {
    private final int bookId;
    private final int bookThemeNightId;
    private final String name;
    private final Date startDate;
    public final String location;
    private final String serviceAddress;

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
}
