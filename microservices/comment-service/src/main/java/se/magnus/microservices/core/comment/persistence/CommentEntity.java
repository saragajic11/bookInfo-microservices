package se.magnus.microservices.core.comment.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

@Document(collection="comments")
@CompoundIndex(name = "book-comment-id", unique = true, def = "{'bookId': 1, 'commentId' : 1}")
public class CommentEntity {
	
    @Id
    private String id;

    @Version
    private Integer version;

    private int bookId;
    private int commentId;
    private String author;
    private String content;
    
    public CommentEntity() {
    	
    }
    
    public CommentEntity(int bookId, int commentId, String author, String content) {
        this.bookId = bookId;
        this.commentId = commentId;
        this.author = author;
        this.content = content;
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

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
    
    
}
