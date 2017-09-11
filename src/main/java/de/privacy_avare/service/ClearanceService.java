package de.privacy_avare.service;

import java.util.List;

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
		profileRepository.delete(delProfiles);
	}

}
