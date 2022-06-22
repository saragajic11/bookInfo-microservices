package se.magnus.microservices.core.book.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.magnus.api.core.book.BookModel;
import se.magnus.api.core.book.BookService;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.EventProcessingException;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(Sink.class)
public class MessageProcessor {
	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final BookService bookService;

    @Autowired
    public MessageProcessor(BookService bookService) {
        this.bookService = bookService;
    }
    
    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, BookModel> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
            BookModel book = event.getData();
            LOG.info("Create book with ID: {}", book.getBookId());
            bookService.createBook(book);
            break;

        case DELETE:
            int bookId = event.getKey();
            LOG.info("Delete comments with BookId: {}", bookId);
            bookService.deleteBook(bookId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }



}
