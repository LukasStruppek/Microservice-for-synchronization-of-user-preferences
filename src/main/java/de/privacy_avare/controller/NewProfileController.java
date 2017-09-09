package de.privacy_avare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.privacy_avare.service.IdGeneratorService;

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

	/**
	 * Erzeugung eines neuen Profils inkl. Generierung einer neuen UserID.
	 * 
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String createProfile() {
		String profileId = idGeneratorService.generateID();
		return profileId;
	}

	@RequestMapping(value = "/{profileId}", method = RequestMethod.GET)
	public String createProfile(@PathVariable("profileId") String profileId) {
		
		return null;
	}
}
