package de.privacy_avare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.service.Contact;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Klasse dient der Aktivierung und Konfiguration von Swagger bzw. Swagger UI.
 * Swagger dient zur Dokumentation der bereitgestellten REST-Schnittstellen.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).useDefaultResponseMessages(false).apiInfo(setApiInfo()).select()
				.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)).paths(PathSelectors.any())
				.build();
	}

	private ApiInfo setApiInfo() {
		return new ApiInfoBuilder().title("Avare SyncServer REST API")
				.description("Dokumentation der REST API für die Nutzung des Avare SyncServers")
				.contact(new Contact("Lukas Struppek", "http://www.privacy-avare.de/", "lukas.struppek@gmail.com"))
				.license("Apache License Version 2.0").licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
				.version("1.0").build();

	}
}