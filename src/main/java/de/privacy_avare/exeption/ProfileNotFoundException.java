package de.privacy_avare.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author Lukas Struppek
 * @version 1.0
 *
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProfileNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public ProfileNotFoundException() {
		
	}
	
	public ProfileNotFoundException(String message) {
		super(message);
	}
	
	public ProfileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
