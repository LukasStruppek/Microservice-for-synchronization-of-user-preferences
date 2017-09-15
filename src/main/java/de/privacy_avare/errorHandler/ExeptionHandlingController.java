package de.privacy_avare.errorHandler;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import de.privacy_avare.dto.ErrorInformation;
import de.privacy_avare.exeption.ClientProfileOutdatedException;
import de.privacy_avare.exeption.MalformedProfileIdException;
import de.privacy_avare.exeption.ProfileAlreadyExistsException;
import de.privacy_avare.exeption.ProfileNotFoundException;
import de.privacy_avare.exeption.ProfileSetOnDeletionException;
import de.privacy_avare.exeption.ServerProfileOutdatedException;

/**
 * 
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

@ControllerAdvice
public class ExeptionHandlingController {

	@ExceptionHandler(value = ProfileNotFoundException.class)
	public ResponseEntity<?> handleProfileNotFoundException(ProfileNotFoundException pnfe, HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("Profil nicht gefunden");
		errorInformation.setException(pnfe.getClass().getName());
		errorInformation.setStatus(HttpStatus.NOT_FOUND.value());
		errorInformation.setDetail(pnfe.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.NOT_FOUND);
		return responseEntity;
	}

	@ExceptionHandler(value = ClientProfileOutdatedException.class)
	public ResponseEntity<?> handleClientProfileOutdatedException(ClientProfileOutdatedException cpoe,
			HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("Client Profil veraltet");
		errorInformation.setException(cpoe.getClass().getName());
		errorInformation.setStatus(HttpStatus.CONFLICT.value());
		errorInformation.setDetail(cpoe.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.CONFLICT);
		return responseEntity;
	}
	
	@ExceptionHandler(value = ServerProfileOutdatedException.class)
	public ResponseEntity<?> handleServerProfileOutdatedException(ServerProfileOutdatedException spoe,
			HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("Server Profil veraltet");
		errorInformation.setException(spoe.getClass().getName());
		errorInformation.setStatus(HttpStatus.CONFLICT.value());
		errorInformation.setDetail(spoe.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.CONFLICT);
		return responseEntity;
	}

	@ExceptionHandler(value = MalformedProfileIdException.class)
	public ResponseEntity<?> handleMalformedProfileIdException(MalformedProfileIdException mpide,
			HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("Ungültige ProfilId");
		errorInformation.setException(mpide.getClass().getName());
		errorInformation.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
		errorInformation.setDetail(mpide.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.UNPROCESSABLE_ENTITY);
		return responseEntity;
	}

	@ExceptionHandler(value = ProfileAlreadyExistsException.class)
	public ResponseEntity<?> handleProfileAlreadyExistsException(ProfileAlreadyExistsException paee,
			HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("Profil bereits vorhanden");
		errorInformation.setException(paee.getClass().getName());
		errorInformation.setStatus(HttpStatus.CONFLICT.value());
		errorInformation.setDetail(paee.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.CONFLICT);
		return responseEntity;
	}

	@ExceptionHandler(value = ProfileSetOnDeletionException.class)
	public ResponseEntity<?> handleProfileSetOnDeletionException(ProfileSetOnDeletionException psode,
			HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("Profil zum Löschen freigegeben");
		errorInformation.setException(psode.getClass().getName());
		errorInformation.setStatus(HttpStatus.GONE.value());
		errorInformation.setDetail(psode.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.GONE);
		return responseEntity;
	}
	
	@ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException htmnse,
			HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("HTTP-Methode nicht unterstützt");
		errorInformation.setException(htmnse.getClass().getName());
		errorInformation.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
		errorInformation.setDetail(htmnse.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.METHOD_NOT_ALLOWED);
		return responseEntity;
	}
	
	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException matme,
			HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("Falscher Datentyp im Parameter");
		errorInformation.setException(matme.getClass().getName());
		errorInformation.setStatus(HttpStatus.BAD_REQUEST.value());
		errorInformation.setDetail(matme.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.BAD_REQUEST);
		return responseEntity;
	}
	
	
	@ExceptionHandler(value = MappingException.class)
	public ResponseEntity<?> handleMappingException(MappingException me,
			HttpServletRequest request) {
		ErrorInformation errorInformation = new ErrorInformation();
		errorInformation.setTitle("Daten entsprechen nicht denen der Profile-Klasse -> Konvertierungsfehler");
		errorInformation.setException(me.getClass().getName());
		errorInformation.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorInformation.setDetail(me.getMessage());
		errorInformation.setRequestedURI(request.getRequestURI());
		errorInformation.setTimestamp(new Date());
		errorInformation.setAdditionalInformation("");
		ResponseEntity<?> responseEntity = new ResponseEntity<>(errorInformation, null, HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}
}
