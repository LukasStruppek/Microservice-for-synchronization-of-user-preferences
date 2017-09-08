/**
 * Singleton, welches die Spring Boot Application startet.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

package de.privacy_avare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SyncServerMain {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SyncServerMain.class);
        /*
         * Platzhalter für zusätzliche Konfigurationen
         */
        app.run(args);
    }
}