package de.privacy_avare.domain;

import java.util.Calendar;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;

/**
 * Die Klasse representiert die Userdaten. Diese beinhalten die Eigenschaften
 * ProfileId, lastProfileChange, lastProfileContact und profileData.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

@Document
public class Profile {
	@Id
	private String profileId;

	@Field
	private Calendar lastProfileChange;

	@Field
	private Calendar lastProfileContact;

	@Field
	private Preferences preferences; // Platzhalter für tatsächliche Datenrepresentation

	/**
	 * Erzeugt ein neues Profile-Objekt, in welchem die Instanzvariablen mit
	 * default-Werten besetzt sind und durch eine ProfileId gekennzeichnet sind.
	 */
	public Profile(String profileId) {
		this.profileId = profileId;
		lastProfileChange = null;
		lastProfileContact = null;
		preferences = null;
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
	 * @param profileData
	 *            Die zu setzenden Profileinstellungen.
	 */
	public Profile(String id, Calendar lastProfileChange, Calendar lastProfileContact, Preferences profileData) {
		this.profileId = id;
		this.lastProfileChange = lastProfileChange;
		this.lastProfileContact = lastProfileContact;
		this.preferences = profileData;
	}

	/**
	 * Ruft die ProfileID, die im Data Transfer Object gespeichert ist, ab. @ return
	 * Die ProfileID.
	 */
	public String getProfileId() {
		return profileId;
	}

	/**
	 * Setzt die ProfileID im Data Transfer Object.
	 * 
	 * @param id
	 *            Die zu setzende ProfileID.
	 */
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	/**
	 * Ruft den letzten Änderungszeitpunkt, der im Data Transfer Object gespeichert
	 * ist, ab.
	 * 
	 * @return Der letzte Änderungszeitpunkt.
	 */
	public Calendar getLastProfileChange() {
		return lastProfileChange;
	}

	/**
	 * Setzt den letzten Änderungszeitpunkt im Data Transfer Object.
	 * 
	 * @param lastProfileChange
	 *            Der zu setzende Änderungszeitpunkt.
	 */
	public void setLastProfileChange(Calendar lastProfileChange) {
		this.lastProfileChange = lastProfileChange;
	}

	/**
	 * Ruft den letzten Kontaktzeitpunkt, der im Data Transfer Object gespeichert
	 * ist, ab.
	 * 
	 * @return Der letzte Kontaktzeitpunkt.
	 */
	public Calendar getLastProfileContact() {
		return lastProfileContact;
	}

	/**
	 * Setzt den letzten Kontaktzeitpunkt im Data Transfer Object.
	 * 
	 * @param lastProfileContact
	 *            Der zu setzende Kontaktzeitpunkt.
	 */
	public void setLastProfileContact(Calendar lastProfileContact) {
		this.lastProfileContact = lastProfileContact;
	}

	/**
	 * Ruft die Profileinstellungen, die im Data Transfer Object gespeichert sind,
	 * ab.
	 * 
	 * @return Die Profileinstellungen
	 */
	public Preferences getProfileData() {
		return preferences;
	}

	/**
	 * Setzt die Profileinstellungen im Data Transfer Object.
	 * 
	 * @param profileData
	 *            Die zu setzenden Profileinstellungen.
	 */
	public void setProfileData(Preferences profileData) {
		this.preferences = profileData;
	}
}
