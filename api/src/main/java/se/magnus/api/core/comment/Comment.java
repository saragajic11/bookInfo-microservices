package se.magnus.api.core.comment;

public class Comment {
    private final int bookId;
    private final int commentId;
    private final String author;
    private final String content;
    private final String serviceAddress;

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
}
