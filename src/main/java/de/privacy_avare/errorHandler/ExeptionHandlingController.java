package de.privacy_avare.errorHandler;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import de.privacy_avare.domain.ErrorInformation;
import de.privacy_avare.exeption.ClientProfileOutdatedException;
import de.privacy_avare.exeption.MalformedProfileIdException;
import de.privacy_avare.exeption.ProfileAlreadyExistsException;
import de.privacy_avare.exeption.ProfileNotFoundException;
import de.privacy_avare.exeption.ProfileSetOnDeletionException;

@ControllerAdvice
public class ExeptionHandlingController {

	@ExceptionHandler(ProfileNotFoundException.class)
	public ResponseEntity<?> handleProfileNotFoundException(ProfileNotFoundException pnfe, HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("Profile Not Found");
		errorInformation.setException(pnfe.getClass().getName());
		errorInformation.setStatus(HttpStatus.NOT_FOUND.value());
		errorInformation.setDetail(pnfe.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.NOT_FOUND);
		return responseEntity;
	}

	@ExceptionHandler(ClientProfileOutdatedException.class)
	public ResponseEntity<?> handleClientProfileOutdatedException(ClientProfileOutdatedException cpoe,
			HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("Client Profile Outdated");
		errorInformation.setException(cpoe.getClass().getName());
		errorInformation.setStatus(HttpStatus.NOT_FOUND.value());
		errorInformation.setDetail(cpoe.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.NOT_FOUND);
		return responseEntity;
	}

	@ExceptionHandler(MalformedProfileIdException.class)
	public ResponseEntity<?> handleMalformedProfileIdException(MalformedProfileIdException mpide,
			HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("No Valid Profile ID");
		errorInformation.setException(mpide.getClass().getName());
		errorInformation.setStatus(HttpStatus.NOT_FOUND.value());
		errorInformation.setDetail(mpide.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.NOT_FOUND);
		return responseEntity;
	}

	@ExceptionHandler(ProfileAlreadyExistsException.class)
	public ResponseEntity<?> handleProfileAlreadyExistsException(ProfileAlreadyExistsException paee,
			HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("Profile Already Exists");
		errorInformation.setException(paee.getClass().getName());
		errorInformation.setStatus(HttpStatus.NOT_FOUND.value());
		errorInformation.setDetail(paee.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.NOT_FOUND);
		return responseEntity;
	}

	@ExceptionHandler(ProfileSetOnDeletionException.class)
	public ResponseEntity<?> handleProfileSetOnDeletionException(ProfileSetOnDeletionException psode,
			HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("Profile Set On Deletion");
		errorInformation.setException(psode.getClass().getName());
		errorInformation.setStatus(HttpStatus.NOT_FOUND.value());
		errorInformation.setDetail(psode.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.NOT_FOUND);
		return responseEntity;
	}

}
