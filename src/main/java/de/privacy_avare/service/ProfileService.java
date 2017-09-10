package de.privacy_avare.service;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.privacy_avare.Repository.ProfileRepository;
import de.privacy_avare.domain.Profile;

@Service
public class ProfileService {
	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private IdGeneratorService idGeneratorService;

	/**
	 * Erzeugt ein neues Profile-Objekt in der Datenbank mit zufällig generierter
	 * ProfileID.
	 * 
	 * @return Neu erzeugtes Profil.
	 */
	public Profile createNewProfile() {
		String id = idGeneratorService.generateID();
		Profile profile = new Profile(id);
		updateProfile(profile);
		return profile;
	}

	/**
	 * Fügt ein Profil in die Datenbank ein. Bereits bestehende Profile mit
	 * identischer ProfileId werden überschrieben.
	 * 
	 * @param profile
	 *            Das in die Datenbank zu schreibende Profil.
	 */
	public void updateProfile(Profile profile) {
		profile.setLastProfileContact(Calendar.getInstance(Locale.GERMANY));
		profileRepository.save(profile);
	}

	/**
	 * Sucht in der Datenbank nach einem Profil mit einer bestimmten ProfileId.
	 * 
	 * @param id
	 *            ProfileId, nach welcher in der Datenbank gesucht werden soll.
	 * @return Vorhandenes Profil der Datenbank.
	 */
	public Profile getProfileById(String id) {
		Profile profile = profileRepository.findOne(id);
		return profile;
	}

	/**
	 * Sucht ein durch die ProfileId festgelegtes Profile in der Datenbank. Der
	 * Zeitpunkt des lastProfileChange wird auf 100 Jahre in die Zukunft gesetzt.
	 * Zusätzlich wird das unSync-Flag auf true gesetzt.
	 * 
	 * @param id
	 *            ProfileId des zu löschen Profiles.
	 */
	public void removeProfile(String id) {
		Profile delProfile = new Profile(id);

		// Bestimmung des aktuellen Zeitpunktes plus 100 Jahre.
		Calendar lastProfileChange = delProfile.getLastProfileChange();
		int yearPlusHundred = lastProfileChange.get(Calendar.YEAR) + 100;
		lastProfileChange.set(Calendar.YEAR, yearPlusHundred);

		// Setzen der Löschoptionen
		delProfile.setLastProfileChange(lastProfileChange);
		delProfile.setUnSync(true);

		// Überschreiben des zu löschenden Profils in der Datenbank
		profileRepository.save(delProfile);
	}

	/**
	 * Sucht ein durch die ProfileId festgelegtes Profile in der Datenbank. Der
	 * Zeitpunkt des lastProfileChange wird auf 100 Jahre in die Zukunft gesetzt.
	 * Zusätzlich wird das unSync-Flag auf true gesetzt.
	 * 
	 * @param id
	 *            ProfileId des zu löschen Profiles.
	 */
	public void removeProfile(Profile profile) {
		removeProfile(profile.getId());
	}

	/**
	 * Pushen eines aktualisierten Profils. Ist der Zeitunkt lastProfileChange des
	 * zu pushenden Profils aktueller als der des in der Datenbank bestehenden
	 * Profils, so wird dieses Überschrieben. Andernfalls wird das ursprüngliche
	 * Profil in der Datenbank beibehalten. Der Zeitpunkt lastProfileContact wird in
	 * beiden Fällen aktualisiert.
	 * 
	 * @param newProfile
	 *            Das zu pushende Profil.
	 */
	public void pushProfile(Profile newProfile) {
		Profile oldProfile = getProfileById(newProfile.getId());
		if (oldProfile == null) {
			System.out.println("Kein Profil in der DB gefunden");
			// Neues Profil wird gespeichert.
			updateProfile(newProfile);
		}

		if (newProfile.getLastProfileChange().after(oldProfile.getLastProfileChange())) {
			updateProfile(newProfile);
		} else {
			updateProfile(oldProfile);
		}
	}

	public List<Profile> getAllProfiles() {
		List<Profile> list = profileRepository.findAllByOrderByIdAsc();
		return list;
	}

}
