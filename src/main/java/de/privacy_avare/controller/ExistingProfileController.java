package de.privacy_avare.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * Die Antworten auf REST-Anfragen werden stets in Form von
 * ResponseEntity-Objekten zurückgeliefert, welche neben dem eigentlichen Inhalt
 * verschiedene, zusätzliche Informationen bereitstellen.
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
	 * @see org.springframework.http.ResponseEntity
	 */
	@Autowired
	private ProfileService profileService;

	/**
	 * Löscht ein Profil mit entsprechender ProfileId aus der Datenbank. Dabei wird
	 * das unsync-Flag auf true, der Zeitpunkt lastProfileChange 100 Jahre in die
	 * Zukunft und die preferences auf null gesetzt. In jedem Fall wird der
	 * Zeitpunkt lastContactTimestamp angepasst.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * Ist das entsprechende Profil in der DB auf den Zustand unsync gesetzt, so
	 * wird eine ProfileSetOnDeletionException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des zu löschenden Profils.
	 * @return Leere ResponseEntity mit Statuscode 204 No Content.
	 * 
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
	 * findet keine Überschreibung statt. In jedem Fall wird der Zeitpunkt
	 * lastContactTimestamp angepasst.
	 * 
	 * * Das Format für die Übertragung des clientLastProfileChangeTimestamp lässt
	 * sich mithilfe eines SimpleDateFormat-Objekts und der Konfiguration
	 * "yyyy-MM-dd'T'HH:mm:ss,SSS" erreichen.
	 * 
	 * Ist das Profil in der DB aktueller hinsichtlich des Zeitpunktes
	 * lastProfileChange als das zu pushende Profil, so wird eine
	 * ClientProfileOutdatedException zurückgegeben.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * Ist das entsprechende Profil in der DB auf den Zustand unsync gesetzt, so
	 * wird eine ProfileSetOnDeletionException zurückgegeben.
	 * 
	 * 
	 * @param id
	 *            ProfileId des zu pushenden Profils.
	 * @param clientLastProfileChangeTimestamp
	 *            Letzter Änderungszeitpunkt der Nutzerpräferenzen.
	 * @param preferences
	 *            Zu pushende Nutzerpräferenzen.
	 * @return Leere ResponseEntity mit Statuscode 204 No Content.
	 * 
	 */
	@RequestMapping(value = "/{id}/{clientProfileChangeTimestamp}", method = RequestMethod.PUT)
	public ResponseEntity<Void> pushProfile(@PathVariable("id") String id,
			@PathVariable("clientProfileChangeTimestamp") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss,SSS") Date clientLastProfileChangeTimestamp,
			@RequestBody Object preferences) throws RuntimeException {
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
	 * true). In jedem Fall wird der Zeitpunkt lastContactTimestamp angepasst.
	 * 
	 * Das Format für die Übertragung des clientLastProfileChangeTimestamp lässt
	 * sich mithilfe eines SimpleDateFormat-Objekts und der Konfiguration
	 * "yyyy-MM-dd'T'HH:mm:ss,SSS" erreichen.
	 * 
	 * Ist das Profil in der DB aktueller hinsichtlich des Zeitpunktes
	 * lastProfileChange als das zu pushende Profil, so wird eine
	 * ClientProfileOutdatedException zurückgegeben, falls overwrite = false als
	 * Parameter übergeben wurde.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * Ist das entsprechende Profil in der DB auf den Zustand unsync gesetzt, so
	 * wird eine ProfileSetOnDeletionException zurückgegeben.
	 * 
	 * 
	 * 
	 * @param id
	 *            ProfileId des zu pushenden Profils.
	 * @param clientLastProfileChangeTimestamp
	 *            Letzter Änderungszeitpunkt der Nutzerpräferenzen.
	 * @param preferences
	 *            Zu pushende Nutzerpräferenzen.
	 * @param overwrite
	 *            Legt fest, ob ein neueres Profil in DB überschrieben werden soll.
	 * @return Leere ResponseEntity mit Statuscode 204 No Content.
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
	 * Sucht in der Datenbank nach der übergebenen ProfileId. Bei einem
	 * entsprechenden gefundenen Profil wird der lastProfileChangeTimestamp des
	 * Profils aus der Datenbank mit dem clientLastProfileChangeTimestamp
	 * verglichen. Ist das Profil aus der Datenbank nicht neuer als 5 Minuten, so
	 * wird eine Fehlermeldung zurückgeliefert. In jedem Fall wird der Zeitpunkt
	 * lastContactTimestamp angepasst.
	 * 
	 * Das Format für die Übertragung des clientLastProfileChangeTimestamp lässt
	 * sich mithilfe eines SimpleDateFormat-Objekts und der Konfiguration
	 * "yyyy-MM-dd'T'HH:mm:ss,SSS" erreichen.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * Ist das entsprechende Profil in der DB auf den Zustand unsync gesetzt, so
	 * wird eine ProfileSetOnDeletionException zurückgegeben.
	 * 
	 * Ist das gefundene DB Profil hinsichtlich des Zeitpunktes lastProfileChange
	 * älter als der Parameter der Anfrage, so wird eine
	 * ServerProfileOutdatedException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des gesuchten Profils.
	 * @param clientLastProfileChangeTimestamp
	 *            Zeitpunkt der letzten Profilaktualisierung auf Clientseite.
	 * @return HashMap mit allen Eigenschaften des Profils.
	 * @see java.text.SimpleDateFormat
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

	/**
	 * Sucht in der Datenbank nach der übergebenen ProfileId und liefert ein
	 * gefundenes Profil unabhängig von dessen lastProfileChangeTimestamp zurück. In
	 * jedem Fall wird der Zeitpunkt lastContactTimestamp angepasst.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * Ist das entsprechende Profil in der DB auf den Zustand unsync gesetzt, so
	 * wird eine ProfileSetOnDeletionException zurückgegeben.
	 * 
	 * 
	 * @param id
	 *            ProfileId des gesuchten Profils.
	 * @return HashMap mit allen Eigenschaften des Profils.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> pullProfileIgnoringLastProfileChange(@PathVariable("id") String id) {
		Profile serverProfile = profileService.getProfileById(id);
		HashMap<String, Object> map = serverProfile.toHashMap();
		ResponseEntity<HashMap<String, Object>> response = new ResponseEntity<HashMap<String, Object>>(map,
				HttpStatus.OK);
		return response;
	}

	/**
	 * Sucht in der Datenbank nach der übergebenen ProfileId und liefert die
	 * Eigenschaft lastProfileChangeTimestamp des gefundenes Profil zurück. In jedem
	 * Fall wird der Zeitpunkt lastContactTimestamp angepasst.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des gesuchten Profils.
	 * 
	 * @return Eigenschaft lastProfileChangeTimestamp des DB-Profils.
	 */
	@RequestMapping(value = "/{id}/lastProfileChange", method = RequestMethod.GET)
	public ResponseEntity<Date> getLastProfileChange(@PathVariable("id") String id) {
		Date serverLastProfileChange = profileService.getLastProfileChange(id);
		ResponseEntity<Date> response = new ResponseEntity<Date>(serverLastProfileChange, HttpStatus.OK);
		return response;
	}

	/**
	 * Sucht in der Datenbank nach der übergebenen ProfileId und liefert die
	 * Eigenschaft lastProfileContactTimestamp des gefundenes Profil zurück. In
	 * jedem Fall wird der Zeitpunkt lastContactTimestamp angepasst.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des gesuchten Profils.
	 * 
	 * @return Eigenschaft lastProfileContactTimestamp des DB-Profils.
	 */
	@RequestMapping(value = "/{id}/lastProfileContact", method = RequestMethod.GET)
	public ResponseEntity<Date> getLastProfileContact(@PathVariable("id") String id) {
		Date serverLastProfileContact = profileService.getLastProfileContact(id);
		ResponseEntity<Date> response = new ResponseEntity<Date>(serverLastProfileContact, HttpStatus.OK);
		return response;
	}

	/**
	 * Sucht in der Datenbank nach der übergebenen ProfileId und liefert die
	 * Eigenschaft unSync des gefundenes Profil zurück. In jedem Fall wird der
	 * Zeitpunkt lastContactTimestamp angepasst.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des gesuchten Profils.
	 * 
	 * @return Eigenschaft unSync des DB-Profils.
	 */
	@RequestMapping(value = "/{id}/unSync", method = RequestMethod.GET)
	public ResponseEntity<Boolean> isProfileUnsync(@PathVariable("id") String id) {
		boolean unSync = profileService.isUnSync(id);
		ResponseEntity<Boolean> response = new ResponseEntity<Boolean>(unSync, HttpStatus.OK);
		return response;
	}

	/**
	 * Sucht in der Datenbank nach der übergebenen ProfileId und liefert die
	 * preferences des gefundenes Profil zurück. In jedem Fall wird der Zeitpunkt
	 * lastContactTimestamp angepasst.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des gesuchten Profils.
	 * 
	 * @return Eigenschaft preferences des DB-Profils.
	 */
	@RequestMapping(value = "/{id}/preferences", method = RequestMethod.GET)
	public ResponseEntity<Object> getPreferences(@PathVariable("id") String id) {
		Object serverPreferences = profileService.getPreferences(id);
		ResponseEntity<Object> response = new ResponseEntity<Object>(serverPreferences, HttpStatus.OK);
		return response;
	}

	/**
	 * Sucht alle Profile in der Datenbank und liefert diese in Form einer Liste
	 * zurück, unabhängig vom Status der einzelnen Profile. Werden in der DB keine
	 * Profile gefunden, so wird eine ProfileNotFoundException zurückgeliefert.
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

	/**
	 * Methode für spezielle Testfälle. Wird fortlaufend angepasst und in der
	 * finalen Version entfernt.
	 * 
	 * @param date
	 *            TBD
	 */
	@Deprecated
	@RequestMapping(value = "/test/{date}", method = RequestMethod.PUT)
	public void test(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss,SSS") Date date) {
		System.out.println(date);
	}
}
