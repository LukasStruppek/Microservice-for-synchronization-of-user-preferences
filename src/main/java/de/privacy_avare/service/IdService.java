package de.privacy_avare.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.privacy_avare.repository.ProfileRepository;

/**
 * Klasse stellt statische Methode zur Generierung einer 16-stelligen,
 * eindeutigen UserID bereit. Erzeugung von Instanzen der Klasse ist nicht
 * möglich.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */

@Service
public class IdService {

	@Autowired
	ProfileRepository profileRepository;

	/**
	 * Erzeugt unter Verwendung des aktuellen Datum, der aktuellen Uhrzeit und
	 * zufälligen Buchstaben eine eindeutige UserID.
	 * 
	 * @return generierte UserID
	 */
	public String generateID() {
		Random random = new Random();
		Calendar calendar = Calendar.getInstance(Locale.GERMANY);
		calendar.setTime(new Date());
		StringBuffer id = new StringBuffer();
		final String lowerCase = "abcdefghijklmnopqrstuvwxyz";

		// Abruf des aktuellen Datums und der Uhrzeit in Zahlenform
		int day = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		// Aufbau der ID aus dem aktuellen Datum und der Uhrzeit
		id.append(day + 25);
		id.append(month + 56);
		id.append(year - 2000 + 7);
		id.append(hour + 71);
		id.append(minute + 10);

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

	public boolean isIdAlreadyTaken(String id) {
		boolean idExists = profileRepository.exists(id);
		return idExists;
	}

	public boolean validateId(String id) {
		char[] chars = id.toCharArray();
		int numbers = 0;
		int characters = 0;
		boolean isValid;
		for (char c : chars) {
			if (Character.isDigit(c)) {
				++numbers;
			} else if (Character.isLetter(c)) {
				++characters;
			} else {
				isValid = false;
				return isValid;
			}
		}
		if (numbers == 10 && characters == 6) {
			isValid = true;
		} else {
			isValid = false;
		}
		return isValid;

	}
}
