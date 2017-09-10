package de.privacy_avare.domain;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;

/**
 * Die Klasse representiert die Userdaten. Diese beinhalten die Eigenschaften
 * ProfileId, lastProfileChange, lastProfileContact, preferences und ein
 * unSync-Flag.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

@Document
public class Profile {
	@Id
	@Field
	private String id;

	@Field
	private Calendar lastProfileChange;

	@Field
	private Calendar lastProfileContact;

	@Field
	private Preferences preferences; // Platzhalter für tatsächliche Datenrepresentation

	@Field
	private boolean unSync;

	/**
	 * Default-Konstruktor für die automatische Konvertierung von JSON in eine
	 * Instanz dieser Klasse.
	 */
	public Profile() {
	}

	/**
	 * Erzeugt ein neues Profile-Objekt, in welchem die Zeitpunkte lastProfileChange
	 * und lastProfileContact auf den Zeitpunkt der Erzeugung gesetzt werden.
	 */
	public Profile(String id) {
		this.id = id;
		this.lastProfileChange = GregorianCalendar.getInstance(Locale.GERMANY);
		this.lastProfileContact = GregorianCalendar.getInstance(Locale.GERMANY);
		this.preferences = new Preferences();
		this.unSync = false;
	}

	/**
	 * Erzeugt ein neues Profile-Objekt und setzt die Instanzvariablen entsprechend
	 * den übergebenen Parametern.
	 * 
	 * @param id
	 *            Die zu setzende ProfileId
	 * @param lastProfileChange
	 *            Der zu setzende Änderungszeitpunkt.
	 * @param lastProfileContact
	 *            Der zu setzende Kontaktzeitpunkt.
	 * @param preferences
	 *            Die zu setzenden Preferences.
	 */
	public Profile(String id, Calendar lastProfileChange, Calendar lastProfileContact, Preferences preferences) {
		this.id = id;
		this.lastProfileChange = lastProfileChange;
		this.lastProfileContact = lastProfileContact;
		this.preferences = preferences;
		this.unSync = false;
	}

	/**
	 * Ruft die ProfileID des Profils ab.
	 * 
	 * @ return Die ProfileID.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Setzt die ProfileID im Profil.
	 * 
	 * @param id
	 *            Die zu setzende ProfileID.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Ruft den letzten Änderungszeitpunkt des Profils ab.
	 * 
	 * @return Der letzte Änderungszeitpunkt.
	 */
	public Calendar getLastProfileChange() {
		return lastProfileChange;
	}

	/**
	 * Setzt den letzten Änderungszeitpunkt im Profil.
	 * 
	 * @param lastProfileChange
	 *            Der zu setzende Änderungszeitpunkt.
	 */
	public void setLastProfileChange(Calendar lastProfileChange) {
		this.lastProfileChange = lastProfileChange;
	}

	/**
	 * Ruft den letzten Kontaktzeitpunkt des Profils ab.
	 * 
	 * @return Der letzte Kontaktzeitpunkt.
	 */
	public Calendar getLastProfileContact() {
		return lastProfileContact;
	}

	/**
	 * Setzt den letzten Kontaktzeitpunkt im Profil.
	 * 
	 * @param lastProfileContact
	 *            Der zu setzende Kontaktzeitpunkt.
	 */
	public void setLastProfileContact(Calendar lastProfileContact) {
		this.lastProfileContact = lastProfileContact;
	}

	/**
	 * Ruft die Preferences des Profils ab.
	 * 
	 * @return Die Profileinstellungen
	 */
	public Preferences getPreferences() {
		return preferences;
	}

	/**
	 * Setzt die Preferences im Profil.
	 * 
	 * @param profileData
	 *            Die zu setzenden Preferences.
	 */
	public void setpreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	/**
	 * Ruft den Zustand des unSync-Flags im Profil ab.
	 * 
	 * @return Zustand des unSync-Flags.
	 */
	public boolean isUnSync() {
		return unSync;
	}

	/**
	 * Setzt das unSync-Flag im Profil.
	 * 
	 * @param unSync
	 *            Zustand des unSync-Flags.
	 */
	public void setUnSync(boolean unSync) {
		this.unSync = unSync;
	}

}
