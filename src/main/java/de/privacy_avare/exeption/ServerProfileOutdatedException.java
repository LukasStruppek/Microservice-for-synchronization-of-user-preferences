package de.privacy_avare.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Tritt auf, wenn beim Pull-Versuch der Zeitstempel lastProfileContact des
 * DB-Profils älter ist als der des ClientProfiles.
 * 
 * Der HTTP-Statuscode entspricht 409 Conflict.
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see RuntimeException
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class ServerProfileOutdatedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Default-Konstruktor, welcher den entsprechenden parameterlosen
	 * Superkonstruktor von Runtime aufruft.
	 */
	public ServerProfileOutdatedException() {
		super();
	}

	/**
	 * Ruft den entsprechenden Superkonstruktor von RutimeException auf.
	 * 
	 * @param message
	 *            Beschreibung des Fehlers.
	 */
	public ServerProfileOutdatedException(String message) {
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
	public ServerProfileOutdatedException(String message, Throwable cause) {
		super(message, cause);
	}
}
