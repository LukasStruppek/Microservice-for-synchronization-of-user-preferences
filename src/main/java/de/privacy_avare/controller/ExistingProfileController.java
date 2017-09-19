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
import de.privacy_avare.exeption.ClientPreferencesOutdatedException;
import de.privacy_avare.exeption.NoProfilesInDatabaseException;
import de.privacy_avare.exeption.ProfileNotFoundException;
import de.privacy_avare.exeption.ServerPreferencesOutdatedException;
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
 * Eine REST-Dokumentation wird über Swagger bereitgestellt.
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see de.privacy_avare.service.ProfileService
 * @see org.springframework.http.ResponseEntity
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
	 * Default-Konstruktor
	 */
	public ExistingProfileController() {

	}

	/**
	 * Dient dazu, ein Profil auf den Status unSync zu setzen. Hierzu wird vom
	 * Client ein für diesen identifizierbares unSync-Preferences gesendet und in
	 * der Datenbank gespeichert. Eine Überprüfung des Zeitstempels
	 * lastProfileChange findet nicht statt. Eine Überprüfung, ob das Profil bereits
	 * auf den Zustand unSync gesetzt wurde, findet nicht statt.
	 * 
	 * Das unSyncProfile wird im Body der Http-Nachricht erwartet.
	 * 
	 * In jedem Fall wird der Zeitpunkt lastContact angepasst.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des zu löschenden Profils.
	 * @param unSyncPreferences
	 *            Vom Client empfangenes unSyncProfile.
	 * @return Leere ResponseEntity mit Statuscode 204 No Content.
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteProfile(@PathVariable("id") String id, @RequestBody String unSyncPreferences) {
		profileService.setProfileOnDeletion(id, unSyncPreferences);
		ResponseEntity<Void> response = new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		return response;
	}

	/**
	 * Pushen eines aktuellen Profils. Ist der Zeitunkt lastProfileChange des zu
	 * pushenden Profils mindestens 5 Minuten aktueller als der des in der Datenbank
	 * bestehenden Profils, so wird dieses Überschrieben. Andernfalls findet keine
	 * Überschreibung statt.
	 * 
	 * In jedem Fall wird der Zeitpunkt lastProfileContact angepasst.
	 * 
	 * Das Format für die Übertragung des Zeitstempels clientLastProfileChange lässt
	 * sich in Java-Anwendungen mithilfe eines SimpleDateFormat-Objekts und der
	 * Konfiguration "yyyy-MM-dd'T'HH:mm:ss,SSS" erreichen.
	 * 
	 * Ist das Profil in der DB aktueller hinsichtlich des Zeitpunktes
	 * lastProfileChange als das zu pushende Profil, so wird eine
	 * ClientProfileOutdatedException zurückgegeben.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des zu pushenden Profils.
	 * @param clientLastProfileChange
	 *            Letzter Änderungszeitpunkt der Nutzerpräferenzen.
	 * @param preferences
	 *            Zu pushende Nutzerpräferenzen.
	 * @return Leere ResponseEntity mit Statuscode 204 No Content.
	 * @see java.text.SimpleDateFormat
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 * @throws ClientPreferencesOutdatedException
	 *             Gesendeter Zeitstempel ist jünger als in DB gespeicherter
	 *             Zeitstempel.
	 */
	@RequestMapping(value = "/{id}/{clientProfileChange}", method = RequestMethod.PUT)
	public ResponseEntity<Void> pushProfilePreferences(@PathVariable("id") String id,
			@PathVariable("clientProfileChange") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss,SSS") Date clientLastProfileChange,
			@RequestBody String preferences) throws ProfileNotFoundException, ClientPreferencesOutdatedException {
		profileService.pushProfile(id, clientLastProfileChange, preferences, false);
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
	 * In jedem Fall wird der Zeitpunkt lastProfileContact angepasst.
	 * 
	 * Das Format für die Übertragung des Zeitstempels clientLastProfileChange lässt
	 * sich in Java-Anwendungen mithilfe eines SimpleDateFormat-Objekts und der
	 * Konfiguration "yyyy-MM-dd'T'HH:mm:ss,SSS" erreichen.
	 * 
	 * Ist das Profil in der DB aktueller hinsichtlich des Zeitpunktes
	 * lastProfileChange als das zu pushende Profil, so wird eine
	 * ClientProfileOutdatedException zurückgegeben, falls overwrite = false als
	 * Parameter übergeben wurde.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des zu pushenden Profils.
	 * @param clientLastProfileChange
	 *            Letzter Änderungszeitpunkt der Nutzerpräferenzen.
	 * @param preferences
	 *            Zu pushende Nutzerpräferenzen.
	 * @param overwrite
	 *            Legt fest, ob ein neueres Profil in DB überschrieben werden soll.
	 * @return Leere ResponseEntity mit Statuscode 204 No Content.
	 * @see java.text.SimpleDateFormat
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 * @throws ClientPreferencesOutdatedException
	 *             Gesendeter Zeitstempel ist jünger als in DB gespeicherter
	 *             Zeitstempel (bei overwrite = false).
	 */
	@RequestMapping(value = "/{id}/{clientProfileChange}/{overwrite}", method = RequestMethod.PUT)
	public ResponseEntity<Void> pushProfilePreferences(@PathVariable("id") String id,
			@PathVariable("clientProfileChange") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss,SSS") Date clientLastProfileChange,
			@RequestBody String preferences, @PathVariable("overwrite") boolean overwrite)
			throws ProfileNotFoundException, ClientPreferencesOutdatedException {
		profileService.pushProfile(id, clientLastProfileChange, preferences, overwrite);
		ResponseEntity<Void> response = new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		return response;
	}

	/**
	 * Sucht in der Datenbank nach der übergebenen ProfileId. Bei einem
	 * entsprechenden gefundenen Profil wird der Zeitpunkt lastProfileChange des
	 * Profils aus der Datenbank mit dem Zeitpunkt clientLastProfileChange
	 * verglichen. Ist das Profil aus der Datenbank nicht neuer als 5 Minuten, so
	 * wird eine Fehlermeldung zurückgeliefert.
	 * 
	 * In jedem Fall wird der Zeitpunkt lastProfileContact angepasst.
	 * 
	 * Das Format für die Übertragung des Zeitstempels clientLastProfileChange lässt
	 * sich in Java-Anwendungen mithilfe eines SimpleDateFormat-Objekts und der
	 * Konfiguration "yyyy-MM-dd'T'HH:mm:ss,SSS" erreichen.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * Ist das gefundene DB Profil hinsichtlich des Zeitpunktes lastProfileChange
	 * älter als der Parameter der Anfrage, so wird eine
	 * ServerProfileOutdatedException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des gesuchten Profils.
	 * @param clientLastProfileChange
	 *            Zeitpunkt der letzten Profilaktualisierung auf Clientseite.
	 * @return Preferences des Profils in der Datenbank.
	 * @see java.text.SimpleDateFormat
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 * @throws ServerPreferencesOutdatedException
	 *             Gesendeter Zeitstempel ist älter als in DB gespeicherter
	 *             Zeitstempel (bei overwrite = false).
	 */
	@RequestMapping(value = "/{id}/{lastProfileChange}", method = RequestMethod.GET)
	public ResponseEntity<String> pullProfilePreferences(@PathVariable("id") String id,
			@PathVariable("lastProfileChange") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss,SSS") Date clientLastProfileChange)
			throws ProfileNotFoundException, ServerPreferencesOutdatedException {
		Profile serverProfile = profileService.getProfileByIdComparingLastChange(id, clientLastProfileChange);
		ResponseEntity<String> response = new ResponseEntity<String>(serverProfile.getPreferences(), HttpStatus.OK);
		return response;
	}

	/**
	 * Sucht in der Datenbank nach der übergebenen ProfileId und liefert ein
	 * gefundenes Profil unabhängig von dessen lastProfileChange zurück. In jedem
	 * Fall wird der Zeitpunkt lastProfileContact angepasst.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * 
	 * @param id
	 *            ProfileId des gesuchten Profils.
	 * @return Preferences des Profils in der Datenbank.
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> pullProfilePreferencesIgnoringLastProfileChange(@PathVariable("id") String id) {
		Profile serverProfile = profileService.getProfileById(id);
		ResponseEntity<String> response = new ResponseEntity<String>(serverProfile.getPreferences(), HttpStatus.OK);
		return response;
	}

	/**
	 * Sucht in der Datenbank nach der übergebenen ProfileId und liefert die
	 * Eigenschaft lastProfileChange des gefundenes Profil zurück. In jedem Fall
	 * wird der Zeitpunkt lastContact angepasst.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des gesuchten Profils.
	 * @return Eigenschaft lastProfileChange des DB-Profils.
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 */
	@RequestMapping(value = "/{id}/lastProfileChange", method = RequestMethod.GET)
	public ResponseEntity<Date> getLastProfileChange(@PathVariable("id") String id) {
		Date serverLastProfileChange = profileService.getLastProfileChange(id);
		ResponseEntity<Date> response = new ResponseEntity<Date>(serverLastProfileChange, HttpStatus.OK);
		return response;
	}

	/**
	 * Sucht in der Datenbank nach der übergebenen ProfileId und liefert die
	 * Eigenschaft lastProfileContact des gefundenes Profil zurück.
	 * 
	 * In jedem Fall wird der Zeitpunkt lastContact anschließend angepasst.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des gesuchten Profils.
	 * @return Eigenschaft lastProfileContact des DB-Profils.
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 */
	@RequestMapping(value = "/{id}/lastProfileContact", method = RequestMethod.GET)
	public ResponseEntity<Date> getLastProfileContact(@PathVariable("id") String id) {
		Date serverLastProfileContact = profileService.getLastProfileContact(id);
		ResponseEntity<Date> response = new ResponseEntity<Date>(serverLastProfileContact, HttpStatus.OK);
		return response;
	}

	/**
	 * Sucht alle Profile in der Datenbank und liefert diese in Form einer Liste
	 * zurück, unabhängig vom Status der einzelnen Profile. Werden in der DB keine
	 * Profile gefunden, so wird eine ProfileNotFoundException zurückgeliefert.
	 * 
	 * Schnittstelle dient hauptsächlich zu Testzwecken in der Entwicklung.
	 * 
	 * @return Liste mit allen in der DB enthaltenen Profilen.
	 * @throws NoProfilesInDatabaseException
	 *             Keine Profile in DB vorhanden.
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<List<HashMap<String, Object>>> getAllProfiles() throws NoProfilesInDatabaseException {
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
