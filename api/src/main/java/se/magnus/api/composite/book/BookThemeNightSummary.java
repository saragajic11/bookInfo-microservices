package se.magnus.api.composite.book;


import java.util.Date;

public class BookThemeNightSummary {

    private final int bookThemeNightId;
    private final String name;
    private final Date startDate;
    public final String location;

    public BookThemeNightSummary(int bookThemeNightId, String name, Date startDate, String location) {
        this.bookThemeNightId = bookThemeNightId;
        this.name = name;
        this.startDate = startDate;
        this.location = location;
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
}
