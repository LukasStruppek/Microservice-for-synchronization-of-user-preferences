package de.privacy_avare.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.privacy_avare.domain.Preferences;

/**
 * @author Lukas Struppek
 * @version 1.0
 */

@RestController("existingProfileControllerV1")
@RequestMapping(value = "/v1/profiles")
public class ExistingProfileController {

	@RequestMapping(value = "/{profileId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProfile(@PathVariable("profileId") String profileId, Preferences preferences) {
		// Prüfen, ob ID vorhanden ist
		// entsprechend Überschreiben der ProfileData
		// Anpassen des letzten Änderungszeitpunktes in die Zukunft
		// Generierung einer Rückmeldung an den Client
		return null;
	}

	@RequestMapping(value = "/{profileId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateProfile(String profileId, Preferences preferences) {
		return null;
	}

	@RequestMapping(value = "/{profileId}", method = RequestMethod.GET)
	public ResponseEntity<?> getProfile(String profileId) {
		return null;
	}

	@RequestMapping(value = "/newProfile", method = RequestMethod.GET)
	public ResponseEntity<?> getNewProfileId() {
		return null;
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test() {
		return null;
	}

}
