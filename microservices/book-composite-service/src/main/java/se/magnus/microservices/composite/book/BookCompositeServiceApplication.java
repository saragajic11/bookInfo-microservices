package se.magnus.microservices.composite.book;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import static java.util.Collections.emptyList;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
@ComponentScan("se.magnus")
public class BookCompositeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookCompositeServiceApplication.class, args);
	}
	
	@Value("${api.common.version}")           String apiVersion;
    @Value("${api.common.title}")             String apiTitle;
    @Value("${api.common.description}")       String apiDescription;
    @Value("${api.common.termsOfServiceUrl}") String apiTermsOfServiceUrl;
    @Value("${api.common.license}")           String apiLicense;
    @Value("${api.common.licenseUrl}")        String apiLicenseUrl;
    @Value("${api.common.contact.name}")      String apiContactName;
    @Value("${api.common.contact.url}")       String apiContactUrl;
    @Value("${api.common.contact.email}")     String apiContactEmail;
	

	@Bean
	public Docket apiDocumentation() {

		return new Docket(SWAGGER_2)
			.select()
			.apis(basePackage("se.magnus.microservices.composite.book"))
			.paths(PathSelectors.any())
			.build()
				.globalResponseMessage(POST, emptyList())
				.globalResponseMessage(GET, emptyList())
				.globalResponseMessage(DELETE, emptyList())
				.apiInfo(new ApiInfo(
                   apiTitle,
                   apiDescription,
                   apiVersion,
                   apiTermsOfServiceUrl,
                   new Contact(apiContactName, apiContactUrl, apiContactEmail),
                   apiLicense,
                   apiLicenseUrl,
                   emptyList()
               ));
   }
	
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	

}
