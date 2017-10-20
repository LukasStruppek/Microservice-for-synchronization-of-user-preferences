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

package de.privacy_avare.ControllerTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import de.privacy_avare.repository.ProfileRepository;

/**
 * Unit-Test für den REST-Controller NewProfileController, welcher
 * Schnittstellen zur Erzeugung neuer Profile in der Datenbank bereitstellt.
 * 
 * @author Lukas Struppek
 * @version 1.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class NewProfileControllerTest {
	@Autowired
	TestRestTemplate restTemplate;

	@Autowired
	ProfileRepository profileRepository;

	private LinkedList<String> generatedIds = new LinkedList<String>();
	private StringBuffer mockId;

	/**
	 * Generiert eine zufällige ProfileId, um die Profilerzeugung basierend auf einer 
	 */
	@Before
	public void generateMockId() {
		// Generierung einer MockId nach dem Schema xxxxxx1234567890, wobei x für
		// beliebige Kleinbuchstaben gilt.
		StringBuffer mockId = new StringBuffer();
		mockId.append("A");
		char[] chars = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
				'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
		for (int i = 0; i < 5; ++i) {
			mockId.append(chars[(int) (Math.random() * 26)]);
		}
		mockId.append("1234567890");
		this.mockId = mockId;
	}

	/**
	 * Löscht die im Zuge des Tests erstellten Profile wieder aus der Datenbank.
	 */
	@After
	public void deleteGeneratedProfils() {
		for (String id : generatedIds) {
			profileRepository.delete(id);
		}
		generatedIds.clear();
	}

	/**
	 * Unit-Test für REST-API POST /v1/newProfiles. Überprüft den Fall einer
	 * automatischen Erzeugung einer neuen ProfileId durch den Server und deren
	 * formale Korrektheit (HttpStatus 201).
	 */
	@Test
	public void testCreateProfile() {
		ResponseEntity<String> responseEntity1 = restTemplate.postForEntity("/v1/newProfiles", null, String.class);
		generatedIds.add(responseEntity1.getBody());
		assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(responseEntity1.getBody().chars().filter(x -> Character.isDigit(x)).count()).isEqualTo(10);
		assertThat(responseEntity1.getBody().chars().filter(x -> Character.isLowerCase(x)).count()).isEqualTo(6);
		assertThat(responseEntity1.getBody().length()).isEqualTo(16);
	}

	/**
	 * Unit-Test für REST-API POST /v1/newProfiles/{id}. Überprüft die drei Fälle der
	 * korrekten Erzeugung eines Profils basierend auf einer gültigen ProfileId ,
	 * wobei einmal mit Kleinbuchstaben und einmal mit Großbuchstaben gearbeitet
	 * wird. Es wird die Erzeugung einer korrekten ProfileId mit Kleinbuchstaben
	 * überprüft (HttpStatus 201).
	 * 
	 * Zusätzlich wird die Erzeugung eines Profils mit einer bereits vorhandenen
	 * ProfileId überprüft (HttpStatus 409).
	 * 
	 * Weiterhin wird der Versuch der Profilerzeugung mit einer ungültigen ProfileId
	 * überprüft. Hierzu wird in vier Testfälle untergliedert: zu kurze ProfileId,
	 * zu lange ProfileId, zu viele Buchstaben in der ProfileId und zu wenige
	 * Buchstaben in der ProfileId. Weiterhin wird der Versuch mittels eines leeren
	 * Strings überprüft. In allen Fällen sollte es zu einer Fehlermeldung kommen
	 * (HttpStatus 422);
	 */
	@Test
	// Zeitpunkte noch zu prüfen!!!
	public void testCreateProfileWithKnownId() {
		// Überprüfung der korrekten Erstellung eines Profils mit vorhandener ProfileId
		// in Kleinbuchstaben
		ResponseEntity<String> responseEntity1 = restTemplate.postForEntity("/v1/newProfiles/" + mockId, null,
				String.class);
		generatedIds.add(responseEntity1.getBody());
		assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(responseEntity1.getBody().chars().filter(x -> Character.isDigit(x)).count()).isEqualTo(10);
		assertThat(responseEntity1.getBody().chars().filter(x -> Character.isLowerCase(x)).count()).isEqualTo(6);
		assertThat(responseEntity1.getBody().length()).isEqualTo(16);

		// Überprüfung der korrekten Erstellung eines Profils mit vorhandener ProfileId
		// in Großbuchstaben
		ResponseEntity<String> responseEntity2 = restTemplate.postForEntity(
				"/v1/newProfiles/" + mockId.toString().toUpperCase().replace('A', 'B'), null, String.class);
		generatedIds.add(responseEntity2.getBody());
		assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(responseEntity2.getBody().chars().filter(x -> Character.isDigit(x)).count()).isEqualTo(10);
		assertThat(responseEntity2.getBody().chars().filter(x -> Character.isLowerCase(x)).count()).isEqualTo(6);
		assertThat(responseEntity2.getBody().length()).isEqualTo(16);

		// Überprüfung der Erstellung eines bereits vorhandenen Profils mit gültigem
		// ProfileId-Format
		ResponseEntity<String> responseEntity3 = restTemplate.postForEntity("/v1/newProfiles/" + mockId, null,
				String.class);
		assertThat(responseEntity3.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(responseEntity3.getBody()).containsSequence("ProfileAlreadyExistsException");

		// Überprüfung der Erstellung eines neuen vorhandenen Profils mit ungültigem
		// ProfileId-Format (zu kurz)
		ResponseEntity<String> responseEntity4 = restTemplate
				.postForEntity("/v1/newProfiles/" + mockId.substring(0, mockId.length() - 1), null, String.class);
		assertThat(responseEntity4.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(responseEntity4.getBody()).containsSequence("MalformedProfileIdException");

		// Überprüfung der Erstellung eines neuen vorhandenen Profils mit ungültigem
		// ProfileId-Format (zu lang)
		ResponseEntity<String> responseEntity5 = restTemplate.postForEntity("/v1/newProfiles/" + mockId + "a", null,
				String.class);
		assertThat(responseEntity5.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(responseEntity5.getBody()).containsSequence("MalformedProfileIdException");

		// Überprüfung der Erstellung eines neuen vorhandenen Profils mit ungültigem
		// ProfileId-Format (zu viele Zahlen)
		ResponseEntity<String> responseEntity6 = restTemplate
				.postForEntity("/v1/newProfiles/" + mockId.replace(0, 0, "1"), null, String.class);
		assertThat(responseEntity6.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(responseEntity6.getBody()).containsSequence("MalformedProfileIdException");

		// Überprüfung der Erstellung eines neuen vorhandenen Profils mit ungültigem
		// ProfileId-Format (zu viele Buchstaben)
		ResponseEntity<String> responseEntity7 = restTemplate
				.postForEntity("/v1/newProfiles/" + mockId.replace(7, 7, "a"), null, String.class);
		assertThat(responseEntity7.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(responseEntity7.getBody()).containsSequence("MalformedProfileIdException");
	}

}
