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
	/**
	 * Titel der Fehlermeldung.
	 */
	private String title;

	/**
	 * Art des Fehlers.
	 */
	private String exception;

	/**
	 * HTTP Statuscode
	 */
	private int status;

	/**
	 * Beschreibung der Art des Fehlers. In der Regel die Nachricht der Exception.
	 */
	private String detail;

	/**
	 * Aufruf des Clients, welcher den Fehler ausgelöst hast.
	 */
	private String requestedURI;

	/**
	 * Zeitpunkt, zu welchem der Fehler aufgetreten ist. Format orientiert sich am
	 * Übertragungsformat für Zeitpunkte, wie es auch in anderen Klassen der
	 * Anwendung genutzt wird.
	 */
	private String timestamp;

	/**
	 * Bietet die Möglichkeit, zusätzliche Informationen zu liefern. Standard ist
	 * ein leerer String.
	 */
	private String additionalInformation;

	/**
	 * Dient der Konvertierung von Zeitpunkten in Strings.
	 */
	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,SSS");

	/**
	 * Default-Konstruktor
	 */
	public ErrorInformation() {

	}

	/**
	 * Konstruktor, welcher alle Eigenschaften bereits mit Werten versieht.
	 * 
	 * @param title
	 *            Titel der Fehlermeldung.
	 * @param exception
	 *            Art des Fehlers.
	 * @param status
	 *            HTTP Statuscode
	 * @param detail
	 *            Detailliertere Fehlerbeschreibung
	 * @param requestedURI
	 *            URI-Aufruf, welcher die Fehlermeldung ausgelöst hast.
	 * @param timestamp
	 *            Zeitpunkt des Fehlerauftretens.
	 */
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

	/**
	 * Getter für den Titel der Fehlermeldung.
	 * 
	 * @return Titel der Fehlermeldung.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setter für den Titel der Fehlermeldung.
	 * 
	 * @param title
	 *            Titel der Fehlermeldung.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Getter für die Art der Fehlermeldung.
	 * 
	 * @return Art der Fehlermeldung
	 */
	public String getException() {
		return exception;
	}

	/**
	 * Setter für die Art der Fehlermeldung.
	 * 
	 * @param exception
	 *            Art der Fehlermeldung.
	 */
	public void setException(String exception) {
		this.exception = exception;
	}

	/**
	 * Getter für den Statuscode.
	 * 
	 * @return HTTP-Statuscode.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Setter für den HTTP-Statuscode.
	 * 
	 * @param status
	 *            HTTP-Statuscode.
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Getter für die Fehlerdetails.
	 * 
	 * @return Fehlerdetails
	 */
	public String getDetail() {
		return detail;
	}

	/**
	 * Setter für die Fehlerdetails.
	 * 
	 * @param detail
	 *            Fehlerdetails.
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

	/**
	 * Getter für die aufgerufene URI.
	 * 
	 * @return Aufgerufene URI
	 */
	public String getRequestedURI() {
		return requestedURI;
	}

	/**
	 * Setter für die aufgerufene URI.
	 * 
	 * @param requestedURI
	 *            Aufgerufene URI.
	 */
	public void setRequestedURI(String requestedURI) {
		this.requestedURI = requestedURI;
	}

	/**
	 * Getter für den Zeitpunkt des Fehlers.
	 * 
	 * @return Zeitpunkt des Fehlers
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Setter für die Zeitpunkt des Fehlers im Stringformat.
	 * 
	 * @param timestamp
	 *            Zeitpunkt des Fehlers.
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Setter für die Zeitpunkt des Fehlers als Date-Objekt. Automatische
	 * Konvertierung in entsprechende String-Repräsentation.
	 * 
	 * @param d
	 *            Zeitpunkt des Fehlers.
	 */
	public void setTimestamp(Date d) {
		this.timestamp = this.format.format(d);
	}

	/**
	 * Getter für zusätzliche Informationen.
	 * 
	 * @return Zusätzliche Informationen.
	 */
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	/**
	 * Setter für zusätzliche Informationen.
	 * 
	 * @param additionalInformation Zusätzliche Informationen.
	 */
	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

}
