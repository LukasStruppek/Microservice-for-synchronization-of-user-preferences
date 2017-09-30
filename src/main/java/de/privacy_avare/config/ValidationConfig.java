package de.privacy_avare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Klasse dient zur Konfiguration von Data Validation. Hierzu zählt die Nutzung
 * von Annotations gemäß JSR303 Bean Validation. Bisher noch nicht in
 * Verwendung.
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see <a href = "http://beanvalidation.org/1.0/spec/"> JSR303 Bean Validation
 *      </a>
 */

@Configuration
public class ValidationConfig {
	/**
	 * Setup für Data Validation.
	 * @return LocalValidatorFactoryBean
	 */
	@Bean
	public LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}
}
