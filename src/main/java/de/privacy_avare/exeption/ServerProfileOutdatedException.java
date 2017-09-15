package de.privacy_avare.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author Lukas Struppek
 * @version 1.0
 *
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class ServerProfileOutdatedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ServerProfileOutdatedException() {

	}

	public ServerProfileOutdatedException(String message) {
		super(message);
	}

	public ServerProfileOutdatedException(String message, Throwable cause) {
		super(message, cause);
	}
}
