package de.privacy_avare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.privacy_avare.domain.Preferences;
import de.privacy_avare.domain.Profile;
import de.privacy_avare.service.ProfileService;

/**
 * @author Lukas Struppek
 * @version 1.0
 */

@RestController("existingProfileControllerV1")
@RequestMapping(value = "/v1/profiles")
public class ExistingProfileController {
	@Autowired
	ProfileService profileService;

	/**
	 * Sucht in der Datenbank nach der übergebenen ProfileId und liefert ein
	 * entsprechendes Profile bzw. null zurück.
	 * 
	 * @param profileId
	 *            ProfileId des gesuchten Profils.
	 * @return Gefundenes Profil.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Profile pullProfile(@PathVariable("id") String id) {
		Profile profile = profileService.getProfileById(id);
		return profile;
	}

	/**
	 * Sucht alle Profile in der Datenbank und liefert diese in Form einer Liste
	 * zurück.
	 * 
	 * @return Liste mit allen enthaltenen Profilen.
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public List<Profile> pullAllProfiles() {
		List<Profile> list = profileService.getAllProfiles();
		return list;
	}

	/**
	 * Löscht ein Profil mit entsprechender ProfileId aus der Datenbank.
	 * 
	 * @param id
	 *            ProfileId des zu löschenden Profils.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteProfile(@PathVariable("id") String id) {
		profileService.removeProfile(id);
	}

	/**
	 * Löscht das gesendete Profil aus der Datenbank.
	 * 
	 * @param profile Das zu löschende Profil.
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	public void deleteProfile(@RequestBody Profile profile) {
		profileService.removeProfile(profile);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public void pushProfile(Profile pushProfile) {
		profileService.pushProfile(pushProfile);
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public void test() {
		List<Profile> list = profileService.getAllProfiles();
		for (Profile p : list)
			System.out.println(p.getId());
	}
}
