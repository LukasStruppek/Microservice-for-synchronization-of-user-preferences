package de.privacy_avare.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Tritt auf, wenn beim Abruf eines Profiles aus der DB kein Profil mit
 * entsprechender ProfileId vorhanden ist.
 * 
 * Der HTTP-Statuscode entspricht 404 Not Found.
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see RuntimeException
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProfileNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Default-Konstruktor, welcher den entsprechenden parameterlosen
	 * Superkonstruktor von Runtime aufruft.
	 */
	public ProfileNotFoundException() {
		super();
	}

	/**
	 * Ruft den entsprechenden Superkonstruktor von RutimeException auf.
	 * 
	 * @param message
	 *            Beschreibung des Fehlers.
	 */
	public ProfileNotFoundException(String message) {
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
	public ProfileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
