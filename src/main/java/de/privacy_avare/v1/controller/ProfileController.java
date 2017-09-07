/**
 * @author Lukas Struppek
 * @version 1.0
 * 
 */

package de.privacy_avare.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.privacy_avare.domain.Profile;
import de.privacy_avare.domain.ProfilePreferences;

@RestController("profileControllerV1")
@RequestMapping("/v1/")
public class ProfileController {

	@RequestMapping(value = "/profiles/{profileId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProfile(String ProfileId, ProfilePreferences profilePreferences) {
		// Prüfen, ob ID vorhanden ist
		// entsprechend Überschreiben der ProfileData
		// Anpassen des letzten Änderungszeitpunktes in die Zukunft
		// Generierung einer Rückmeldung an den Client

		return null;
	}
	
	@RequestMapping(value = "/profiles/{profileId}", method = RequestMethod.POST)
	public ResponseEntity<?> createProfile(String profileId, Profile profil){
		
		return null;
	}

	@RequestMapping(value = "/profiles/{profileId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateProfile(String profileId, ProfilePreferences profilePreferences) {
		return null;
	}
	
	@RequestMapping(value = "/profiles/{profileId}", method = RequestMethod.GET)
	public ResponseEntity<?> getProfile(String profileId){
		return null;
	}
	
	@RequestMapping(value = "/newProfile", method = RequestMethod.GET)
	public ResponseEntity<?> getNewProfileId(){
		return null;
	}

}
