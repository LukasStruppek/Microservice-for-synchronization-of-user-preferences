/*
 * Copyright 2017 Lukas Struppek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.privacy_avare.service;

import java.io.FileReader;
import java.io.Reader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.privacy_avare.config.DefaultProperties;
import de.privacy_avare.domain.Profile;
import de.privacy_avare.repository.ProfileRepository;

/**
 * Der Service dient zum endgültigen Löschen von Profilen aus der Datenbank. Der
 * Service ist hauptsächlich für die Anwendung in einem Scheduler gedacht.
 * 
 * @author Lukas Struppek
 * @version 1.0
 *
 */

@Service
public class ClearanceService {

	/**
	 * Service zum Abruf der Profile aus der Datenbank.
	 */
	@Autowired
	ProfileRepository profileRepository;

	private int monthsBeforeDeletion;

	private static boolean infoPrinted = false;

	/**
	 * Konstruktor zum Festlegen des Zeitraums, nach dem ein Profil ohne Kontakt
	 * gelöscht wird. Zeitraum wird in application.properties festgelegt.
	 * Default-Wert sind 18 Monate.
	 */
	public ClearanceService() {
		Reader reader = null;
		try {
			reader = new FileReader("src/main/resources/application.properties");
			Properties properties = new Properties(new DefaultProperties());
			properties.load(reader);

			this.monthsBeforeDeletion = Integer.valueOf(properties.getProperty("server.monthsBeforeDeletion"));
		} catch (Exception e) {
			e.printStackTrace();
			this.monthsBeforeDeletion = 18;
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (infoPrinted == false) {
				System.out.println("************************************************");
				System.out.println("Folgender Zeitraum ohne Profilkontakt vor dem Löschen wurde festgelegt:");
				System.out.println("\t Zeitraum in Monaten: " + this.monthsBeforeDeletion);
				System.out.println("************************************************");
				infoPrinted = true;
			}
		}
	}

	/**
	 * Löscht alle Profile in der Datenbank endgültig, auf welche länger als 18
	 * Monate nicht zugegriffen wurde. Der Zugriffszeitpunkt wird anhand der
	 * Eigenschaft lastProfileContact geprüft.
	 */
	public void cleanDatabase() {
		// Berechnung Zeitpunkt vor 'monthsBeforeDeletion' Monaten
		Calendar cal = GregorianCalendar.getInstance(Locale.GERMANY);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - (30 * monthsBeforeDeletion));

		// Suchen und Löschen aller Profile mit lastProfileContact vor
		// 'monthsBeforeDeletion' Monaten oder
		// länger
		Iterable<Profile> unusedProfiles = profileRepository.findAllByLastProfileContactBefore(cal.getTime());
		profileRepository.delete(unusedProfiles);
	}

}
