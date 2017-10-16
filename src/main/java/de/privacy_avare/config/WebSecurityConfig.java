package de.privacy_avare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Klasse dient zur Konfiguration von BasicAuth für Swagger-UI. Weitere
 * Sicherheitseinstellungen können vorgenommen werden.
 * 
 * @author Lukas Struppek
 * @version 1.0
 *
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * Methode dient zur Konfiguration des UserManagers. Damit werden u.a.
	 * bestehende Nutzer verwaltet.
	 * 
	 * @return Konfigurierter UserDetailsService
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(User.withUsername("admin").password("password").roles("Admin").build());
		return manager;
	}

	/**
	 * Legt für Swagger-Ui eine Authorisierung fest.
	 * 
	 * @param http
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers("/swagger-ui.html").authenticated().antMatchers("/v1/**").permitAll().and().httpBasic().and().csrf().disable();
	}
}
