package de.privacy_avare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.core.mapping.event.ValidatingCouchbaseEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Klasse dient zur Konfiguration von Data Validation. Hierzu zählt die Nutzung
 * von Annotations gemäß JSR303 Bean Validation.
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see <a href = "http://beanvalidation.org/1.0/spec/"> JSR303 Bean Validation
 *      </a>
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
