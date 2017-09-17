package de.privacy_avare.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Tritt auf, wenn ein Profil abgerufen werden soll, aber bereits mittels unSync
 * = true zum Löschen freigegeben wurde. Weiterhin wird die Exception geworfen,
 * wenn versucht wird, ein bereits zum Löschen freigegebenes Profil erneut zu
 * löschen.
 * 
 * Der HTTP-Statuscode entspricht 410 Gone.
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see RuntimeException
 */

@ResponseStatus(HttpStatus.GONE)
public class ProfileSetOnDeletionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Default-Konstruktor, welcher den entsprechenden parameterlosen
	 * Superkonstruktor von Runtime aufruft.
	 */
	public ProfileSetOnDeletionException() {

	}

	/**
	 * Ruft den entsprechenden Superkonstruktor von RutimeException auf.
	 * 
	 * @param message
	 *            Beschreibung des Fehlers.
	 */
	public ProfileSetOnDeletionException(String message) {
		super(message);
	}

	/**
	 * Ruft den entsprechenden Superkonstruktor von RutimeException.
	 * 
	 * @param message
	 *            Beschreibung des Fehlers.
	 * @param cause
	 *            Grund des Fehlerauftritts.
	 */
	public ProfileSetOnDeletionException(String message, Throwable cause) {
		super(message, cause);
	}
}
