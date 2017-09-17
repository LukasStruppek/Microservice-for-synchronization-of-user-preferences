package de.privacy_avare.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;

/**
 * Die Klasse repräsentiert die Profildaten in der Datenbank. Diese beinhalten
 * die Eigenschaften id, lastProfileChange, lastProfileContact, preferences und
 * ein unSync-Flag.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

@Document
public class Profile {
	/**
	 * Entspricht der ProfileID, mit welcher ein Profil eindeutig identifiziert
	 * werden kann. Wird auch zur Identifikation der Dokumente innerhalb der
	 * Datenbank verwendet.
	 */
	@Id
	@Field
	private String id;

	/**
	 * Beinhaltet den Zeitpunkt, zu welchem das Profil zuletzt geändert wurde. Eine
	 * Änderung besteht bei Erzeugung des Profils bzw. der Anpassung der
	 * Nutzerpräferenzen.
	 */
	@Field
	private Date lastProfileChange;

	/**
	 * Beinhaltet den Zeitpunkt, zu welchem das Profil zuletzt in der Datenbank
	 * abgerufen bzw. bearbeitet wurde. Hierzu zählen u.a. die Operationen Lesen,
	 * Schreiben und Erzeugen eines Profils.
	 */
	@Field
	private Date lastProfileContact;

	/**
	 * Repräsentiert die Nutzerpräferenzen. !!!Zur Zeit noch als Platzhalter.!!!
	 */
	@Field
	private Object preferences;

	/**
	 * Flag, das zum Löschen eines Profils auf true gesetzt wird. Ist bei Erzeugung
	 * zunächst auf false gesetzt. Bei Löschanfragen wird das Flag auf true gesetzt
	 * und führt bei Datenbanksäuberungen zum löschen des Profils.
	 */
	@Field
	private boolean unSync;

	/**
	 * Default-Konstruktor ohne Setzen der ID. Setzt lastProfileChange auf den 1.
	 * Jan. 1970 und lastProfileContact auf den aktuellen Zeitpunkt. Restliche Werte
	 * werden mit Default-Werten belegt.
	 */
	public Profile() {
		// Setze lastProfileChange auf 1. Jan. 1970
		this.lastProfileChange = new Date(0L);
		// Setze lastProfileContact auf aktuellen Zeitpunkt
		this.lastProfileContact = GregorianCalendar.getInstance(Locale.GERMANY).getTime();

		this.preferences = "Keine Präferenzen festgelegt";
		this.unSync = false;
	}

	/**
	 * Erzeugt ein neues Profile-Objekt mit Id. Setzt lastProfileChange auf den 1.
	 * Jan. 1970 und lastProfileContact auf den aktuellen Zeitpunkt. Restliche Werte
	 * werden mit Default-Werten belegt.
	 * 
	 * @param id
	 *            ProfileID, mit welcher ein neues Profil erzeugt werden soll.
	 */
	public Profile(String id) {
		this.id = id;
		// Setze lastProfileChange auf 1. Jan. 1970
		this.lastProfileChange = new Date(0L);
		// Setze lastProfileContact auf aktuellen Zeitpunkt
		this.lastProfileContact = GregorianCalendar.getInstance(Locale.GERMANY).getTime();
		this.preferences = "Keine Präferenzen festgelegt";
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
	public Profile(String id, Date lastProfileChange, Date lastProfileContact, Object preferences) {
		this.id = id;
		this.lastProfileChange = lastProfileChange;
		this.lastProfileContact = lastProfileContact;
		this.preferences = preferences;
		this.unSync = false;
	}

	/**
	 * Ruft die ProfileID des Profils ab.
	 * 
	 * @return Die ProfileID.
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
	public Date getLastProfileChange() {
		return lastProfileChange;
	}

	/**
	 * Setzt den letzten Änderungszeitpunkt im Profil.
	 * 
	 * @param lastProfileChange
	 *            Der zu setzende Änderungszeitpunkt.
	 */
	public void setLastProfileChange(Date lastProfileChange) {
		this.lastProfileChange = lastProfileChange;
	}

	/**
	 * Ruft den letzten Kontaktzeitpunkt des Profils ab.
	 * 
	 * @return Der letzte Kontaktzeitpunkt.
	 */
	public Date getLastProfileContact() {
		return lastProfileContact;
	}

	/**
	 * Setzt den letzten Kontaktzeitpunkt im Profil.
	 * 
	 * @param lastProfileContact
	 *            Der zu setzende Kontaktzeitpunkt.
	 */
	public void setLastProfileContact(Date lastProfileContact) {
		this.lastProfileContact = lastProfileContact;
	}

	/**
	 * Ruft die Preferences des Profils ab.
	 * 
	 * @return Die Profileinstellungen
	 */
	public Object getPreferences() {
		return preferences;
	}

	/**
	 * Setzt die Preferences im Profil.
	 * 
	 * @param preferences
	 *            Die zu setzenden Preferences.
	 */
	public void setpreferences(Object preferences) {
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

	/**
	 * Liefert eine Stringrepräsentation des aktuellen Zustand des Objekts zurück.
	 * 
	 * @return Aktueller Zustand des Objekts
	 */
	@Override
	public String toString() {
		GregorianCalendar localLastProfileChange = new GregorianCalendar();
		localLastProfileChange.setTime(this.lastProfileChange);
		GregorianCalendar localLastProfileContact = new GregorianCalendar();
		localLastProfileContact.setTime(this.lastProfileContact);

		String strLastProfileChange = "[" + localLastProfileChange.get(Calendar.DATE) + "."
				+ localLastProfileChange.get(Calendar.MONTH) + "." + localLastProfileChange.get(Calendar.YEAR) + " "
				+ localLastProfileChange.get(Calendar.HOUR_OF_DAY) + ":" + localLastProfileChange.get(Calendar.MINUTE)
				+ ":" + localLastProfileChange.get(Calendar.SECOND) + "]";
		String strLastProfileContact = "[" + localLastProfileContact.get(Calendar.DATE) + "."
				+ localLastProfileContact.get(Calendar.MONTH) + "." + localLastProfileContact.get(Calendar.YEAR) + " "
				+ localLastProfileContact.get(Calendar.HOUR_OF_DAY) + ":" + localLastProfileContact.get(Calendar.MINUTE)
				+ ":" + localLastProfileContact.get(Calendar.SECOND) + "]";

		String result = "{" + "Id: " + this.id + ", lastProfileChange: " + strLastProfileChange
				+ ", lastProfileContact: " + strLastProfileContact + ", unSync: " + this.unSync + "}";
		return result;
	}

	/**
	 * Generiert eine HashMap mit den Eigenschaften und ihren entsprechenden
	 * Zuständen eines PRofile-Objekts zurück.
	 * 
	 * @return Generierte HashMap mit allen aktuellen Zuständen des Objekts.
	 */
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", this.id);
		map.put("lastProfileChange", this.lastProfileChange);
		map.put("lastProfileContact", this.lastProfileContact);
		map.put("preferences", this.preferences);
		map.put("unsync", this.unSync);

		return map;
	}
}
