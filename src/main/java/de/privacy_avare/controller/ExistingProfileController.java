package de.privacy_avare.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.privacy_avare.domain.Preferences;
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
	 * Sucht in der Datenbank nach der übergebenen ProfileId. Bei einem entsprechend
	 * gefundenen Profil wird der lastProfileChangeTimestamp des Profils aus der
	 * Datenbank mit dem clientLastProfileChangeTimestamp verglichen. Ist das Profil
	 * aus der Datenbank nicht neuer als 5 Minuten, so wird eine Fehlermeldung
	 * zurückgeliefert.
	 * 
	 * Das Format für die Übertragung des clientLastProfileChangeTimestamp lässt
	 * sich mithilfe eines SimpleDateFormat-Objekts und der Konfiguration
	 * "yyyy-MM-dd HH:mm:ss,SSS" erreichen.
	 * 
	 * Es empfiehlt sich, für die Clientanfrage auf eine HashMap<String, String>
	 * zurückzugreifen, um die Parameter zu übertragen.
	 * 
	 * @param id
	 *            ProfileId des gesuchten Profils.
	 * @param lastProfileChangeTimestamp
	 *            Zeitpunkt der letzten Profilaktualisierung auf Clientseite.
	 * @return ggfs. gefundenes, aktuelleres Profil.
	 */
	@RequestMapping(value = "/{id}/{lastProfileChangeTimestamp}", method = RequestMethod.GET)
	public Profile pullProfile(@PathVariable("id") String id,
			@PathVariable("lastProfileChangeTimestamp") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss,SSS") Date clientLastProfileChangeTimestamp) {
		Profile profile = profileService.getProfileByIdComparingLastChange(id, clientLastProfileChangeTimestamp);
		return profile;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Profile pullProfileIgnoringLastProfileChange(@PathVariable("id") String id) {
		Profile profile = profileService.getProfileById(id);
		return profile;
	}

	@RequestMapping(value = "/{id}/lastProfileChange")
	public Date getLastProfileChange(@PathVariable("id") String id) {
		Date lastProfileChange = profileService.getLastProfileChange(id);
		return lastProfileChange;
	}

	@RequestMapping(value = "/{id}/lastProfileContact")
	public Date getLastProfileContact(@PathVariable("id") String id) {
		Date lastProfileContact = profileService.getLastProfileContact(id);
		return lastProfileContact;
	}

	@RequestMapping(value = "/{id}/unSync")
	public boolean isProfileUnsync(@PathVariable("id") String id) {
		boolean unSync = profileService.isUnSync(id);
		return unSync;
	}

	@RequestMapping(value = "/{id}/preferences")
	public Preferences getPreferences(@PathVariable("id") String id) {
		Preferences preferences = profileService.getPreferences(id);
		return preferences;
	}

	/**
	 * Sucht alle Profile in der Datenbank und liefert diese in Form einer Liste
	 * zurück.
	 * 
	 * @return Liste mit allen enthaltenen Profilen.
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public Iterable<Profile> getAllProfiles() {
		Iterable<Profile> list = profileService.getAllProfiles();
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
	 * Speichert eine aktualisierte Version eines Profils in der Datenbank.
	 * 
	 * @param pushProfile
	 *            Zu pushendes Profil.
	 * @throws Exception
	 *             Platzhalter
	 */
	@RequestMapping(value = "/{overwrite}", method = RequestMethod.PUT)
	public void pushProfile(@RequestBody Profile pushProfile, @PathVariable("overwrite") boolean overwrite)
			throws Exception {
		profileService.pushProfile(pushProfile, overwrite);
	}

	// Methode für spezielle Testläufe. Wird fortlaufend geändert!
	@RequestMapping(value = "/test", method = RequestMethod.PUT)
	public void test(@RequestBody Profile profile) throws Exception {
		profileService.save(profile);
	}
}
