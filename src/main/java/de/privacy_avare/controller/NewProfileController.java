package de.privacy_avare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.privacy_avare.domain.Profile;
import de.privacy_avare.service.IdGeneratorService;
import de.privacy_avare.service.ProfileService;

/**
 * REST-Controller zur Bearbeitung von Anfragen zur Erzeugung neuer Profile.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

@RestController("newProfileControllerV1")
@RequestMapping(value = "/v1/newProfiles")
public class NewProfileController {
	@Autowired
	private IdGeneratorService idGeneratorService;

	@Autowired
	private ProfileService profileService;

	/**
	 * Erzeugung eines neuen Profils inkl. Generierung einer neuen UserID. Das
	 * erzeugte Profil wird automatisch in der Datenbank mit default-Werten
	 * hinterlegt. Entspricht UC1.
	 * 
	 * @return ProfileId des generierten Zufalls, über welche das Objekt in der
	 *         Datenbank angesprochen werden kann.
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Profile createProfile() {
		Profile profile = profileService.createNewProfile();
		return profile;
	}

	@RequestMapping(value = "/{profileId}", method = RequestMethod.GET)
	public String createProfile(@PathVariable("profileId") String profileId) {
		// Aufgerufene ID auf Vorhandensein prüfen.
		// Falls ID nicht vorhanden, auf Gültigkeit prüfen.
		// Bei Gültigkeit erzeugung eines neuen Profiles auf Basis der ID.

		return null;
	}
}
