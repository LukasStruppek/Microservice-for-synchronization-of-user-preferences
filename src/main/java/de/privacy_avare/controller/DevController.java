package de.privacy_avare.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.privacy_avare.domain.Profile;
import de.privacy_avare.dto.ErrorInformation;
import de.privacy_avare.exeption.NoProfilesInDatabaseException;
import de.privacy_avare.exeption.ProfileAlreadyExistsException;
import de.privacy_avare.exeption.ProfileNotFoundException;
import de.privacy_avare.repository.ProfileRepository;
import de.privacy_avare.service.ClearanceService;
import de.privacy_avare.service.ProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Die Klasse stellt eine REST-API zur Erleichterung der Entwicklung zur
 * Verfügung, über welche externe Anfragen bezüglich bereits existierender
 * Profile gestellt werden können. Zur Verarbeitung der Anfragen werden diese an
 * entsprechende Services weitergeleitet. Die Schnittstelle ist ausdrücklich
 * nicht für die Verwendung in Endprodukten gedacht, sondern dient lediglich zu
 * Testzwecken währen der Entwicklung.
 * 
 * Daher ist die Schnittstelle mit BasicAuth gesichert und kann nur von Anwender
 * mit Admin-Rechten verwendet werden.
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
@RestController("DevControllerV1")
@RequestMapping(value = "/v1/dev")
@Api(tags = "Entwickler Funktionen")
public class DevController {
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
	 * Service stellt Verbindung zur Datenbank bereit und wird dazu genutzt, Profile
	 * ohne Überprüfung in der Datenbank zu speichern.
	 */
	@Autowired
	private ProfileRepository profileRepository;

	/**
	 * Service zum Aufräumen veralteter Profile in der Datenbank.
	 */
	@Autowired
	private ClearanceService clearanceService;

	/**
	 * Sucht alle Profile in der Datenbank und liefert diese in Form einer Liste
	 * zurück, unabhängig vom Status der einzelnen Profile. Werden in der DB keine
	 * Profile gefunden, so wird eine ProfileNotFoundException zurückgeliefert.
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
	 * Beendet das Serverprogramm über System.exit(0). Die Methode soll eine
	 * Erleichterung darstellen, da eine GUI für das Programm nicht vorhanden ist.
	 */
	@RequestMapping(value = "/exit", method = RequestMethod.PUT)
	@ApiOperation(value = "Beendet das Serverprogramm", notes = "Beendet das Programm mithilfe eines Aufrufs von System.exit(0). ", response = Void.class)
	public void exit() {
		System.exit(0);
	}

	/**
	 * Ruft die Methode zum Löschen veralteter Profile auf. Die Methode entspricht
	 * dem manuellen Aufruf des Aufräumprozesses, welcher in einem festgelegten
	 * Zeitintervall automatisch durchgeführt wird.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ApiOperation(value = "Löschen aller veralteten Profile in DB", notes = "Entspricht dem Methodenaufruf des automatisierten Löschens. ", response = Void.class)
	public void cleanDatabase() {
		clearanceService.cleanDatabase();
	}

	/**
	 * Generiert ein Profil mit veraltetem lastProfileContact und der Preference
	 * "Veraltetes Profil". Die Methode wird verwendet, um den Aufräumprozess zu
	 * testen.
	 * 
	 * @return ResponseEntity, welche im Body die ProfileId des generierten Profils
	 *         enthält.
	 * @throws ProfileAlreadyExistsException
	 *             Generierte ProfileId bereits vergeben.
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ApiOperation(value = "Generiert ein neues, veraltetes Profil", notes = "Generiert ein neues Profil in der Datenbank mit lastProfileContact = 0 und preferences = 'Veraltetes PRofil'. Wird zum Testen des Aufräumprozesses genutzt", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Profil erfolgreich erzeugt und abgespeichert", response = String.class),
			@ApiResponse(code = 409, message = "Generierte ProfileId bereits vergeben \n \n Geworfene Exception: \n de.privacy_avare.exeption.ProfileAlreadyExistsException", response = ErrorInformation.class) })
	public ResponseEntity<String> createProfile() throws ProfileAlreadyExistsException {
		Profile serverProfile = profileService.createNewProfile();
		serverProfile.setLastProfileContact(new Date(0));
		serverProfile.setPreferences("Veraltetes Profil");
		profileRepository.save(serverProfile);
		ResponseEntity<String> response = new ResponseEntity<String>(serverProfile.get_id(), HttpStatus.CREATED);
		return response;
	}
}
