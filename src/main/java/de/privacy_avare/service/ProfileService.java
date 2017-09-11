package de.privacy_avare.service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.privacy_avare.Repository.ProfileRepository;
import de.privacy_avare.domain.Profile;

/**
 * Klasse stellt verschiedene Services zur Interaktion mit Profilen in der
 * Datenbank bereit. Bei jedem Datenbankzugriff wird stets der Zeitpunkt
 * lastProfileContact auf den Zeitpunkt der Anfrage gesetzt.
 * 
 * @author Lukas Struppek
 * @version 1.0
 *
 */

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
	 * @throws Exception
	 *             UserID bereits vergeben.
	 */
	public Profile createNewProfile() throws Exception {
		String id = idGeneratorService.generateID();
		if (profileRepository.exists(id) == true) {
			id = idGeneratorService.generateID();
			if (profileRepository.exists(id) == true) {
				throw new Exception("UserID wird bereits in einem bestehenden Profil verwendet.");
			}
		}
		Profile profile = new Profile(id);
		updateProfile(profile);
		return profile;
	}

	/**
	 * Erzeugt ein neues Profil mit einer gegebenen UserID.
	 * 
	 * @param id
	 *            Bestehende UserID.
	 * @return Neu erzeugtes Profil.
	 * @throws Exception
	 *             UserID bereits vergeben.
	 */
	public Profile createNewProfile(String id) throws Exception {
		if (id.length() != 14) {
			throw new Exception("Ungültiges UserID-Format - keine 14-stellige ID.");
		}
		if (profileRepository.exists(id) == true) {
			throw new Exception("UserID wird bereits in einem bestehenden Profil verwendet.");
		}
		Profile profile = new Profile(id);
		updateProfile(profile);
		return profile;
	}

	/**
	 * Fügt ein Profil in die Datenbank ein. Bereits bestehende Profile mit
	 * identischer ProfileId werden überschrieben. Bei jedem Methodenaufruf wird im
	 * Profil der Zeitpunkt lastProfileContact aktualisiert.
	 * 
	 * @param profile
	 *            Das in die Datenbank zu schreibende Profil.
	 */
	public void updateProfile(Profile profile) {
		profile.setLastProfileContact(GregorianCalendar.getInstance(Locale.GERMANY).getTime());
		profileRepository.save(profile);
	}

	/**
	 * Fügt eine Liste von Profilen in die Datenbank ein. Bereits bestehende Profile
	 * mit identischer ProfileId werden überschrieben. Bei jedem Methodenaufruf wird
	 * in jedem Profil der Zeitpunkt lastProfileContact aktualisiert.
	 * 
	 * @param profileList
	 *            Liste der in die Datenbank zu schreibende Profile.
	 */
	public void updateProfiles(List<Profile> profileList) {
		for (Profile profile : profileList) {
			profile.setLastProfileContact(GregorianCalendar.getInstance(Locale.GERMANY).getTime());
			profileRepository.save(profile);
		}
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
		updateProfile(profile);
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
	public void setProfileOnDeletion(String id) {
		Profile profile = new Profile(id);

		// Bestimmung des aktuellen Zeitpunktes plus 100 Jahre.
		Calendar lastProfileChange = GregorianCalendar.getInstance(Locale.GERMANY);
		lastProfileChange.setTime(profile.getLastProfileChange());
		int yearPlusHundred = lastProfileChange.get(Calendar.YEAR) + 100;
		lastProfileChange.set(Calendar.YEAR, yearPlusHundred);

		// Setzen der Löschoptionen
		profile.setLastProfileChange(lastProfileChange.getTime());
		profile.setUnSync(true);

		// Überschreiben des zu löschenden Profils in der Datenbank
		updateProfile(profile);
	}

	/**
	 * Sucht ein durch die ProfileId festgelegtes Profile in der Datenbank. Der
	 * Zeitpunkt des lastProfileChange wird auf 100 Jahre in die Zukunft gesetzt.
	 * Zusätzlich wird das unSync-Flag auf true gesetzt.
	 * 
	 * @param profile
	 *            Instanz des zu löschenden Profils.
	 */
	public void setProfileOnDeletion(Profile profile) {
		setProfileOnDeletion(profile.getId());
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
	 * @throws Exception
	 *             Platzhalter
	 */
	public void pushProfile(Profile newProfile) throws Exception {
		Profile oldProfile = getProfileById(newProfile.getId());
		if (oldProfile == null) {
			System.out.println("Kein Profil in der DB gefunden");
			// Neues Profil wird gespeichert.
			createNewProfile(newProfile.getId());
		}

		if (newProfile.getLastProfileChange().after(oldProfile.getLastProfileChange())) {
			updateProfile(newProfile);
		} else {
			updateProfile(oldProfile);
		}
	}

	/**
	 * Liefert eine Liste aller in der Datenbank vorhandenen Profilen zurück.
	 * 
	 * @return Liste mit allen Profilen.
	 */
	public List<Profile> getAllProfiles() {
		List<Profile> list = profileRepository.findAllByOrderByIdAsc();
		updateProfiles(list);
		return list;
	}

	/**
	 * Liefert eine Liste aller in der Datenbank vorhandenen Profilen mit auf true
	 * gesetztem unSync-Flag zurück.
	 * 
	 * @return Liste mit allen Profilen mit gesetztem unSync-Flag.
	 */
	public List<Profile> getAllProfilesWithUnSync() {
		List<Profile> list = profileRepository.findAllByUnSyncTrue();
		updateProfiles(list);
		return list;
	}

}
