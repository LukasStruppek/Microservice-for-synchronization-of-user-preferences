import java.util.Calendar;
import java.util.Date;
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
	private Date lastProfileChange;

	@Field
	private Date lastProfileContact;

	@Field
	private Preferences preferences; // Platzhalter für tatsächliche Datenrepresentation

	@Field
	private boolean unSync;

	/**
	 * Default-Konstruktor für die automatische Konvertierung von JSON in eine
	 * Instanz dieser Klasse.
	 */
	public Profile() {
		this.lastProfileChange = GregorianCalendar.getInstance(Locale.GERMANY).getTime();
		this.lastProfileContact = GregorianCalendar.getInstance(Locale.GERMANY).getTime();
		this.preferences = new Preferences();
		this.unSync = false;
	}

	/**
	 * Erzeugt ein neues Profile-Objekt, in welchem die Zeitpunkte lastProfileChange
	 * und lastProfileContact auf den Zeitpunkt der Erzeugung gesetzt werden.
	 */
	public Profile(String id) {
		this.id = id;
		this.lastProfileChange = GregorianCalendar.getInstance(Locale.GERMANY).getTime();
		this.lastProfileContact = GregorianCalendar.getInstance(Locale.GERMANY).getTime();
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
	public Profile(String id, Date lastProfileChange, Date lastProfileContact, Preferences preferences) {
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
				+ localLastProfileChange.get(Calendar.HOUR_OF_DAY) + ":" + localLastProfileChange.get(Calendar.MINUTE) + ":"
				+ localLastProfileChange.get(Calendar.SECOND) + "]";
		String strLastProfileContact = "[" + localLastProfileContact.get(Calendar.DATE) + "."
				+ localLastProfileContact.get(Calendar.MONTH) + "." + localLastProfileContact.get(Calendar.YEAR) + " "
				+ localLastProfileContact.get(Calendar.HOUR_OF_DAY) + ":" + localLastProfileContact.get(Calendar.MINUTE) + ":"
				+ localLastProfileContact.get(Calendar.SECOND) + "]";

		String result = "{" + "Id: " + this.id + ", lastProfileChange: " + strLastProfileChange
				+ ", lastProfileContact: " + strLastProfileContact + ", unSync: " + this.unSync + "}";
		return result;
	}
}
