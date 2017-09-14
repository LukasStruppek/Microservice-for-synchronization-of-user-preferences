package de.privacy_avare.service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.privacy_avare.domain.Profile;
import de.privacy_avare.repository.ProfileRepository;

/**
 * Der Service dient zum endgültigen Löschen von Profilen aus der Datenbank. Der
 * Service ist hauptsächlich für die Anwendung in einem Scheduler gedacht.
 * 
 * @author Lukas Struppek
 * @version 1.0
 *
 */

@Service
public class ClearanceService {

	@Autowired
	ProfileRepository profileRepository;

	/**
	 * Löscht alle Profile in der Datenbank mit unSync-Flag auf true sowie Profile,
	 * auf welche länger als 18 Monate nicht zugegriffen wurde.
	 */
	public void cleanDatabase() {
		// Suchen & Löschen aller Profile in der DB mit unSync = true
		Iterable<Profile> delProfiles = profileRepository.findAllByUnSyncTrue();
		profileRepository.delete(delProfiles);

		// Berechnung Zeitpunkt vor 18 Monaten
		Calendar cal = GregorianCalendar.getInstance(Locale.GERMANY);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 540);

		// Suchen und Löschen aller Profile mit lastProfileContact vor 18 Monaten oder
		// früher
		Iterable<Profile> unusedProfiles = profileRepository.findAllByLastProfileContactBefore(cal.getTime());
		profileRepository.delete(unusedProfiles);
	}

}
