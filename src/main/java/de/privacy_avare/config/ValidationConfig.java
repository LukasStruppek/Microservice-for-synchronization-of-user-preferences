package de.privacy_avare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.core.mapping.event.ValidatingCouchbaseEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Klasse dient zur Konfiguration innerhalb des Spring-Frameworks und dient als
 * Alternative eines xml-Files für Einstellungen.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

@Configuration
public class ValidationConfig {
	@Bean
	public LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public ValidatingCouchbaseEventListener validationEventListener() {
		return new ValidatingCouchbaseEventListener(validator());
	}

	// Erlaubt Annotations gemäß JSR303 annotations
	// Umsetzung noch nicht erfolgt
}
