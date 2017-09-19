package de.privacy_avare.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Tritt auf, wenn beim Push-Versuch der Preferences des Client diese hinsichtlich des
 * Zeitpunktes lastProfileChange veraltet sind im Vergleich mit dem DB-Profil und ein
 * Überschreiben des DB-Profils ausgeschlossen wurde.
 * 
 * Der HTTP-Statuscode entspricht 409 Conflict.
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see RuntimeException
 *
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class ClientPreferencesOutdatedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Default-Konstruktor, welcher den entsprechenden parameterlosen
	 * Superkonstruktor von Runtime aufruft.
	 */
	public ClientPreferencesOutdatedException() {
		super();
	}

	/**
	 * Ruft den Superkonstruktor von RutimeException auf.
	 * @param message Beschreibung des Fehlers.
	 */
	public ClientPreferencesOutdatedException(String message) {
		super(message);
	}

	/**
	 * Ruft den Superkonstruktor von RutimeException.
	 * @param message Beschreibung des Fehlers.
	 * @param cause Grund des Fehlerauftritts.
	 */
	public ClientPreferencesOutdatedException(String message, Throwable cause) {
		super(message, cause);
	}
}
