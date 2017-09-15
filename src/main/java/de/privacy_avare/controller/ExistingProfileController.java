package de.privacy_avare.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	 * Löscht ein Profil mit entsprechender ProfileId aus der Datenbank.
	 * 
	 * @param id
	 *            ProfileId des zu löschenden Profils.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteProfile(@PathVariable("id") String id) {
		profileService.setProfileOnDeletion(id);
		ResponseEntity<Void> response = new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		return response;
	}

	/**
	 * Pushen eines aktualisierten Profils. Ist der Zeitunkt lastProfileChange des
	 * zu pushenden Profils mindestens 5 Minuten aktueller als der des in der
	 * Datenbank bestehenden Profils, so wird dieses Überschrieben. Andernfalls
	 * findet keine Überschreibung statt.
	 * 
	 * * Das Format für die Übertragung des clientLastProfileChangeTimestamp lässt
	 * sich mithilfe eines SimpleDateFormat-Objekts und der Konfiguration
	 * "yyyy-MM-dd'T'HH:mm:ss,SSS" erreichen.
	 * 
	 * @param id
	 *            ProfileId
	 * @param clientLastProfileChangeTimestamp
	 *            Letzter Änderungszeitpunkt der Nutzerpräferenzen
	 * @param preferences
	 *            zu pushende Nutzerpräferenzen
	 * @throws RuntimeException
	 */
	@RequestMapping(value = "/{id}/{clientProfileChangeTimestamp}", method = RequestMethod.PUT)
	public ResponseEntity<Void> pushProfile(@PathVariable("id") String id,
			@PathVariable("clientProfileChangeTimestamp") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss,SSS") Date clientLastProfileChangeTimestamp,
			@RequestBody Object preferences) throws RuntimeException {
		System.out.println("Methode aufgerufen: " + clientLastProfileChangeTimestamp);
		profileService.pushProfile(id, clientLastProfileChangeTimestamp, preferences, false);
		ResponseEntity<Void> response = new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		return response;
	}

	/**
	 * Pushen eines aktualisierten Profils. Ist der Zeitunkt lastProfileChange des
	 * zu pushenden Profils mindestens 5 Minuten aktueller als der des in der
	 * Datenbank bestehenden Profils, so wird dieses Überschrieben. Andernfalls wird
	 * entsprechend dem Parameter overwrite das ursprüngliche Profil in der
	 * Datenbank beibehalten (overwrite = false) oder überschrieben (overwrite =
	 * true).
	 * 
	 * Das Format für die Übertragung des clientLastProfileChangeTimestamp lässt
	 * sich mithilfe eines SimpleDateFormat-Objekts und der Konfiguration
	 * "yyyy-MM-dd'T'HH:mm:ss,SSS" erreichen.
	 * 
	 * @param id
	 *            ProfileId
	 * @param clientLastProfileChangeTimestamp
	 *            Letzter Änderungszeitpunkt der Nutzerpräferenzen
	 * @param preferences
	 *            zu pushende Nutzerpräferenzen
	 * @throws RuntimeException
	 */
	@RequestMapping(value = "/{id}/{clientProfileChangeTimestamp}/{overwrite}", method = RequestMethod.PUT)
	public ResponseEntity<Void> pushProfile(@PathVariable("id") String id,
			@PathVariable("clientProfileChangeTimestamp") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss,SSS") Date clientLastProfileChangeTimestamp,
			@RequestBody Object preferences, @PathVariable("overwrite") boolean overwrite) throws RuntimeException {
		profileService.pushProfile(id, clientLastProfileChangeTimestamp, preferences, overwrite);
		ResponseEntity<Void> response = new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		return response;
	}

	/**
	 * Sucht in der Datenbank nach der übergebenen ProfileId. Bei einem entsprechend
	 * gefundenen Profil wird der lastProfileChangeTimestamp des Profils aus der
	 * Datenbank mit dem clientLastProfileChangeTimestamp verglichen. Ist das Profil
	 * aus der Datenbank nicht neuer als 5 Minuten, so wird eine Fehlermeldung
	 * zurückgeliefert.
	 * 
	 * Das Format für die Übertragung des clientLastProfileChangeTimestamp lässt
	 * sich mithilfe eines SimpleDateFormat-Objekts und der Konfiguration
	 * "yyyy-MM-dd'T'HH:mm:ss,SSS" erreichen.
	 * 
	 * 
	 * @param id
	 *            ProfileId des gesuchten Profils.
	 * @param lastProfileChangeTimestamp
	 *            Zeitpunkt der letzten Profilaktualisierung auf Clientseite.
	 * @return ggfs. gefundenes, aktuelleres Profil.
	 */
	@RequestMapping(value = "/{id}/{lastProfileChangeTimestamp}", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> pullProfile(@PathVariable("id") String id,
			@PathVariable("lastProfileChangeTimestamp") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss,SSS") Date clientLastProfileChangeTimestamp) {
		Profile serverProfile = profileService.getProfileByIdComparingLastChange(id, clientLastProfileChangeTimestamp);
		HashMap<String, Object> map = serverProfile.toHashMap();
		ResponseEntity<HashMap<String, Object>> response = new ResponseEntity<HashMap<String, Object>>(map,
				HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> pullProfileIgnoringLastProfileChange(@PathVariable("id") String id) {
		Profile serverProfile = profileService.getProfileById(id);
		HashMap<String, Object> map = serverProfile.toHashMap();
		ResponseEntity<HashMap<String, Object>> response = new ResponseEntity<HashMap<String, Object>>(map,
				HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/{id}/lastProfileChange")
	public ResponseEntity<Date> getLastProfileChange(@PathVariable("id") String id) {
		Date serverLastProfileChange = profileService.getLastProfileChange(id);
		ResponseEntity<Date> response = new ResponseEntity<Date>(serverLastProfileChange, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/{id}/lastProfileContact")
	public ResponseEntity<Date> getLastProfileContact(@PathVariable("id") String id) {
		Date serverLastProfileContact = profileService.getLastProfileContact(id);
		ResponseEntity<Date> response = new ResponseEntity<Date>(serverLastProfileContact, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/{id}/unSync")
	public ResponseEntity<Boolean> isProfileUnsync(@PathVariable("id") String id) {
		boolean unSync = profileService.isUnSync(id);
		ResponseEntity<Boolean> response = new ResponseEntity<Boolean>(unSync, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/{id}/preferences")
	public ResponseEntity<Object> getPreferences(@PathVariable("id") String id) {
		Object serverPreferences = profileService.getPreferences(id);
		ResponseEntity<Object> response = new ResponseEntity<Object>(serverPreferences, HttpStatus.OK);
		return response;
	}

	/**
	 * Sucht alle Profile in der Datenbank und liefert diese in Form einer Liste
	 * zurück.
	 * 
	 * @return Liste mit allen enthaltenen Profilen.
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<List<HashMap<String, Object>>> getAllProfiles() {
		Iterable<Profile> serverList = profileService.getAllProfiles();
		List<HashMap<String, Object>> responseList = new LinkedList<HashMap<String, Object>>();
		for (Profile p : serverList) {
			responseList.add(p.toHashMap());
		}
		ResponseEntity<List<HashMap<String, Object>>> response = new ResponseEntity<List<HashMap<String, Object>>>(
				responseList, HttpStatus.OK);
		return response;
	}

	// Methode für spezielle Testläufe. Wird fortlaufend geändert!
	@RequestMapping(value = "/test/{date}", method = RequestMethod.PUT)
	public void test(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss,SSS") Date date)
			throws Exception {
		System.out.println(date);
	}
}
