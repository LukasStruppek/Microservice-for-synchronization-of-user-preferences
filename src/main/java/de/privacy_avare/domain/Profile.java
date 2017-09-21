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
 * die Eigenschaften id, lastProfileChange, lastProfileContact und preferences.
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
	String _id;

	/**
	 * Beinhaltet den Zeitpunkt, zu welchem das Profil zuletzt geändert wurde. Eine
	 * Änderung besteht bei Erzeugung des Profils bzw. der Anpassung der
	 * Nutzerpräferenzen.
	 */
	@Field
	Date lastProfileChange;

	/**
	 * Beinhaltet den Zeitpunkt, zu welchem das Profil zuletzt in der Datenbank
	 * abgerufen bzw. bearbeitet wurde. Hierzu zählen u.a. die Operationen Lesen,
	 * Schreiben und Erzeugen eines Profils.
	 */
	@Field
	Date lastProfileContact;

	/**
	 * Repräsentiert die Nutzerpräferenzen.
	 */
	@Field
	String preferences;

	public Profile() {
		// Setze lastProfileChange auf 1. Jan. 1970
		this.lastProfileChange = new Date(0L);
		// Setze lastProfileContact auf aktuellen Zeitpunkt
		this.lastProfileContact = GregorianCalendar.getInstance(Locale.GERMANY).getTime();
		this.preferences = "";
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
		this._id = id;
		// Setze lastProfileChange auf 1. Jan. 1970
		this.lastProfileChange = new Date(0L);
		// Setze lastProfileContact auf aktuellen Zeitpunkt
		this.lastProfileContact = GregorianCalendar.getInstance(Locale.GERMANY).getTime();
		this.preferences = "";
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
	public Profile(String id, Date lastProfileChange, Date lastProfileContact, String preferences) {
		this._id = id;
		this.lastProfileChange = lastProfileChange;
		this.lastProfileContact = lastProfileContact;
		this.preferences = preferences;
	}

	/**
	 * Ruft die ProfileID des Profils ab.
	 * 
	 * @return Die ProfileID.
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * Setzt die ProfileID im Profil.
	 * 
	 * @param id
	 *            Die zu setzende ProfileID.
	 */
	public void set_id(String id) {
		this._id = id;
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
	public String getPreferences() {
		return preferences;
	}

	public void setPreferences(String preferences) {
		this.preferences = preferences;
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

		String result = "{" + "id: " + this._id + ", lastProfileChange: " + strLastProfileChange
				+ ", lastProfileContact: " + strLastProfileContact + "}";
		return result;
	}

	/**
	 * Generiert eine HashMap mit den Eigenschaften und ihren entsprechenden
	 * Zuständen eines Profile-Objekts zurück.
	 * 
	 * @return Generierte HashMap mit allen aktuellen Zuständen des Objekts.
	 */
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", this._id);
		map.put("lastProfileChange", this.lastProfileChange);
		map.put("lastProfileContact", this.lastProfileContact);
		map.put("preferences", this.preferences);

		return map;
	}
}
