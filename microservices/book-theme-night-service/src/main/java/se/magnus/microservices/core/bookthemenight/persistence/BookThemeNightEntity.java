package se.magnus.microservices.core.bookthemenight.persistence;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

@Document(collection="comments")
@CompoundIndex(name = "book-book-theme-night-id", unique = true, def = "{'bookId': 1, 'bookThemeNightId' : 1}")
public class BookThemeNightEntity {

    @Id
    private String id;

    @Version
    private Integer version;
    
    private int bookId;
    private int bookThemeNightId;
    private String name;
    private Date startDate;
    private String location;
    
    public BookThemeNightEntity() {
    	
    }
    
    public BookThemeNightEntity(int bookId, int bookThemeNightId, String name, Date startDate, String location) {
        this.bookId = bookId;
        this.bookThemeNightId = bookThemeNightId;
        this.name = name;
        this.startDate = startDate;
        this.location = location;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public int getBookThemeNightId() {
		return bookThemeNightId;
	}

	public void setBookThemeNightId(int bookThemeNightId) {
		this.bookThemeNightId = bookThemeNightId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
    
    
    
    
}
