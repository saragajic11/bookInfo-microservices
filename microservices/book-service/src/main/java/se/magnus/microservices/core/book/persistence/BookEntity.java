package se.magnus.microservices.core.book.persistence;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="books")
public class BookEntity {

	@Id
	private String id;
	
	@Version
	private Integer version;
	
	@Indexed(unique = true)
	private int bookId;
	
    private String name;
    private Date releaseDate;
    public String language;
    
    public BookEntity() {
    	
    }
    
    public BookEntity(int bookId, String name, Date releaseDate, String language) {
        this.bookId = bookId;
        this.name = name;
        this.releaseDate = releaseDate;
        this.language = language;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
    
    
}
