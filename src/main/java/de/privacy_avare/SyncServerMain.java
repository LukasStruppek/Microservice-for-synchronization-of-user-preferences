package de.privacy_avare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Einstiegspunkt für das Server-Programm. Startet automatisch einen
 * embedded Tomcat-Server. 
 * 
 * In der main-Methode können weitere Konfigurationen vorgenommen werden.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

@SpringBootApplication
@ComponentScan ({"de.privacy_Avare.repository"})
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