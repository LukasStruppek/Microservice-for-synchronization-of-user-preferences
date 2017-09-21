package de.privacy_avare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.privacy_avare.domain.Profile;
import de.privacy_avare.exeption.MalformedProfileIdException;
import de.privacy_avare.exeption.ProfileAlreadyExistsException;
import de.privacy_avare.service.ProfileService;

/**
 * Die Klasse stellt eine REST-API zur Verfügung, über welche externe Anfragen
 * zur Erzeugung neuer Profile gestellt werden können. Zur Verarbeitung der
 * Anfragen werden diese an entsprechende interne Services weitergeleitet.
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

@RestController("newProfileControllerV1")
@RequestMapping(value = "/v1/newProfiles")
public class NewProfileController {

	/**
	 * Default Konstruktor
	 */
	public NewProfileController() {

	}

	/**
	 * Service stellt diverse Methoden zur Verarbeitung von Profilen sowie der
	 * Ablage in der Datenbank bzw. dem Abruf von Profilen aus der Datenbank bereit.
	 * Instanz wird über Dependency Injection bereitgestellt.
	 * 
	 * @see de.privacy_avare.service.ProfileService
	 */
	@Autowired
	private ProfileService profileService;

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<String> createProfile() throws ProfileAlreadyExistsException {
		Profile serverProfile = profileService.createNewProfile();
		ResponseEntity<String> response = new ResponseEntity<String>(serverProfile.get_id(), HttpStatus.CREATED);
		return response;
	}

	/**
	 * Erzeugung eines neuen Profils basierend auf gegebener ProfileId. Das erzeugte
	 * Profil wird automatisch in der Datenbank mit default-Werten hinterlegt.
	 * Entspricht UC1 mit vorhandener ProfileId.
	 * 
	 * @param id
	 *            ProfileId, mit welcher ein neues Profil erzeugt werden soll.
	 * @return ProfileId des generierten Profils.
	 * @throws ProfileAleadyExistsException
	 *             Wird geworfen, falls bei der Id-Generierung eine bereits
	 *             vorhandene ProfileId generiert wird.
	 * @throws MalformedProfileIdException
	 *             Wird geworfen, wenn die übergebene ProfileId nicht dem Aufbau
	 *             einer gültigen ProfileId entspricht.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public ResponseEntity<String> createProfile(@PathVariable("id") String id)
			throws ProfileAlreadyExistsException, MalformedProfileIdException {
		Profile serverProfile = profileService.createNewProfile(id);
		ResponseEntity<String> response = new ResponseEntity<String>(serverProfile.get_id(), HttpStatus.CREATED);
		return response;
	}
}
