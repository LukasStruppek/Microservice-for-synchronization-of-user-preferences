package de.privacy_avare.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Tritt auf, wenn beim Abruf aller Profile der DB in dieser noch keine Profile
 * abgelegt worden sind bzw. die Datenbank leer ist.
 * 
 * Der HTTP-Statuscode entspricht 409 Conflict.
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see RuntimeException
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class NoProfilesInDatabaseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Default-Konstruktor, welcher den entsprechenden parameterlosen
	 * Superkonstruktor von Runtime aufruft.
	 */
	public NoProfilesInDatabaseException() {
		super();
	}

	/**
	 * Ruft den entsprechenden Superkonstruktor von RutimeException auf.
	 * 
	 * @param message
	 *            Beschreibung des Fehlers.
	 */
	public NoProfilesInDatabaseException(String message) {
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
	public NoProfilesInDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}
}
