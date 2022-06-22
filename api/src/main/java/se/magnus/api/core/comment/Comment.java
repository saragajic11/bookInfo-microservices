package se.magnus.api.core.comment;

public class Comment {
    private int bookId;
    private int commentId;
    private String author;
    private String content;
    private String serviceAddress;

    public Comment() {
        bookId = 0;
        commentId = 0;
        author = null;
        content = null;
        serviceAddress = null;
    }

    public Comment(int bookId, int commentId, String author, String content, String serviceAddress) {
        this.bookId = bookId;
        this.commentId = commentId;
        this.author = author;
        this.content = content;
        this.serviceAddress = serviceAddress;
    }

    public int getBookId() {
        return bookId;
    }

    public int getCommentId() {
        return commentId;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
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

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
