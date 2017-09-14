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
	private IdService idService;

	/**
	 * Erzeugt ein neues Profile-Objekt in der Datenbank mit zufällig generierter
	 * ProfileID.
	 * 
	 * @return Neu erzeugtes Profil.
	 * @throws RuntimeException
	 *             UserID bereits vergeben.
	 */
	public Profile createNewProfile() throws RuntimeException {
		String id = idService.generateID();
		if (idService.isIdAlreadyTaken(id) == true) {
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
	 * @throws RuntimeException
	 *             UserID bereits vergeben oder ungültiges Format.
	 */
	public Profile createNewProfile(String id) throws RuntimeException {
		if (idService.validateId(id) == false) {
			throw new RuntimeException("Ungültiges UserID-Format - keine 16-stellige ID.");
		}
		if (idService.isIdAlreadyTaken(id) == true) {
			throw new RuntimeException("UserID wird bereits in einem bestehenden Profil verwendet.");
		}
		Profile profile = new Profile(id);
		updateProfile(profile);
		return profile;
	}

	/**
	 * Sucht in der Datenbank nach einem Profil mit einer bestimmten ProfileId.
	 * Eigenschaft lastProfileContact wird in der Datenbank aktualisiert. Das
	 * zurückgelieferte Profil weist jedoch noch den ursprünglichen Zeitstempel auf.
	 * 
	 * @param id
	 *            ProfileId, nach welcher in der Datenbank gesucht werden soll.
	 * @return Vorhandenes Profil der Datenbank.
	 */
	public Profile getProfileById(String id) {
		Profile dbProfile = profileRepository.findOne(id);
		if (dbProfile == null) {
			throw new RuntimeException("Kein Profil mit entsprechender ID gefunden");
		} else {
			updateProfile(dbProfile);
		}
		return dbProfile;
	}

	/**
	 * Sucht in der Datenbank nach einem Profil mit einer bestimmten ProfileId. Wird
	 * ein Profil gefunden, so wird seine Eigenschaft lastProfileChangeTimestamp mit
	 * dem Parameter clientLastProfileChange verglichen. Ist das Profil aus der
	 * Datenbank mindestens 5 Minuten 'neuer' als der im Parameter spezifizierte
	 * Zeitstempel, so wird das Profil aus der Datenbank zurückgeliefert.
	 * Andernfalls wird null zerückgegeben.
	 * 
	 * Der Wert lastProfileContactTimestamp wird in der Datenbank in allen Fällen
	 * angepasst.
	 * 
	 * @param id
	 *            ProfileId des in der Datenbank zu suchenden Profils.
	 * @param clientLastProfileChange
	 *            Entspricht der Aktualität des Profils auf dem Clientgerät.
	 * @return Gefundenes, aktuelleres Datenbankprofil oder null.
	 */
	public Profile getProfileByIdComparingLastChange(String id, Date clientLastProfileChange) {
		Profile dbProfile = getProfileById(id);
		if (dbProfile == null) {
			new RuntimeException("Kein Profil mit dieser ID gefunden");
		}
		if (dbProfile.isUnSync() == true) {
			new RuntimeException("Profil ist gelöscht!");
		}
		GregorianCalendar dbLastProfileChangeTimestamp = new GregorianCalendar();
		dbLastProfileChangeTimestamp.setTime(dbProfile.getLastProfileChange());
		dbLastProfileChangeTimestamp.set(Calendar.MINUTE, dbLastProfileChangeTimestamp.get(Calendar.MINUTE) - 5);
		if (dbLastProfileChangeTimestamp.getTime().after(clientLastProfileChange)) {
			return dbProfile;
		} else {
			return null;
		}
	}

	/**
	 * Liefert eine Liste aller in der Datenbank vorhandenen Profilen, absteigend
	 * nach der ProfileId sortiert, zurück.
	 * 
	 * Bei allen gefundenen Profilen wird die Eigenschaft
	 * lastProfileContactTimestamp aktualisiert.
	 * 
	 * @return Liste mit allen Profilen.
	 */
	public Iterable<Profile> getAllProfiles() {
		Iterable<Profile> list = profileRepository.findAllByOrderByIdAsc();
		if (list != null) {
			updateProfiles(list);
		}
		return list;
	}

	/**
	 * Liefert eine Liste aller in der Datenbank vorhandenen Profilen mit auf true
	 * gesetztem unSync-Flag zurück.
	 * 
	 * Bei allen gefundenen Profilen wird die Eigenschaft
	 * lastProfileContactTimestamp aktualisiert.
	 * 
	 * @return Liste mit allen Profilen mit gesetztem unSync-Flag.
	 */
	public Iterable<Profile> getAllProfilesWithUnSync() {
		Iterable<Profile> list = profileRepository.findAllByUnSyncTrue();
		if (list != null) {
			updateProfiles(list);
		}
		return list;
	}

	/**
	 * Liefert eine Liste aller in der Datenbank vorhandenen Profilen mit auf false
	 * gesetztem unSync-Flag zurück.
	 * 
	 * Bei allen gefundenen Profilen wird die Eigenschaft
	 * lastProfileContactTimestamp aktualisiert.
	 * 
	 * @return Liste mit allen Profilen mit ungesetzem unSync-Flag.
	 */
	public Iterable<Profile> getAllProfilesWithoutUnSync() {
		Iterable<Profile> list = profileRepository.findAllByUnSyncFalse();
		if (list != null) {
			updateProfiles(list);
		}
		return list;
	}

	/**
	 * Liefert den Zeitstempel für die Eigenschaft lastProfileChange des
	 * entsprechenden Profils zurück. Die Eigenschaft lastProfileContact wird bei
	 * diesem Zugriff nicht verändert.
	 * 
	 * @param id
	 *            ProfielId des gesuchten Profils.
	 * @return lastProfileChange des entsprechenden Profils.
	 */
	public Date getLastProfileChange(String id) {
		Profile dbProfile = profileRepository.findOne(id);
		if (dbProfile == null) {
			throw new RuntimeException("Kein Profil mit dieser ID gefunden");
		}
		Date dbProfileLastProfileChange = dbProfile.getLastProfileChange();
		return dbProfileLastProfileChange;
	}

	/**
	 * Liefert den Zeitstempel für die Eigenschaft lastProfileContact des
	 * entsprechenden Profils zurück. Die Eigenschaft lastProfileContact wird bei
	 * diesem Zugriff nicht verändert.
	 * 
	 * @param id
	 *            ProfielId des gesuchten Profils.
	 * @return lastProfileContact des entsprechenden Profils.
	 */
	public Date getLastProfileContact(String id) {
		Profile dbProfile = profileRepository.findOne(id);
		if (dbProfile == null) {
			throw new RuntimeException("Kein Profil mit dieser ID gefunden");
		}
		Date dbProfileLastProfileContact = dbProfile.getLastProfileContact();
		return dbProfileLastProfileContact;
	}

	/**
	 * Liefert die Preferences des entsprechenden Profils zurück. Die Eigenschaft
	 * lastProfileContact wird bei diesem Zugriff nicht verändert.
	 * 
	 * @param id
	 *            ProfielId des gesuchten Profils.
	 * @return Preferences des entsprechenden Profils.
	 */
	public Object getPreferences(String id) {
		Profile dbProfile = profileRepository.findOne(id);
		if (dbProfile == null) {
			throw new RuntimeException("Kein Profil mit dieser ID gefunden");
		}
		Object dbProfilePreferences = dbProfile.getPreferences();
		return dbProfilePreferences;
	}

	/**
	 * Liefert den Status des unSync-Flags des entsprechenden Profils zurück. Die
	 * Eigenschaft lastProfileContact wird bei diesem Zugriff nicht verändert.
	 * 
	 * @param id
	 *            ProfielId des gesuchten Profils.
	 * @return lastProfileChange des entsprechenden Profils.
	 */
	public boolean isUnSync(String id) {
		Profile dbProfile = profileRepository.findOne(id);
		if (dbProfile == null) {
			throw new RuntimeException("Kein Profil mit dieser ID gefunden");
		}
		boolean dbProfileUnSync = dbProfile.isUnSync();
		return dbProfileUnSync;
	}

	/**
	 * Pushen eines aktualisierten Profils. Ist der Zeitunkt lastProfileChange des
	 * zu pushenden Profils aktueller als der des in der Datenbank bestehenden
	 * Profils, so wird dieses Überschrieben. Andernfalls wird entsprechend dem
	 * Parameter overwrite das ursprüngliche Profil in der Datenbank beibehalten
	 * (overwrite = false) oder überschrieben (overwrite = true).
	 * 
	 * Der Zeitpunkt lastProfileContact wird in allen Fällen aktualisiert.
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
	public void pushProfile(String id, Date clientLastProfileChange, Object clientPreferences, boolean overwrite)
			throws RuntimeException {
		// Abrufen des entsprechenden Profils aus der Datenbank
		Profile dbProfile = getProfileById(id);

		// Falls kein Profil in der DB ist, wird das neue in die DB geschrieben.
		if (dbProfile == null) {
			throw new RuntimeException("Kein Profil in der DB vorhanden");
		} else if (dbProfile.isUnSync() == true) {
			throw new RuntimeException("Profil zum löschen freigegeben");
		} else {
			if (overwrite == true) {
				dbProfile.setpreferences(clientPreferences);
				dbProfile.setLastProfileChange(clientLastProfileChange);
				updateProfile(dbProfile);
			} else {
				GregorianCalendar dbProfileLastProfileChange = new GregorianCalendar();
				dbProfileLastProfileChange.setTime(dbProfile.getLastProfileChange());
				dbProfileLastProfileChange.set(Calendar.MINUTE, dbProfileLastProfileChange.get(Calendar.MINUTE) + 5);

				if (dbProfileLastProfileChange.getTime().before(clientLastProfileChange) || overwrite == true) {
					dbProfile.setpreferences(clientPreferences);
					dbProfile.setLastProfileChange(clientLastProfileChange);
					updateProfile(dbProfile);
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
	 * Die Methode dient hauptsächlich zur Verwendung in anderen Service-Methoden,
	 * um eine Aktualisierung der Eigenschaft lastProfileContactTimestamp
	 * sicherzustellen.
	 * 
	 * @param profile
	 *            Das in die Datenbank zu schreibende Profil.
	 */
	public void updateProfile(Profile profile) {
		profile.setLastProfileContact(GregorianCalendar.getInstance(Locale.GERMANY).getTime());
		profileRepository.save(profile);
	}

	/**
	 * Fügt eine Menge von Profilen in die Datenbank ein. Bereits bestehende Profile
	 * mit identischer ProfileId werden überschrieben. Bei jedem Methodenaufruf wird
	 * in jedem Profil der Zeitpunkt lastProfileContact aktualisiert.
	 * 
	 * Die Methode dient hauptsächlich zur Verwendung in anderen Service-Methoden,
	 * um eine Aktualisierung der Eigenschaft lastProfileContactTimestamp
	 * sicherzustellen.
	 * 
	 * @param profileList
	 *            Liste der in die Datenbank zu schreibende Profile.
	 */
	public void updateProfiles(Iterable<Profile> profileList) {
		for (Profile profile : profileList) {
			profile.setLastProfileContact(GregorianCalendar.getInstance(Locale.GERMANY).getTime());
			profileRepository.save(profile);
		}
	}

	/**
	 * Sucht ein durch die ProfileId eindeutig festgelegtes Profile in der
	 * Datenbank. Der Zeitpunkt des lastProfileChange wird auf 100 Jahre in die
	 * Zukunft gesetzt. Zusätzlich wird das unSync-Flag auf true gesetzt.
	 * 
	 * Der Wert lastProfileContactTimestamp wird in der Datenbank in allen Fällen
	 * angepasst.
	 * 
	 * @param id
	 *            ProfileId des zu löschen Profiles.
	 */
	public void setProfileOnDeletion(String id) {
		Profile dbProfile = getProfileById(id);
		if (dbProfile == null) {
			throw new RuntimeException("Kein Profil mit dieser ID vorhanden");
		}

		else if (dbProfile.isUnSync() == true) {
			throw new RuntimeException("Profile bereits gelöscht");
		} else {
			// Bestimmung des aktuellen Zeitpunktes plus 100 Jahre.
			Calendar lastProfileChange = GregorianCalendar.getInstance(Locale.GERMANY);
			lastProfileChange.setTime(dbProfile.getLastProfileChange());
			lastProfileChange.set(Calendar.YEAR, lastProfileChange.get(Calendar.YEAR) + 100);

			// Setzen der Eigenschaften lastProfileChange + 100 Jahre und unSync = true;
			// Löschen der Nutzerpräferenzen
			dbProfile.setLastProfileChange(lastProfileChange.getTime());
			dbProfile.setUnSync(true);
			dbProfile.setpreferences(null);

			// Überschreiben des zu löschenden Profils in der Datenbank
			updateProfile(dbProfile);
		}
	}

	/**
	 * Sucht ein durch die ProfileId eindeutig festgelegtes Profile in der
	 * Datenbank. Der Zeitpunkt des lastProfileChange wird auf 100 Jahre in die
	 * Zukunft gesetzt. Zusätzlich wird das unSync-Flag auf true gesetzt.
	 * 
	 * Der Wert lastProfileContactTimestamp wird in der Datenbank in allen Fällen
	 * angepasst.
	 * 
	 * @param profile
	 *            Instanz des zu löschenden Profils.
	 */
	public void setProfileOnDeletion(Profile profile) {
		setProfileOnDeletion(profile.getId());
	}

	/**
	 * Speichern von Profilen ohne Anpassen des lastProfileContact Timestamps. Diese
	 * Methode ist eher zu Testzwecken gedacht und sollte in der finalen Anwendung
	 * nicht mehr genutzt werden.
	 * 
	 * @param p
	 * 
	 * @deprecated Nur für Testzwecke gedacht!
	 */
	public void save(Profile p) {
		profileRepository.save(p);
	}

}
