package de.privacy_avare.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.privacy_avare.domain.Profile;
import de.privacy_avare.exeption.ClientProfileOutdatedException;
import de.privacy_avare.exeption.MalformedProfileIdException;
import de.privacy_avare.exeption.NoProfilesInDatabaseException;
import de.privacy_avare.exeption.ProfileAlreadyExistsException;
import de.privacy_avare.exeption.ProfileSetOnDeletionException;
import de.privacy_avare.exeption.ServerProfileOutdatedException;
import de.privacy_avare.exeption.ProfileNotFoundException;
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
	 * Erzeugt ein neues Profil mit einer gegebenen ProfileId. Bei erfolgreicher
	 * Erzeugung wird ein entsprechendes DB-Profil angelegt, wobei die Eigenschaft
	 * lastProfileChangeTimestamp auf 0 gesetzt wird. Das DB-Profil enthält noch
	 * keine preferences.
	 * 
	 * 
	 * @return Neu erzeugtes Profil.
	 * @throws ProfileAlreadyExistsException
	 *             ProfileId bereits vergeben.
	 * 
	 */
	public Profile createNewProfile() throws ProfileAlreadyExistsException {
		String id = idService.generateId();
		if (idService.isIdAlreadyTaken(id) == true) {
			throw new ProfileAlreadyExistsException(
					"ProfileId (" + id + ") wird bereits in einem bestehenden Profil verwendet.");
		}
		Profile profile = new Profile(id);
		updateProfile(profile);
		return profile;
	}

	/**
	 * Erzeugt ein neues Profil mit einer gegebenen ProfileId. Bei erfolgreicher
	 * Erzeugung wird ein entsprechendes DB-Profil angelegt, wobei die Eigenschaft
	 * lastProfileChangeTimestamp auf 0 gesetzt wird. Das DB-Profil enthält noch
	 * keine preferences.
	 * 
	 * 
	 * @param id
	 *            Bestehende ProfileID.
	 * @return Neu erzeugtes Profil.
	 * @throws ProfileAlreadyExistsException
	 *             ProfileId bereits vergeben.
	 * @throws MalformedProfileIdException
	 *             Ungültiges ProfileId-Format.
	 */
	public Profile createNewProfile(String id) throws ProfileAlreadyExistsException, MalformedProfileIdException {
		if (idService.validateId(id) == false) {
			throw new MalformedProfileIdException("Ungültiges ProfileId-Format (" + id + ") - keine 16-stellige ID.");
		}
		if (idService.isIdAlreadyTaken(id) == true) {
			throw new ProfileAlreadyExistsException(
					"ProfileId (" + id + ") wird bereits in einem bestehenden Profil verwendet.");
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
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 * @throws ProfileSetOnDeletionException
	 *             Profil zum Löschen auf unSync gesetzt.
	 */
	public Profile getProfileById(String id) throws ProfileNotFoundException, ProfileSetOnDeletionException {
		Profile dbProfile = profileRepository.findOne(id);
		if (dbProfile == null) {
			throw new ProfileNotFoundException("Kein Profil mit entsprechender ProfileId (" + id + ") gefunden.");
		} else if (dbProfile.isUnSync() == true) {
			throw new ProfileSetOnDeletionException("Profil (" + id + ") ist zum löschen freigegeben.");
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
	 * 
	 * Der Wert lastProfileContactTimestamp wird in der Datenbank in allen Fällen
	 * angepasst.
	 * 
	 * @param id
	 *            ProfileId, nach welcher in der Datenbank gesucht werden soll.
	 * @param clientLastProfileChange
	 *            Entspricht der Aktualität des Profils auf dem Clientgerät.
	 * @return Gefundenes, aktuelleres Datenbankprofil.
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 * @throws ProfileSetOnDeletionException
	 *             Profil zum Löschen auf unSync gesetzt.
	 * @throws ServerProfileOutdatedException
	 *             Profil in DB weist einen älteren Zeitpunkt
	 *             lastProfileChangeTimestamp auf als der Parameter.
	 */
	public Profile getProfileByIdComparingLastChange(String id, Date clientLastProfileChange)
			throws ProfileNotFoundException, ProfileSetOnDeletionException, ServerProfileOutdatedException {
		Profile dbProfile = getProfileById(id);
		GregorianCalendar dbLastProfileChangeTimestamp = new GregorianCalendar();
		dbLastProfileChangeTimestamp.setTime(dbProfile.getLastProfileChange());
		dbLastProfileChangeTimestamp.set(Calendar.MINUTE, dbLastProfileChangeTimestamp.get(Calendar.MINUTE) - 5);
		if (dbLastProfileChangeTimestamp.getTime().after(clientLastProfileChange)) {
			return dbProfile;
		} else {
			throw new ServerProfileOutdatedException("Profil (" + id + ") in DB älter als Clientprofil");
		}
	}

	/**
	 * Liefert eine Liste aller in der Datenbank vorhandenen Profilen, absteigend
	 * nach der ProfileId sortiert, zurück. Dabei werden die Profile unabhängig
	 * ihrer gesetzten Eigenschaften zurückgeliefert.
	 * 
	 * Bei allen gefundenen Profilen wird die Eigenschaft
	 * lastProfileContactTimestamp aktualisiert.
	 * 
	 * @return Liste mit allen Profilen.
	 * @throws NoProfilesInDatabaseException
	 *             Datenbank ist leer.
	 */
	public Iterable<Profile> getAllProfiles() throws NoProfilesInDatabaseException {
		Iterable<Profile> list = profileRepository.findAllByOrderByIdAsc();
		if (list.iterator().hasNext() == false) {
			throw new NoProfilesInDatabaseException("Keine Profile in der DB vorhanden.");
		}
		updateProfiles(list);
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
	 *            ProfileId, nach welcher in der Datenbank gesucht werden soll.
	 * @return lastProfileChange des entsprechenden Profils.
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 */
	public Date getLastProfileChange(String id) throws ProfileNotFoundException {
		Profile dbProfile = profileRepository.findOne(id);
		if (dbProfile == null) {
			throw new ProfileNotFoundException("Kein Profil mit entsprechender ProfileId (" + id + ") gefunden.");
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
	 *            ProfileId, nach welcher in der Datenbank gesucht werden soll.
	 * @return lastProfileContact des entsprechenden Profils.
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 */
	public Date getLastProfileContact(String id) throws ProfileNotFoundException {
		Profile dbProfile = profileRepository.findOne(id);
		if (dbProfile == null) {
			throw new ProfileNotFoundException("Kein Profil mit entsprechender ProfileId (" + id + ") gefunden.");
		}
		Date dbProfileLastProfileContact = dbProfile.getLastProfileContact();
		return dbProfileLastProfileContact;
	}

	/**
	 * Liefert die Preferences des entsprechenden Profils zurück. Die Eigenschaft
	 * lastProfileContact wird bei diesem Zugriff nicht verändert.
	 * 
	 * @param id
	 *            ProfileId, nach welcher in der Datenbank gesucht werden soll.
	 * @return Preferences des entsprechenden Profils.
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 * @throws ProfileSetOnDeletionException
	 *             Profil zum Löschen auf unSync gesetzt.
	 */
	public String getPreferences(String id) throws ProfileNotFoundException, ProfileSetOnDeletionException {
		Profile dbProfile = profileRepository.findOne(id);
		if (dbProfile == null) {
			throw new ProfileNotFoundException("Kein Profil mit entsprechender ProfileId (" + id + ") gefunden.");
		} else if (dbProfile.isUnSync() == true) {
			throw new ProfileSetOnDeletionException("Profil (" + id + ") ist zum löschen freigegeben.");
		}
		String dbProfilePreferences = dbProfile.getPreferences();
		return dbProfilePreferences;
	}

	/**
	 * Liefert den Status des unSync-Flags des entsprechenden Profils zurück. Die
	 * Eigenschaft lastProfileContact wird bei diesem Zugriff nicht verändert.
	 * 
	 * @param id
	 *            ProfileId, nach welcher in der Datenbank gesucht werden soll.
	 * @return lastProfileChange des entsprechenden Profils.
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 */
	public boolean isUnSync(String id) throws ProfileNotFoundException {
		Profile dbProfile = profileRepository.findOne(id);
		if (dbProfile == null) {
			throw new ProfileNotFoundException("Kein Profil mit entsprechender ProfileId (" + id + ") gefunden.");
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
	 * @param id
	 *            ProfileId, nach welcher in der Datenbank gesucht werden soll.
	 * @param clientLastProfileChange
	 *            Letzte Änderungszeitpunkt des Profils auf Clienseite.
	 * @param clientPreferences
	 *            Die zu pushenden Präferenzen.
	 * @param overwrite
	 *            Soll ein bestehendes, aktuelleres Profil überschrieben werden?
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 * @throws ProfileSetOnDeletionException
	 *             DB-Profil zum Löschen auf unSync gesetzt.
	 * @throws ClientProfileOutdatedException
	 *             Profil in DB aktueller als Clientprofile.
	 */
	public void pushProfile(String id, Date clientLastProfileChange, String clientPreferences, boolean overwrite)
			throws ProfileNotFoundException, ProfileSetOnDeletionException, ClientProfileOutdatedException {
		// Abrufen des entsprechenden Profils aus der Datenbank, wirft eventuell
		// Exceptions
		Profile dbProfile = getProfileById(id);
		{
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
					new ClientProfileOutdatedException("Profil (" + id + ") in DB aktueller als Clientprofile.");
				}
			}
		}
	}

	/**
	 * Fügt ein Profil in die Datenbank ein. Bereits bestehende Profile mit
	 * identischer ProfileId werden überschrieben.
	 * 
	 * Der Zeitpunkt lastProfileContact wird in allen Fällen aktualisiert.
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
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 * @throws ProfileSetOnDeletionException
	 *             Profile bereits zum Löschen freigegeben.
	 */
	public void setProfileOnDeletion(String id) throws ProfileNotFoundException, ProfileSetOnDeletionException {
		// throws ProfileNotFoundException und ProfileSetOnDeletionException
		Profile dbProfile = getProfileById(id);

		// Falls Profil noch nicht benutzt, setze letzten Änderungszeitpunkt zunächst
		// auf aktuellen Zeitpunkt.
		if (dbProfile.getLastProfileChange().getTime() == 0L) {
			dbProfile.setLastProfileChange(new Date());
		}

		// Bestimmung des aktuellen Zeitpunktes plus 100 Jahre.
		Calendar lastProfileChange = GregorianCalendar.getInstance(Locale.GERMANY);
		lastProfileChange.setTime(dbProfile.getLastProfileChange());
		lastProfileChange.set(Calendar.YEAR, lastProfileChange.get(Calendar.YEAR) + 100);

		// Setzen der Eigenschaften lastProfileChange + 100 Jahre und unSync = true;
		// Löschen der Nutzerpräferenzen
		if (dbProfile.getLastProfileChange().getTime() == 0L) {
			dbProfile.setLastProfileChange(lastProfileChange.getTime());
		}
		dbProfile.setLastProfileChange(lastProfileChange.getTime());
		dbProfile.setUnSync(true);
		dbProfile.setpreferences(null);

		// Überschreiben des zu löschenden Profils in der Datenbank
		updateProfile(dbProfile);
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
	 * @throws ProfileNotFoundException
	 *             Kein Profil mit entsprechender ID gefunden.
	 * @throws ProfileSetOnDeletionException
	 *             Profile bereits zum Löschen freigegeben.
	 */
	public void setProfileOnDeletion(Profile profile) throws ProfileNotFoundException, ProfileSetOnDeletionException {
		setProfileOnDeletion(profile.getId());
	}

	/**
	 * Speichern von Profilen ohne Anpassen des lastProfileContact Timestamps. Diese
	 * Methode ist eher zu Testzwecken gedacht und sollte in der finalen Anwendung
	 * nicht mehr genutzt werden.
	 * 
	 * @param p
	 *            Zu speicherndes Profil.
	 * 
	 * @deprecated Nur für Testzwecke gedacht!
	 */
	public void save(Profile p) {
		profileRepository.save(p);
	}

}
