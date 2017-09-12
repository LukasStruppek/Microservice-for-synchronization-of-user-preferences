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
	 * Löscht alle Profile in der Datenbank mit unSync-Flag auf true.
	 */
	public void cleanDatabase() {
		List<Profile> delProfiles = profileRepository.findAllByUnSyncTrue();
		System.out.println("*** Deletion Profiles ***");
		for (Profile p : delProfiles)
			System.out.println(p);
		profileRepository.delete(delProfiles);
		Calendar cal = GregorianCalendar.getInstance(Locale.GERMANY);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 180);
		List<Profile> unusedProfiles = profileRepository.findAllByLastProfileContactBefore(cal.getTime());
		System.out.println("*** Unused Profiles ***");
		for (Profile p : unusedProfiles)
			System.out.println(p);
		profileRepository.delete(unusedProfiles);
		System.out.println("*** Sonstige Profiles ***");
		List<Profile> sonstige = profileRepository.findAllByOrderByIdAsc();
		for (Profile p : sonstige)
			System.out.println(p);
	}

}
