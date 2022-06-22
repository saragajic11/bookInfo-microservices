package se.magnus.microservices.core.bookthemenight.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.magnus.api.core.bookthemenight.BookThemeNight;
import se.magnus.api.core.bookthemenight.BookThemeNightService;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final BookThemeNightService bookThemeNightService;

    @Autowired
    public MessageProcessor(BookThemeNightService bookThemeNightService) {
        this.bookThemeNightService = bookThemeNightService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, BookThemeNight> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
        	BookThemeNight bookThemeNight = event.getData();
            LOG.info("Create book theme night with ID: {}/{}", bookThemeNight.getBookId(), bookThemeNight.getBookThemeNightId());
            bookThemeNightService.createBookThemeNight(bookThemeNight);
            break;

        case DELETE:
            int bookId = event.getKey();
            LOG.info("Delete book theme night with BookId: {}", bookId);
            bookThemeNightService.deleteBookThemeNight(bookId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }

}
