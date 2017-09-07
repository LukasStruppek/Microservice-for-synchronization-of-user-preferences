/**
 * @author Lukas Struppek
 * @version 1.0
 * 
 * Die Klasse representiert die Userdaten.
 * Diese beinhalten die Eigenschaften ProfileId, lastProfileChange,
 * lastProfileContact und profileData.
 */

package de.privacy_avare.domain;

import java.util.Calendar;

public class Profile {
	private String id;
	private Calendar lastProfileChange;
	private Calendar lastProfileContact;
	private Object profileData; // Platzhalter für tatsächliche Datenrepresentation

	/**
	 * Erzeugt ein neues Profile-Objekt, in welchem die Instanzvariablen mit default-Werten besetzt.
	 */
	public Profile() {
		id = null;
		lastProfileChange = null;
		lastProfileContact = null;
		profileData = null;
	}
	
	/**
	 * Erzeugt ein neues Profile-Objekt und setzt die Instanzvariablen entsprechend den übergebenen Parametern.
	 * @param id Die zu setzende ProfileId
	 * @param lastProfileChange Der zu setzende Änderungszeitpunkt.
	 * @param lastProfileContact Der zu setzende Kontaktzeitpunkt.
	 * @param profileData Die zu setzenden Profileinstellungen.
	 */
	@public Profile(String id, Calendar lastProfileChange, Calendar lastProfileContact, Object profileData){
		this.id = id;
		this.lastProfileChange = lastProfileChange;
		this.lastProfileContact = lastProfileContact;
		this.profileData = profileData;
	}
	
	/**
	 * Ruft die ProfileID, die im Data Transfer Object gespeichert ist, ab. @ return
	 * Die ProfileID.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Setzt die ProfileID im Data Transfer Object.
	 * 
	 * @param id
	 *            Die zu setzende ProfileID.
	 */
	public void setId(String id) {
		this.id = id;
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
	public Object getProfileData() {
		return profileData;
	}

	/**
	 * Setzt die Profileinstellungen im Data Transfer Object.
	 * 
	 * @param profileData
	 *            Die zu setzenden Profileinstellungen.
	 */
	public void setProfileData(Object profileData) {
		this.profileData = profileData;
	}
}
