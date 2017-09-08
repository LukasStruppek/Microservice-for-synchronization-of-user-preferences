/**
 * @author Lukas Struppek
 * @version 1.0
 * 
 */

package de.privacy_avare.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.privacy_avare.domain.Preferences;
import de.privacy_avare.domain.Profile;
import de.privacy_avare.id.IdGenerator;

@RestController("profileControllerV1")
public class ProfileController {

	@RequestMapping(value = "/profiles/{profileId}", method = RequestMethod.POST)
	public ResponseEntity<?> createProfile(String profileId, Profile profil) {

		return null;
	}

	@RequestMapping(value = "/profiles/newProfile", method = RequestMethod.GET)
	public ResponseEntity<?> createProfile() {
		return null;
	}

	@RequestMapping(value = "/profiles/{profileId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProfile(String ProfileId, Preferences preferences) {
		// Prüfen, ob ID vorhanden ist
		// entsprechend Überschreiben der ProfileData
		// Anpassen des letzten Änderungszeitpunktes in die Zukunft
		// Generierung einer Rückmeldung an den Client

		return null;
	}

	@RequestMapping(value = "/profiles/{profileId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateProfile(String profileId, Preferences preferences) {
		return null;
	}

	@RequestMapping(value = "/profiles/{profileId}", method = RequestMethod.GET)
	public ResponseEntity<?> getProfile(String profileId) {
		return null;
	}

	@RequestMapping(value = "/newProfile", method = RequestMethod.GET)
	public ResponseEntity<?> getNewProfileId() {
		return null;
	}

	@RequestMapping(value = "/createId", method = RequestMethod.GET)
	public String generateId() {
		String userId = IdGenerator.generateID();
		return userId;
	}

}
