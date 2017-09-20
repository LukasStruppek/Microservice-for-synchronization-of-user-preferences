package de.privacy_avare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.privacy_avare.domain.Profile;
import de.privacy_avare.service.ProfileService;

/**
 * Die Klasse stellt eine REST-API zur Verfügung, über welche externe Anfragen
 * zur Erzeugung neuer Profile gestellt werden können. Zur Verarbeitung der
 * Anfragen werden diese an entsprechende Services weitergeleitet.
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see de.privacy_avare.service.ProfileService
 */

@RestController("newProfileControllerV1")
@RequestMapping(value = "/v1/newProfiles")
public class NewProfileController {

	/**
<<<<<<< HEAD
	 * Service stellt Methode zur automatischen Generierung einer eindeutigen
	 * ProfileId bereit. Instanz wird über Dependency Injection bereitgestellt.
	 * 
	 * @see de.privacy_avare.service.IdService
	 */

	/**
=======
>>>>>>> parent of 0cfadc7... Revert "Aktueller Stand vor Reverting"
	 * Service stellt diverse Methoden zur Verarbeitung von Profilen sowie der
	 * Ablage in der Datenbank bzw. dem Abruf von Profilen aus der Datenbank bereit.
	 * Instanz wird über Dependency Injection bereitgestellt.
	 * 
	 * @see de.privacy_avare.service.ProfileService
	 */
	@Autowired
	private ProfileService profileService;

	/**
<<<<<<< HEAD
	 * Erzeugung eines neuen Profils inkl. Generierung einer neuen UserID. Das
=======
	 * Default-Konstruktor.
	 */
	public NewProfileController() {

	}

	/**
	 * Erzeugung eines neuen Profils inklusive Generierung einer neuen UserID. Das
>>>>>>> parent of 0cfadc7... Revert "Aktueller Stand vor Reverting"
	 * erzeugte Profil wird automatisch in der Datenbank mit default-Werten
	 * hinterlegt. Entspricht UC1 ohne Parameter.
	 * 
	 * @return ResponseEntity, welche im Body die ProfileId des generierten Profils
	 *         enthält.
	 * @throws Exception
	 *             Profilerzeugung fehlgeschlagen
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
<<<<<<< HEAD
	public ResponseEntity<String> createProfile() throws Exception {
=======
	public ResponseEntity<String> createProfile() throws ProfileAlreadyExistsException {
		System.out.println("Anfrage erhalten");
>>>>>>> parent of 0cfadc7... Revert "Aktueller Stand vor Reverting"
		Profile serverProfile = profileService.createNewProfile();
		ResponseEntity<String> response = new ResponseEntity<String>(serverProfile.getId(), HttpStatus.CREATED);
		return response;
	}

	/**
	 * Erzeugung eines neuen Profils basierend auf gegebener UserID. Das erzeugte
	 * Profil wird automatisch in der Datenbank mit default-Werten hinterlegt.
	 * Entspricht UC1 mit Parameter.
	 * 
	 * @param id
	 *            ProfileId, mit welcher ein neues Profil erzeugt werden soll.
	 * @return id des generierten Zufalls, über welche das Objekt in der Datenbank
	 *         angesprochen werden kann.
	 * @throws Exception
	 *             Profilerzeugung fehlgeschlagen
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public ResponseEntity<String> createProfile(@PathVariable("id") String id) throws Exception {
		Profile serverProfile = profileService.createNewProfile(id);
		ResponseEntity<String> response = new ResponseEntity<String>(serverProfile.getId(), HttpStatus.CREATED);
		return response;
	}
}
