package se.magnus.microservices.core.bookthemenight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("se.magnus")
public class BookThemeNightServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookThemeNightServiceApplication.class, args);
	}

}
