package de.privacy_avare.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ClientProfileOutdatedException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public ClientProfileOutdatedException() {
		
	}

	public ClientProfileOutdatedException(String message) {
		super(message);
	}

	public ClientProfileOutdatedException(String message, Throwable cause) {
		super(message, cause);
	}
}
