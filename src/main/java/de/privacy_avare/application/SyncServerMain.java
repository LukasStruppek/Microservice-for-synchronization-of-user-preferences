/**
 * Singleton, welches die Spring Boot Application startet.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

package de.privacy_avare.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SyncServerMain {
    public static void main(String[] args) {
        SpringApplication.run(SyncServerMain.class, args);
    }
}