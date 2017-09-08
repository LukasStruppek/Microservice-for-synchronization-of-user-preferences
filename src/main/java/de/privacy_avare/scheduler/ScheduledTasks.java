/**
 * @author Lukas Struppek
 * @version 1.0
 * 
 * Klasse enthält zeitgesteuerte Aufgaben.
 */

package de.privacy_avare.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
	/**
	 * Löschen deaktivierte Profile aus der Datenbank. Aufruf jeden Montag, 03:00:00 Uhr.
	 * Uhr Muster für Scheduling: Sekunde, Minute, Stunde, Tag, Monat, Wochentag.
	 * Trennung der einzelnen Parameter durch Leerzeichen.
	 */
	@Scheduled(cron = "0 0 3 * * MON")
	public void cleanDataBase() {
		/*
		 * Platzhalter für Code Löschen aller deaktivierten Profile in der Datenbank
		 */
	}
}
