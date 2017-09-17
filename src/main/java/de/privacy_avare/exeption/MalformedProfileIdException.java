package de.privacy_avare.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Tritt auf, wenn bei der Erzeugung eines Profils mit einer vorgegebenen
 * ProfileId diese nicht der Form einer korrekten ProfileId entspricht.
 * 
 * Der HTTP-Statuscode entspricht 402 Unprocessable Entity.
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see RuntimeException
 * 
 */

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class MalformedProfileIdException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Default-Konstruktor, welcher den entsprechenden parameterlosen
	 * Superkonstruktor von Runtime aufruft.
	 */
	public MalformedProfileIdException() {
		super();
	}

	/**
	 * Ruft den entsprechenden Superkonstruktor von RutimeException auf.
	 * 
	 * @param message
	 *            Beschreibung des Fehlers.
	 */
	public MalformedProfileIdException(String message) {
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
	public MalformedProfileIdException(String message, Throwable cause) {
		super(message, cause);
	}
}
