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

 import org.apache.catalina.Context;
 import org.apache.catalina.connector.Connector;
 import org.apache.tomcat.util.descriptor.web.SecurityCollection;
 import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
 import
 org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
 import
 org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;

 /**
  * Klasse dient zur Konfiguration der SSL-Verbindung und weiterer Einstellungen bezüglich der REST-Schnittstelle bzw. HTTP-Verbindung.
  * @author Lukas Struppek
  * @version 1.0
  *
  */
 
@Configuration
public class SSLConfig {
	// @Bean
	// public EmbeddedServletContainerFactory servletContainer() {
	// TomcatEmbeddedServletContainerFactory tomcat = new
	// TomcatEmbeddedServletContainerFactory() {
	// @Override
	// protected void postProcessContext(Context context) {
	// SecurityConstraint securityConstraint = new SecurityConstraint();
	// securityConstraint.setUserConstraint("CONFIDENTIAL");
	// SecurityCollection collection = new SecurityCollection();
	// collection.addPattern("/*");
	// securityConstraint.addCollection(collection);
	// context.addConstraint(securityConstraint);
	// }
	// };
	//
	// tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
	// return tomcat;
	// }
	//
	// private Connector initiateHttpConnector() {
	// Connector connector = new
	// Connector("org.apache.coyote.http11.Http11NioProtocol");
	// connector.setScheme("http");
	// connector.setPort(8080);
	// connector.setSecure(false);
	// connector.setRedirectPort(8443);
	//
	// return connector;
	// }
}
