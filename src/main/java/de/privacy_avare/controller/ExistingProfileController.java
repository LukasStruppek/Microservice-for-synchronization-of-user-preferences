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
import de.privacy_avare.dto.ErrorInformation;
import de.privacy_avare.exeption.ClientPreferencesOutdatedException;
import de.privacy_avare.exeption.NoProfilesInDatabaseException;
import de.privacy_avare.exeption.ProfileNotFoundException;
import de.privacy_avare.exeption.ServerPreferencesOutdatedException;
import de.privacy_avare.service.ProfileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

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
@Api(tags = "Existierendes Profile")
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
	 * Default-Konstruktor ohne erweiterte Funktionalität.
	 */
	public ExistingProfileController() {

	}

	/**
	 * Dient dazu, ein Profil auf den Status unSync zu setzen. Hierzu wird vom
	 * Client ein für diesen identifizierbares unSync-Preferences gesendet und in
	 * der Datenbank gespeichert. Eine Überprüfung des Zeitstempels
	 * lastProfileChange findet nicht statt. Eine Überprüfung, ob das Profil bereits
	 * auf den Zustand unSync gesetzt wurde, findet ebenfalls nicht statt. Für den
	 * Server ist nicht ersichtlich, ob es sich um unSync-Preferences handelt oder
	 * nicht. Die Methode sollte jedoch nicht zum ungeprüften Überschreiben der
	 * Preferences verwendet werden, hierfür eignet sich die Methode
	 * pushProfile(String, Date, String, boolean).
	 * 
	 * Das unSyncProfile wird im Body der Http-Nachricht als einfacher Text
	 * erwartet.
	 * 
	 * In jedem Fall wird der Zeitpunkt lastContact angepasst.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des zu löschenden Profils.
	 * @param unSyncPreferences
	 *            Vom Client empfangene unSync-Preferences.
	 * @return Leere ResponseEntity mit Statuscode 200 OK oder Fehlermeldung.
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "Ersetzt Preferences durch unSync-Preferences", notes = "Die in der DB gespeicherten Preferences werden durch die im Parameter übergebenen unSync-Preferences ersetzt, "
			+ "eine <b>Prüfung des lastProfileChange wird nicht durchgeführt</b>. Weiterhin wird der Wert von lastProfileChange um 100 Jahre in die Zukunft gesetzt. "
			+ "Außer anhand des Zeitstempel lastProfileChange ist der Zustand der Preferences für den Server nicht ersichtlich. Daher wird das Profil weiterhin behandelt wie ein normales Profil. "
			+ "Die Auswertung der unSync-Preferences erfolgt auf Seite des Clients. \n \n Die Methode sollte nicht zum ungeprüften Überschreiben der "
			+ "Preferences verwendet werden, hierfür dient die entsprechende REST-Schnittstelle  <b>PUT /v1/profiles/{id}/{clientProfileChange}/{overwrite}.</b> "
			+ "\n \n Zeitstempel lastProfileContact wird aktualisiert.", response = Void.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Preferences erfolgreich durch unSync-Preferences ersetzt", response = Void.class),
			@ApiResponse(code = 404, message = "Kein Profil mit entsprechender Id gefunden \n \n Geworfene Exception: \n de.privacy_avare.exeption.ProfileNotFoundException", response = ErrorInformation.class) })
	public ResponseEntity<Void> deleteProfile(
			@ApiParam(value = "ProfileId des zu löschenden Profils", required = true) @PathVariable("id") String id,
			@ApiParam(value = "Zu speichernde unSync-Preferences", required = true) @RequestBody String unSyncPreferences) {
		profileService.setProfileOnDeletion(id, unSyncPreferences);
		ResponseEntity<Void> response = new ResponseEntity<Void>(HttpStatus.OK);
		return response;
	}

	/**
	 * Speichert preferences von einem Client in der Datenbank entsprechend der
	 * Aktualität des Profils. Ist der Zeitpunkt lastProfileChange des zu pushenden
	 * Profils mindestens 5 Minuten aktueller als der des in der Datenbank
	 * bestehenden Profils, so wird dieses Überschrieben. Andernfalls findet keine
	 * Überschreibung statt. Soll trotzdem das Profil in der DB überschrieben
	 * werden, so ist die Methode mit entsprechend gesetztem overwrite-Parameter zu
	 * nutzen.
	 * 
	 * In jedem Fall wird der Zeitpunkt lastProfileContact angepasst.
	 * 
	 * Das Format für die Übertragung des Zeitstempels clientLastProfileChange lässt
	 * sich in Java-Anwendungen mithilfe eines SimpleDateFormat-Objekts und der
	 * Konfiguration "yyyy-MM-dd'T'HH:mm:ss,SSS" erreichen.
	 * 
	 * Ist das Profil in der DB aktueller hinsichtlich des Zeitpunktes
	 * lastProfileChange als die zu pushendem Preferences, so wird eine
	 * ClientProfileOutdatedException zurückgegeben.
	 * 
	 * Wird kein Profil mit der übergebenen ProfileId gefunden, so wird eine
	 * ProfileNotFoundException zurückgegeben.
	 * 
	 * @param id
	 *            ProfileId des zu pushenden Profils.
	 * @param clientLastProfileChange
	 *            Letzter Änderungszeitpunkt der Client-Preferences.
	 * @param preferences
	 *            Zu pushende Preferences.
	 * @return Leere ResponseEntity mit Statuscode 200 OK oder Fehlermeldung.
	 * @see java.text.SimpleDateFormat
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 * @throws ClientPreferencesOutdatedException
	 *             Gesendeter Zeitstempel ist älter als in DB gespeicherter
	 *             Zeitstempel.
	 */
	@RequestMapping(value = "/{id}/{clientProfileChange}", method = RequestMethod.PUT)
	@ApiOperation(value = "Speichert Preferences in DB", notes = "Zunächst wird in der DB nach dem entsprechenden Profil gesucht und der lastProfileChange mit dem Parameter des Aufrufs verglichen. "
			+ "Liegt der im Parameter spezifizierten Zeitpunkt <b>mindestens 5 Minuten nach</b> dem lastProfileChange der DB, d.h. die Preferences des Clients sind aktueller, so werden die Preferences in der DB überschrieben. "
			+ "Andernfalls wird eine Fehlermeldung zurückgeliefert. Soll trotzdem das Profil in der DB überschrieben werden, so ist die Methode mit entsprechend gesetztem overwrite-Parameter zu nutzen. "
			+ "\n \n Zeitstempel lastProfileContact wird aktualisiert. \n \n Der Methodenaufruf entspricht dem Aufruf von PUT /v1/profiles/{id}/{clientProfileChange}/<b>false</b>. "
			+ "\n \n Parameter clientProfileChange muss im Format <b>yyyy-MM-dd'T'HH:mm:ss,SSS</b> übergeben werden. "
			+ "Dies kann in Java leicht mithilfe von <a href=https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html>SimpleDateFormat</a> realisiert werden.", response = Void.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Preferences erfolgreich ersetzt"),
			@ApiResponse(code = 400, message = "Ungültiger Parameter/ Falscher Datentyp \n \n Geworfene Exception: \n org.springframework.web.method.annotation. \n MethodArgumentTypeMismatchException", response = ErrorInformation.class),
			@ApiResponse(code = 404, message = "Kein Profil mit entsprechender Id gefunden  \n \n Geworfene Exception: \n de.privacy_avare.exeption.ProfileNotFoundException", response = ErrorInformation.class),
			@ApiResponse(code = 409, message = "ClientProfile veraltet \n \n Geworfene Exception: \n de.privacy_avare.exeption.ClientPreferencesOutdatedException", response = ErrorInformation.class) })
	public ResponseEntity<Void> pushProfilePreferences(
			@ApiParam(value = "ProfileId des zu pushenden Profils", required = true) @PathVariable("id") String id,
			@ApiParam(value = "lastProfileChange der Clientseite", required = true) @PathVariable("clientProfileChange") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss,SSS") Date clientLastProfileChange,
			@ApiParam(value = "Client Preferences", required = true) @RequestBody String preferences)
			throws ProfileNotFoundException, ClientPreferencesOutdatedException {
		profileService.pushProfile(id, clientLastProfileChange, preferences, false);
		ResponseEntity<Void> response = new ResponseEntity<Void>(HttpStatus.OK);
		return response;
	}

	/**
	 * Speichert preferences von einem Client in der Datenbank entsprechend der
	 * Aktualität des Profils und des overwrite-Parameters. Ist der Zeitunkt lastProfileChange des zu pushenden
	 * Profils mindestens 5 Minuten aktueller als der des in der Datenbank
	 * bestehenden Profils, so wird dieses Überschrieben. Andernfalls wird
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
	@ApiOperation(value = "Speichert Preferences in DB", notes = "Zunächst wird in der DB nach dem entsprechenden Profil gesucht und der lastProfileChange mit dem Parameter des Aufrufs verglichen. "
			+ "Liegt der im Parameter spezifizierten Zeitpunkt <b>mindestens 5 Minuten nach</b> dem lastProfileChange der DB, d.h. die Preferences des Clients sind aktueller, so wird das Profil überschrieben. "
			+ "Andernfalls wird der Parameter overwrite überprüft. Ist dieser auf true gesetzt, so werden die bestehenden Preferences überschrieben. Andernfalls eine Fehlermeldung zurückgeliefert. "
			+ "\n \n Zeitstempel lastProfileContact wird aktualisiert. "
			+ "\n \n Parameter clientProfileChange muss im Format <b>yyyy-MM-dd'T'HH:mm:ss,SSS</b> übergeben werden. "
			+ "Dies kann in Java leicht mithilfe von <a href=https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html>SimpleDateFormat</a> realisiert werden.", response = Void.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Preferences erfolgreich ersetzt", response = Void.class),
			@ApiResponse(code = 400, message = "Ungültiger Parameter/ Falscher Datentyp \n \n Geworfene Exception: \n org.springframework.web.method.annotation. \n MethodArgumentTypeMismatchException", response = ErrorInformation.class),
			@ApiResponse(code = 404, message = "Kein Profil mit entsprechender Id gefunden  \n \n Geworfene Exception: \n de.privacy_avare.exeption.ProfileNotFoundException", response = ErrorInformation.class),
			@ApiResponse(code = 409, message = "ClientProfile veraltet \n \n Geworfene Exception: \n de.privacy_avare.exeption.ClientPreferencesOutdatedException", response = ErrorInformation.class) })
	public ResponseEntity<Void> pushProfilePreferences(
			@ApiParam(value = "ProfileId des zu pushenden Profils", required = true) @PathVariable("id") String id,
			@ApiParam(value = "lastProfileChange der Clientseite", required = true) @PathVariable("clientProfileChange") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss,SSS") Date clientLastProfileChange,
			@ApiParam(value = "Client Preferences", required = true) @RequestBody String preferences,
			@ApiParam(value = "Bestehende, aktuellere Preferences überschreiben?", required = true) @PathVariable("overwrite") boolean overwrite)
			throws ProfileNotFoundException, ClientPreferencesOutdatedException {
		profileService.pushProfile(id, clientLastProfileChange, preferences, overwrite);
		ResponseEntity<Void> response = new ResponseEntity<Void>(HttpStatus.OK);
		return response;
	}

	/**
	 * Sucht in der Datenbank nach der übergebenen ProfileId und liefert die
	 * Eigenschaft preferences entsprechend der Aktualität des gefundenen Profils
	 * zurück. Bei einem entsprechenden gefundenen Profil wird der Zeitpunkt
	 * lastProfileChange des Profils aus der Datenbank mit dem Zeitpunkt
	 * clientLastProfileChange verglichen. Ist das Profil aus der Datenbank nicht
	 * neuer als 5 Minuten, so wird eine Fehlermeldung zurückgeliefert.
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
	@ApiOperation(value = "Liest Preferences aus DB mit Vergleich der Zeitstempel", notes = "Sucht in der DB nach vorhandenem Profil und vergleich den lastProfileChange mit dem Parameter des Aufrufs. "
			+ "Liegt der im Prameter spezifizierte Zeitpunkt <b> mindestens 5 Minuten vor</b> dem lastProfileChange der DB, d.h. die Preferences des Servers sind aktueller, so wird das Profil geladen. "
			+ "Sollen trotzdem die Preferences aus der DB geladen werden, so ist die Methode ohne entsprechenden Parameter lastProfileChange aufzurufen."
			+ "Andernfalls wird eine Fehlermeldung gesendet. \n \n Zeitstempel lastProfileContact wird aktualisiert. \n \n Parameter clientProfileChange muss im Format <b>yyyy-MM-dd'T'HH:mm:ss,SSS</b> übergeben werden. "
			+ "Dies kann in Java leicht mithilfe von <a href=https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html>SimpleDateFormat</a> realisiert werden.", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Preferences erfolgreich geladen", response = String.class),
			@ApiResponse(code = 400, message = "Ungültiger Parameter/ Falscher Datentyp \n \n Geworfene Exception: \n org.springframework.web.method.annotation. \n MethodArgumentTypeMismatchException", response = ErrorInformation.class),
			@ApiResponse(code = 404, message = "Kein Profil mit entsprechender Id gefunden  \n \n Geworfene Exception: \n de.privacy_avare.exeption.ProfileNotFoundException", response = ErrorInformation.class),
			@ApiResponse(code = 409, message = "ServerProfile veraltet \n \n Geworfene Exception: \n de.privacy_avare.exeption.ServerPreferencesOutdatedException", response = ErrorInformation.class) })
	public ResponseEntity<String> pullProfilePreferences(
			@ApiParam(value = "ProfileId des zu pullenden Profils", required = true) @PathVariable("id") String id,
			@ApiParam(value = "lastProfileChange der Clientseite", required = true) @PathVariable("lastProfileChange") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss,SSS") Date clientLastProfileChange)
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
	@ApiOperation(value = "Liest Preferences aus DB ohne Vergleich der Zeitstempel", notes = "Sucht in der DB nach vorhandenem Profil. "
			+ "Wird ein Profil mit entsprechender ProfileId gefunden, so werden die gespeicherten Preferences <b>ohne Vergleich des lastProfileChange</b> zurückgeliefert. "
			+ "\n \n Zeitstempel lastProfileContact wird aktualisiert.", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Preferences erfolgreich geladen", response = String.class),
			@ApiResponse(code = 404, message = "Kein Profil mit entsprechender Id gefunden  \n \n Geworfene Exception: \n de.privacy_avare.exeption.ProfileNotFoundException", response = ErrorInformation.class) })
	public ResponseEntity<String> pullProfilePreferencesIgnoringLastProfileChange(
			@ApiParam(value = "ProfileId des entsprechenden Profils", required = true) @PathVariable("id") String id) {
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
	@ApiOperation(value = "Liest lastProfileChange aus DB", notes = "Sucht in der DB nach vorhandenem Profil. Wird ein Profil mit entsprechender ProfileId gefunden, so wird der gespeicherte Zeitpunkt lastProfileChange zurückgeliefert. "
			+ "\n \n Zeitstempel lastProfileContact wird <b>nicht aktualisiert</b>.", response = Date.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "lastProfileChange erfolgreich geladen", response = Date.class),
			@ApiResponse(code = 404, message = "Kein Profil mit entsprechender Id gefunden  \n \n Geworfene Exception: \n de.privacy_avare.exeption.ProfileNotFoundException", response = ErrorInformation.class) })
	public ResponseEntity<Date> getLastProfileChange(
			@ApiParam(value = "ProfileId des entsprechenden Profils", required = true) @PathVariable("id") String id) {
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
	@ApiOperation(value = "Liest lastProfileContact aus DB", notes = "Sucht in der DB nach vorhandenem Profil. Wird ein Profil mit entsprechender ProfileId gefunden, so wird der gespeicherte Zeitpunkt lastProfileContact zurückgeliefert. "
			+ "\n \n ZeitstempellastProfileContact wird <b>nicht aktualisiert</b>.", response = Date.class)

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "lastProfileContact erfolgreich geladen", response = Date.class),
			@ApiResponse(code = 404, message = "Kein Profil mit entsprechender Id gefunden  \n \n Geworfene Exception: \n de.privacy_avare.exeption.ProfileNotFoundException", response = ErrorInformation.class) })
	public ResponseEntity<Date> getLastProfileContact(
			@ApiParam(value = "ProfileId des entsprechenden Profils", required = true) @PathVariable("id") String id) {
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
	@ApiOperation(value = "Liest alle Profile aus DB", notes = "Sucht in der DB nach allen vorhandenen Profilen. Vorhandene Profile werden in einer Menge innerhalb eines JSON-Dokuments zurückgeliefert. "
			+ "Die Methode dient hauptsächlich zu Testzwecken in der Entwicklung. Je nach Anzahl der Profile in der DB kann die Bearbeitungsdauer der Anfrage stark variieren. "
			+ "\n \n Zeitstempel lastProfileContact der einzelnen Profile werden aktualisiert.", response = HashMap.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Profile erfolgreich geladen", response = HashMap.class),
			@ApiResponse(code = 409, message = "Keine Profile in DB gefunden \n \n Geworfene Exception: \n de.privacy_avare.exeption.NoProfilesInDatabaseException", response = ErrorInformation.class) })
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
	 * Beendet das Serverprogramm. Wird aktuell im Entwicklungsstadium verwendet und
	 * zur finalen Version voraussichtlich deaktiviert sein. Methode ist aufgrund
	 * der fehlenden GUI des Serverprogramms implementiert.
	 * 
	 */
	@Deprecated
	@RequestMapping(value = "/exit", method = RequestMethod.PUT)
	@ApiOperation(value = "Beendet das Serverprogramm", notes = "Beendet das Programm mithilfe eines Aufrufs von System.exit(0). "
			+ "Wird aktuell im Entwicklungsstadium verwendet und voraussichtlich in der finalen Version deaktiviert sein.", response = Void.class)
	public void test() {
		System.exit(0);
	}
}
