package de.privacy_avare.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

@ResponseStatus(HttpStatus.GONE)
public class ProfileSetOnDeletionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ProfileSetOnDeletionException() {
		
	}

	public ProfileSetOnDeletionException(String message) {
		super(message);
	}

	public ProfileSetOnDeletionException(String message, Throwable cause) {
		super(message, cause);
	}
}
