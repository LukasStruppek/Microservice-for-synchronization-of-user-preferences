package de.privacy_avare.exeption;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Tritt auf, wenn beim Erstellen eines neuen Profils eine ProfileId verwendet
 * wird, welche bereits einem existierenden Profil zugeordnet ist.
 * 
 * Der HTTP-Statuscode entspricht 409 Conflict.
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see RuntimeException
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class ProfileAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Default-Konstruktor, welcher den entsprechenden parameterlosen
	 * Superkonstruktor von Runtime aufruft.
	 */
	public ProfileAlreadyExistsException() {

	}

	/**
	 * Ruft den entsprechenden Superkonstruktor von RutimeException auf.
	 * 
	 * @param message
	 *            Beschreibung des Fehlers.
	 */
	public ProfileAlreadyExistsException(String message) {
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
	public ProfileAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
}
