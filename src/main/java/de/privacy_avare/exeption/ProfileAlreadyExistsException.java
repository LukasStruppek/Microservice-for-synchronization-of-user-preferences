package de.privacy_avare.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProfileAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ProfileAlreadyExistsException() {
		
	}

	public ProfileAlreadyExistsException(String message) {
		super(message);
	}

	public ProfileAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
}
