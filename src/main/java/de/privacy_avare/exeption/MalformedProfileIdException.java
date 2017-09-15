package de.privacy_avare.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class MalformedProfileIdException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MalformedProfileIdException() {
		
	}

	public MalformedProfileIdException(String message) {
		super(message);
	}

	public MalformedProfileIdException(String message, Throwable cause) {
		super(message, cause);
	}
}
