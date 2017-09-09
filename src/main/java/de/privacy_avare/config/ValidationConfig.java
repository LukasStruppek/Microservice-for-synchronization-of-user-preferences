package de.privacy_avare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.core.mapping.event.ValidatingCouchbaseEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

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
	
	//Erlaubt Annotations gemäß JSR303 annotations
}
