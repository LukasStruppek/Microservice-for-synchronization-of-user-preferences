package de.privacy_avare.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasse dient der problembasierten Darstellung von auftretetenden Exceptions.
 * Der Aufbau der Klasse orientierte sich dabei an der Definition von HTTP
 * Problem Details der UC Berkeley:
 * 
 * 
 * @author Lukas Struppek
 * @version 1.0 * @see <a href=
 *          "https://tools.ietf.org/html/draft-nottingham-http-problem-07">Problem
 *          Details for HTTP APIs draft-nottingham-http-problem-07 </a>
 *
 */

public class ErrorInformation {
	private String title;
	private String exception;
	private int status;
	private String detail;
	private String requestedURI;
	private String timestamp;
	private String additionalInformation;

	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,SSS");

	public ErrorInformation() {

	}

	public ErrorInformation(String title, String exception, int status, String detail, String requestedURI,
			String timestamp) {
		super();
		this.title = title;
		this.exception = exception;
		this.status = status;
		this.detail = detail;
		this.requestedURI = requestedURI;
		this.timestamp = timestamp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getRequestedURI() {
		return requestedURI;
	}

	public void setRequestedURI(String requestedURI) {
		this.requestedURI = requestedURI;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setTimestamp(Date d) {
		this.timestamp = this.format.format(d);
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

}
