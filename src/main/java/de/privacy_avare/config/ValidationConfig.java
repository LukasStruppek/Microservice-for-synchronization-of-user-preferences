/*
 * Copyright 2017 Lukas Struppek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
