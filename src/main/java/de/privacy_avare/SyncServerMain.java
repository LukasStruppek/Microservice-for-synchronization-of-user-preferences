package de.privacy_avare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Einstiegspunkt für das Server-Programm. Startet automatisch einen embedded
 * Tomcat-Server.
 * 
 * In der main-Methode können weitere Konfigurationen vorgenommen werden.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

@SpringBootApplication
@EnableScheduling
public class SyncServerMain {
	/**
	 * Methode dient als Einstiegspunkt des Server-Programms.
	 * 
	 * @param args
	 *            Zusätzliche Parameter, die beim Programmstart übergeben werden.
	 */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(SyncServerMain.class);
		app.run(args);
	}
}