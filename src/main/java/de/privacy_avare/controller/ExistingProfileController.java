package de.privacy_avare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.privacy_avare.domain.Profile;
import de.privacy_avare.service.ProfileService;

/**
 * Die Klasse stellt eine REST-API zur Verfügung, über welche externe Anfragen
 * bezüglich bereits existierender Profile gestellt werden können. Zur
 * Verarbeitung der Anfragen werden diese an entsprechende Services
 * weitergeleitet.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

@RestController("existingProfileControllerV1")
@RequestMapping(value = "/v1/profiles")
public class ExistingProfileController {

	/**
	 * Service stellt diverse Methoden zur Verarbeitung von Profilen sowie der
	 * Ablage in der Datenbank bzw. dem Abruf von Profilen aus der Datenbank bereit.
	 * Instanz wird über Dependency Injection bereitgestellt.
	 * 
	 * @see de.privacy_avare.service.ProfileService
	 */
	@Autowired
	private ProfileService profileService;

	/**
	 * Sucht in der Datenbank nach der übergebenen ProfileId und liefert ein
	 * entsprechendes Profile bzw. null zurück.
	 * 
	 * @param id
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
	public List<Profile> getAllProfiles() {
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
		profileService.setProfileOnDeletion(id);
	}

	/**
	 * Löscht das gesendete Profil aus der Datenbank.
	 * 
	 * @param profile
	 *            Das zu löschende Profil.
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	public void deleteProfile(@RequestBody Profile profile) {
		profileService.setProfileOnDeletion(profile);
	}

	/**
	 * Speichert eine aktualisierte Version eines Profils in der Datenbank.
	 * 
	 * @param pushProfile
	 *            Zu pushendes Profil.
	 * @throws Exception Platzhalter
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public void pushProfile(Profile pushProfile) throws Exception {
		profileService.pushProfile(pushProfile);
	}

	// Methode für spezielle Testläufe. Wird fortlaufend geändert!
	@RequestMapping(value = "/test", method = RequestMethod.PUT)
	public void test(@RequestBody Profile profile) throws Exception {
		profileService.createNewProfile(profile.getId());
	}
}
