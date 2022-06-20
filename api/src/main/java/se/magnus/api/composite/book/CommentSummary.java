package se.magnus.api.composite.book;

public class CommentSummary {

    private final int commentId;
    private final String author;
    private final String content;
    
    public CommentSummary() {
        this.commentId = 0;
        this.author = null;
        this.content = null;
    }

    public CommentSummary(int commentId, String author, String content) {
        this.commentId = commentId;
        this.author = author;
        this.content = content;
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
}
