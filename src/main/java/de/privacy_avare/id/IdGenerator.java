package de.privacy_avare.id;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/** 
 * Klasse stellt statische Methode zur Generierung einer 14-stelligen, eindeutigen UserID bereit.
 * Erzeugung von Instanzen der Klasse ist nicht möglich.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

public class IdGenerator {

	/**
	 * Privater Konstruktor, verhindert Erzeugung einer Instanz der Klasse
	 */
	private IdGenerator() {
	};

	/**
	 * Erzeugt unter Verwendung des aktuellen Datum, der aktuellen Uhrzeit und
	 * zufälligen Buchstaben eine eindeutige UserID.
	 * 
	 * @return generierte UserID
	 */
	public static String generateID() {
		Random random = new Random();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		StringBuffer id = new StringBuffer();
		final String lowerCase = "abcdefghijklmnopqrstuvwxyz";

		// Abruf des aktuellen Datums und der Uhrzeit in Zahlenform
		int day = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		int hour = calendar.get(Calendar.HOUR);

		// Aufbau der ID aus dem aktuellen Datum und der Uhrzeit
		id.append(day + 25);
		id.append(month + 56);
		id.append(year - 2000 + 7);
		id.append(hour + 71);

		// Einfügen von sechs zufälligen Kleinbuchstaben an zufälligen Positionen der
		// UserID
		for (int i = 0; i < 6; ++i) {
			int position = random.nextInt(id.length() + 1);
			char c = lowerCase.charAt(random.nextInt(lowerCase.length()));
			id.insert(position, c);
		}

		// Konvertierung und Rückgabe der UserID in String (immutable)
		String result = id.toString();
		return result;
	}
}
