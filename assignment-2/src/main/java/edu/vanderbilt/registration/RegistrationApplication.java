package edu.vanderbilt.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class RegistrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistrationApplication.class, args);
	}

	// See: https://springfox.github.io/springfox/docs/snapshot/
	//
	// Provides configuration of the Swagger UI accessible via:
	//
	// http://localhost:8080/swagger-ui
	//
	//
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build()
				.apiInfo(new ApiInfoBuilder()
						.title("Course Registration API")
						.description("The API for the assignment...")
						.license("Your license...")
						.licenseUrl("Example...https://opensource.org/licenses/MIT")
						.build());
	}
}
