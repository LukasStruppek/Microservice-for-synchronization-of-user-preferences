package de.privacy_avare.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.privacy_avare.service.ClearanceService;

/**
 * Klasse enthält zeitgesteuerte Aufgaben. Zur Aktivierung der Zeitsteuerung ist
 * die Annotation @EnableScheduling in der main-Methode des Programms zu setzen.
 * Detailliertere Informationen zur Festlegung von zeitgesteuerten
 * Methodenaufrufen sind der CronSequenceGenerator-API zu entnehmen.
 * 
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see de.privacy_avare.SyncServerMain
 * @see <a href="https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html">CronSequenceGenerator </a>
 */

@Component
public class ScheduledTasks {

	@Autowired
	ClearanceService clearanceService;

	/**
	 * Löschen deaktivierte Profile aus der Datenbank. Aufruf jeden Montag, 03:00:00
	 * Uhr. Muster für Scheduling: Sekunde, Minute, Stunde, Tag, Monat, Wochentag.
	 * Trennung der einzelnen Parameter durch Leerzeichen.
	 * 
	 * Datenbank wird nach Profilen mit gesetztem unSync-Flag durchsucht, welche
	 * daraufhin gelöscht werden.
	 */
	@Scheduled(cron = "0 0 3 * * MON", zone = "Europe/Berlin")
	public void cleanDataBase() {
		clearanceService.cleanDatabase();
	}
}
