package microservices.composite.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("se.magnus")
public class BookCompositeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookCompositeServiceApplication.class, args);
	}

}
