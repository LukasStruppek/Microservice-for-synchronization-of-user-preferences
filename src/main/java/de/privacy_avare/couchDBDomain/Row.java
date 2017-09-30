package de.privacy_avare.couchDBDomain;

/**
 * Klasse dient als Bestandteil der Domain AllProfiles. Sie existiert als
 * separate Klasse, da eine automatische JSON-Konvertierung nicht mit internen
 * bzw. anonymen Klassen kompatibel ist.
 * 
 * Rows repräsentieren bei der Abfrage aller Dokumente aus einer
 * CouchDB-Datenbank die einzelnen Dokumente bzw. deren Id.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */
public class Row {
	/**
	 * Repräsentiert die _id eines Dokuments aus einer CouchDB-Datenbank..
	 */
	private String id;

	/**
	 * default-Konstruktor ohne zusätzliche Funktionalität.
	 */
	public Row() {

	}

	/**
	 * Ruft die Eigenschaft _id des Profils ab.
	 * 
	 * @return Die ProfileID.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Setzt die _id des Profils.
	 * 
	 * @param id
	 *            Die zu setzende _id.
	 */
	public void setId(String id) {
		this.id = id;
	}
}