package de.privacy_avare.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.privacy_avare.domain.Profile;
import de.privacy_avare.repository.ProfileRepository;

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
			throw new RuntimeException("UserID wird bereits in einem bestehenden Profil verwendet.");
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
		if (id.length() != 16) {
			throw new Exception("Ungültiges UserID-Format - keine 16-stellige ID.");
		}
		if (profileRepository.exists(id) == true) {
			throw new Exception("UserID wird bereits in einem bestehenden Profil verwendet.");
		}
		Profile profile = new Profile(id);
		updateProfile(profile);
		return profile;
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
	 * Sucht in der Datenbank nach einem Profil mit einer bestimmten ProfileId. Wird
	 * ein Profil gefunden, so wird seine Eigenschaft lastProfileChangeTimestamp mit
	 * dem Parameter clientLastProfileChangeTimestamp verglichen. Ist das Profil aus
	 * der Datenbank mindestens 5 Minuten 'neuer' als der im Parameter spezifizierte
	 * Zeitstempel, so wird das Profil aus der Datenbank zurückgeliefert.
	 * Andernfalls wird null zerückgegeben.
	 * 
	 * @param id
	 *            ProfileId des in der Datenbank zu suchenden Profils.
	 * @param clientLastProfileChangeTimestamp
	 *            Entspricht der Aktualität des Profils auf dem Clientgerät.
	 * @return Gefundenes, aktuelleres Profil oder null.
	 */
	public Profile getProfileByIdComparingLastChange(String id, Date clientLastProfileChangeTimestamp) {
		Profile dbProfile = getProfileById(id);
		if (dbProfile == null) {
			new RuntimeException("kein Profil mit dieser ID gefunden");
		}
		GregorianCalendar dbLastProfileChangeTimestamp = new GregorianCalendar();
		dbLastProfileChangeTimestamp.setTime(dbProfile.getLastProfileChange());
		dbLastProfileChangeTimestamp.set(Calendar.MINUTE, dbLastProfileChangeTimestamp.get(Calendar.MINUTE) - 5);
		if (dbLastProfileChangeTimestamp.after(clientLastProfileChangeTimestamp)) {
			return dbProfile;
		} else {
			return null;
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

	/**
	 * Pushen eines aktualisierten Profils. Ist der Zeitunkt lastProfileChange des
	 * zu pushenden Profils aktueller als der des in der Datenbank bestehenden
	 * Profils, so wird dieses Überschrieben. Andernfalls wird entsprechend dem
	 * Parameter overwrite das ursprüngliche Profil in der Datenbank beibehalten
	 * (overwrite = false) oder überschrieben (overwrite = true). Der Zeitpunkt
	 * lastProfileContact wird in allen Fällen aktualisiert.
	 * 
	 * Ist kein Profil mit entsprechender ID in der Datenbank vorhanden, so wird das
	 * zu pushende Profil in die DB geschrieben.
	 * 
	 * @param clientProfile
	 *            Das zu pushende Profil.
	 * @param overwrite
	 *            Bestehendes, aktuelleres Profil überschreiben?
	 * @throws Exception
	 *             Platzhalter
	 */
	public void pushProfile(Profile clientProfile, boolean overwrite) throws Exception {
		// Abrufen des entsprechenden Profils aus der Datenbank
		Profile dbProfile = getProfileById(clientProfile.getId());

		// Falls kein Profil in der DB ist, wird das neue in die DB geschrieben.
		if (dbProfile == null) {
			// Neues Profil wird in der DB erzeugt und anschließend überschrieben
			createNewProfile(clientProfile.getId());
			updateProfile(clientProfile);
		} else {

			//
			GregorianCalendar dbProfileLastProfileChange = new GregorianCalendar();
			dbProfileLastProfileChange.setTime(dbProfile.getLastProfileChange());
			dbProfileLastProfileChange.set(Calendar.MINUTE, dbProfileLastProfileChange.get(Calendar.MINUTE) + 5);
			if (clientProfile.getLastProfileChange().after(dbProfile.getLastProfileChange())) {
				updateProfile(clientProfile);
			} else {
				if (overwrite == true) {
					updateProfile(clientProfile);
				} else {
					updateProfile(dbProfile);
				}
			}
		}
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
	 * Sucht ein durch die ProfileId festgelegtes Profile in der Datenbank. Der
	 * Zeitpunkt des lastProfileChange wird auf 100 Jahre in die Zukunft gesetzt.
	 * Zusätzlich wird das unSync-Flag auf true gesetzt.
	 * 
	 * @param id
	 *            ProfileId des zu löschen Profiles.
	 */
	public void setProfileOnDeletion(String id) {
		Profile profile = getProfileById(id);
		if (profile == null) {
			throw new RuntimeException("Kein Profil mit dieser ID vorhanden");
		}

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
	 * Speichern von Profilen ohne Anpassen des lastProfileContact Timestamps!
	 * @param p
	 */
	public void save(Profile p)
	{
		profileRepository.save(p);
	}

}
