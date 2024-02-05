package com.school.sba.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;


@Configuration
@OpenAPIDefinition
public class ApplicationDocumentation {
	// Configures the general information about the API
		/*
		 * Info info(): This method configures general information about the API, such
		 * as its title, version, and description. The contact() method is called to set
		 * contact information.
		 */
		Info info() {
			return new Info().title("User Management System API").version("1.0v")
					.description("User Management System is a RESTful API built using " + " Spring Boot and MySQL Database")
					.contact(contact());
		}

		// Configures the contact information for the API
		/*
		 * Contact contact(): This method configures the contact information for the
		 * API, including the name, email, and URL of the contact person or team.
		 */
		Contact contact() {
			return new Contact().name("Devil")
					.email("sri09061998@gmail.com")
					.url("github.com/srinivas1457");
		}

		// Creates and configures the OpenAPI bean for Swagger documentation
		/*
		 * @Bean OpenAPI openAPI(): This method creates and configures the OpenAPI bean
		 * for Swagger documentation. It sets the API information by invoking the info()
		 * method.
		 */
		@Bean
		OpenAPI openAPI() {
			return new OpenAPI().info(info());
		}

}
