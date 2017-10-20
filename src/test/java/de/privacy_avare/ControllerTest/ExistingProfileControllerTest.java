package de.privacy_avare.ControllerTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import de.privacy_avare.domain.Profile;
import de.privacy_avare.repository.ProfileRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ExistingProfileControllerTest {
	@Autowired
	TestRestTemplate restTemplate;

	@Autowired
	ProfileRepository profileRepository;

	private LinkedList<String> generatedIds = new LinkedList<String>();
	private SimpleDateFormat dateFormat = new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss,SSS");
	private String mockId;

	@Before
	public void generateMockProfile() {
		// Generierung einer MockId nach dem Schema xxxxxx1234567890, wobei x für
		// beliebige Kleinbuchstaben gilt.
		StringBuffer mockId = new StringBuffer();
		mockId.append("a");
		char[] chars = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
				'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
		for (int i = 0; i < 5; ++i) {
			mockId.append(chars[(int) (Math.random() * 26)]);
		}
		mockId.append("1234567890");
		this.mockId = mockId.toString().toLowerCase();
		restTemplate.postForEntity("/v1/newProfiles/" + this.mockId, null, String.class);
	}

	@After
	public void deleteGeneratedProfils() {
		for (String id : generatedIds) {
			profileRepository.delete(id);
		}
		generatedIds.clear();
	}

	/**
	 * Unit-Test für REST-API DELETE /v1/profiles/{id}. Zunächst wird geprüft, ob
	 * ein bereits vorhandenes Profil korrekt auf ein unsync-Profil gesetzt wird und
	 * die Eigenschaft lastProfileChange korrekt angepasst wird (HttpStatus 200).
	 * 
	 * Anschließend wird geprüft, ob ein bereits aus unsync gesetztes Profil durch
	 * erneuten Unsync-Befehl geändert wird oder die Datenbank ihre Konsistenz
	 * beibehält (HttpStatus 200).
	 * 
	 * Weiterhin wird geprüft, ob beim Versuch des Löschens mittels einer nicht
	 * vorhandenen ProfileId, einer ProfileId mit ungültigem Format und keiner
	 * Übergabe einer ProfileId eine entsprechende Fehlermeldung zurückgegeben wird
	 * (HttpStatus 404).
	 */
	@Test
	public void testDeleteProfile() {
		String mockUnsyncPreferences = "Profil von Synchronisation ausgeschlossen!";
		generatedIds.add(this.mockId);

		// Überprüfung des Löschens eines Profils, bei welchem dies ein unsync-Profil
		// erhält.
		ResponseEntity<Void> responseEntity1 = restTemplate.exchange("/v1/profiles/" + this.mockId, HttpMethod.DELETE,
				new HttpEntity<String>(mockUnsyncPreferences), Void.class);
		assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
		Profile mockProfile1 = profileRepository.findOne(this.mockId);
		assertThat(mockProfile1.getPreferences()).isEqualTo(mockUnsyncPreferences);
		assertThat(mockProfile1.getLastProfileChange())
				.isAfterYear(GregorianCalendar.getInstance(Locale.GERMANY).get(Calendar.YEAR) + 99);
		assertThat(mockProfile1.getLastProfileChange())
				.isBeforeYear(GregorianCalendar.getInstance(Locale.GERMANY).get(Calendar.YEAR) + 101);

		// Überprüfung des Löschens des Profils, nachdem dies bereits aus unsync gesetzt
		// wurde.
		ResponseEntity<Void> responseEntity2 = restTemplate.exchange("/v1/profiles/" + this.mockId, HttpMethod.DELETE,
				new HttpEntity<String>(mockUnsyncPreferences), Void.class);
		assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
		Profile mockProfile2 = profileRepository.findOne(this.mockId);
		assertThat(mockProfile2.getPreferences()).isEqualTo(mockUnsyncPreferences);
		assertThat(mockProfile2.getLastProfileChange())
				.isAfterYear(GregorianCalendar.getInstance(Locale.GERMANY).get(Calendar.YEAR) + 99);
		assertThat(mockProfile2.getLastProfileChange())
				.isBeforeYear(GregorianCalendar.getInstance(Locale.GERMANY).get(Calendar.YEAR) + 101);

		// Überprüfung des Löschens eines nicht vorhandenen Profils mit gültiger
		// ProfileId.
		ResponseEntity<String> responseEntity3 = restTemplate.exchange("/v1/profiles/" + this.mockId.replace('a', 'b'),
				HttpMethod.DELETE, new HttpEntity<String>(mockUnsyncPreferences), String.class);
		assertThat(responseEntity3.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseEntity3.getBody()).containsSequence("ProfileNotFoundException");

		// Überprüfung des Löschens eines Profils mit ungültigem Format der übergebenen
		// ProfileId.
		ResponseEntity<String> responseEntity4 = restTemplate.exchange("/v1/profiles/" + this.mockId + "b",
				HttpMethod.DELETE, new HttpEntity<String>(mockUnsyncPreferences), String.class);
		assertThat(responseEntity4.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseEntity4.getBody()).containsSequence("ProfileNotFoundException");

		// Überprüfung des Löschens eines Profils ohne Übergabe der ProfileId
		ResponseEntity<String> responseEntity5 = restTemplate.exchange("/v1/profiles/", HttpMethod.DELETE,
				new HttpEntity<String>(mockUnsyncPreferences), String.class);
		assertThat(responseEntity5.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseEntity5.getBody()).containsSequence("Not Found");
	}

	/**
	 * Unit-Test für REST-API PUT /v1/profiles/{id}/{clientProfileChange}. Zunächst
	 * wird überprüft, ob bei einem neu erzeugten Profil die Preferences beim ersten
	 * Mal korrekt überschrieben werden (HttpStatus 200).
	 * 
	 * Anschließend wird geprüft, ob bei zu geringen Zeitunterschiedenem beim Push
	 * dieser entsprechend abgelehnt wird (HttpStatus 409).
	 * 
	 * Daneben wird die Reaktion auf leere Preferences bzw. ein ungültiges Format
	 * für den Zeitstempel korrekt beantwortet werden (HttpStatus 400)
	 * 
	 * Weiterhin wird geprüft, ob Preferences mit genügend Aktualität korrekt in die
	 * Datenbank geschrieben werden und die Eigenschaften lastProfileChange und
	 * lastProfileContact entsprechend angepasst werden (HttpStatus 200).
	 * 
	 * Abschließend wird geprüft, ob nicht in der Datenbank vorhandene Profile bzw.
	 * ProfileIds mit ungültigem Format korrekt abgefangen werden (HttpStatus 404).
	 */
	@Test
	public void testPushProfilePreferences() {
		String mockPreferences = "Die Präferenzen wurden erfolgreich aktualisiert";
		generatedIds.add(this.mockId);
		ResponseEntity<String> responseEntity;

		// Überprüfung, ob erstes Setzen der Preferences nach Erzeugung des Profils
		// fehlerfrei abläuft und die Eigenschaft lastProfileChange sowie
		// lastProfileContact korrekt angepasst werden.
		responseEntity = restTemplate.exchange("/v1/profiles/" + this.mockId + "/" + dateFormat.format(new Date()),
				HttpMethod.PUT, new HttpEntity<String>("Initiale Preferences"), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		Profile dbProfile = profileRepository.findOne(this.mockId);
		assertThat(dbProfile.getPreferences()).isEqualTo("Initiale Preferences");
		assertThat(dbProfile.getLastProfileChange()).isCloseTo(GregorianCalendar.getInstance(Locale.GERMANY).getTime(),
				1000);
		assertThat(dbProfile.getLastProfileContact()).isCloseTo(GregorianCalendar.getInstance(Locale.GERMANY).getTime(),
				1000);

		// Überprüfung, ob Überschreiben der Preferences unterhalb des minimalen
		// Zeitabstandes minTimeDifference korrekt abgelehnt wird.
		responseEntity = restTemplate.exchange("/v1/profiles/" + this.mockId + "/" + dateFormat.format(new Date()),
				HttpMethod.PUT, new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(responseEntity.getBody()).containsSequence("ClientPreferencesOutdatedException");

		// Überprüfung, ob Überschreibung der Preferencen mit gültigen Preferences und
		// falschem Format des Datums abgelehnt werden.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId + "/" + dateFormat.format(new Date()).replace("T", ""), HttpMethod.PUT,
				new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseEntity.getBody()).containsSequence("MethodArgumentTypeMismatchException");

		// Überprüfung, ob Ersetzen der Preferences mit leeren Preferences und
		// genügend Aktualität des Profils abgelehnt wird.
		Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
		calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId + "/" + dateFormat.format(calendar.getTime()), HttpMethod.PUT,
				new HttpEntity<String>(""), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseEntity.getBody()).containsSequence("HttpMessageNotReadableException");

		// Überprüfung, ob Ersetzen der Preferences mit gültigen Preferences und
		// genügend Aktualität des Profils fehlerfrei abläuft. Dabei werden auch die
		// angepassten Werte von lastProfileChange und lastProfileContact untersucht.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId + "/" + dateFormat.format(calendar.getTime()), HttpMethod.PUT,
				new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		dbProfile = profileRepository.findOne(this.mockId);
		assertThat(dbProfile.getPreferences()).isEqualTo(mockPreferences);
		assertThat(dbProfile.getLastProfileChange()).isEqualTo(calendar.getTime());
		assertThat(dbProfile.getLastProfileContact()).isCloseTo(GregorianCalendar.getInstance(Locale.GERMANY).getTime(),
				1000);

		// Überprüfung, ob nicht in der Datenbank vorhandene ProfileIds korrekt erkannt
		// werden.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId.replace('a', 'b') + "/" + dateFormat.format(new Date()), HttpMethod.PUT,
				new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseEntity.getBody()).containsSequence("ProfileNotFoundException");

		// Überprüfung, ob bei ungültigen ProfileIds ebenfalls eine
		// korrekte Fehlermeldung geworfen wird.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId.substring(1) + "/" + dateFormat.format(new Date()), HttpMethod.PUT,
				new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseEntity.getBody()).containsSequence("ProfileNotFoundException");
	}

	/**
	 * Unit-Test für REST-API PUT /v1/profiles/{id}/{clientProfileChange}/false.
	 * Entspricht dem Test ohne Angabe des Overwrite-Flags.
	 * 
	 * Zunächst wird überprüft, ob bei einem neu erzeugten Profil die Preferences
	 * beim ersten Mal korrekt überschrieben werden (HttpStatus 200).
	 * 
	 * Anschließend wird geprüft, ob bei zu geringen Zeitunterschiedenem beim Push
	 * dieser entsprechend abgelehnt wird (HttpStatus 409).
	 * 
	 * Daneben wird die Reaktion auf leere Preferences bzw. ein ungültiges Format
	 * für den Zeitstempel korrekt beantwortet werden (HttpStatus 400)
	 * 
	 * Weiterhin wird geprüft, ob Preferences mit genügend Aktualität korrekt in die
	 * Datenbank geschrieben werden und die Eigenschaften lastProfileChange und
	 * lastProfileContact entsprechend angepasst werden (HttpStatus 200).
	 * 
	 * Abschließend wird geprüft, ob nicht in der Datenbank vorhandene Profile bzw.
	 * ProfileIds mit ungültigem Format korrekt abgefangen werden (HttpStatus 404).
	 */
	@Test
	public void testPushProfilePreferencesWithoutOverwriting() {
		String mockPreferences = "Die Präferenzen wurden erfolgreich aktualisiert";
		generatedIds.add(this.mockId);
		ResponseEntity<String> responseEntity;

		// Überprüfung, ob erstes Setzen der Preferences nach Erzeugung des Profils
		// fehlerfrei abläuft und die Eigenschaft lastProfileChange sowie
		// lastProfileContact korrekt angepasst werden.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId + "/" + dateFormat.format(new Date()) + "/false", HttpMethod.PUT,
				new HttpEntity<String>("Initiale Preferences"), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		Profile dbProfile = profileRepository.findOne(this.mockId);
		assertThat(dbProfile.getPreferences()).isEqualTo("Initiale Preferences");
		assertThat(dbProfile.getLastProfileChange()).isCloseTo(GregorianCalendar.getInstance(Locale.GERMANY).getTime(),
				1000);
		assertThat(dbProfile.getLastProfileContact()).isCloseTo(GregorianCalendar.getInstance(Locale.GERMANY).getTime(),
				1000);

		// Überprüfung, ob Überschreiben der Preferences unterhalb des minimalen
		// Zeitabstandes minTimeDifference korrekt abgelehnt wird.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId + "/" + dateFormat.format(new Date()) + "/false", HttpMethod.PUT,
				new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(responseEntity.getBody()).containsSequence("ClientPreferencesOutdatedException");

		// Überprüfung, ob Überschreibung der Preferencen mit gültigen Preferences und
		// falschem Format des Datums abgelehnt werden.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId + "/" + dateFormat.format(new Date()).replace("T", "") + "/false",
				HttpMethod.PUT, new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseEntity.getBody()).containsSequence("MethodArgumentTypeMismatchException");

		// Überprüfung, ob Ersetzen der Preferences mit leeren Preferences und
		// genügend Aktualität des Profils abgelehnt wird.
		Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
		calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId + "/" + dateFormat.format(calendar.getTime()) + "/false", HttpMethod.PUT,
				new HttpEntity<String>(""), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseEntity.getBody()).containsSequence("HttpMessageNotReadableException");

		// Überprüfung, ob Ersetzen der Preferences mit gültigen Preferences und
		// genügend Aktualität des Profils fehlerfrei abläuft. Dabei werden auch die
		// angepassten Werte von lastProfileChange und lastProfileContact untersucht.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId + "/" + dateFormat.format(calendar.getTime()) + "/false", HttpMethod.PUT,
				new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		dbProfile = profileRepository.findOne(this.mockId);
		assertThat(dbProfile.getPreferences()).isEqualTo(mockPreferences);
		assertThat(dbProfile.getLastProfileChange()).isEqualTo(calendar.getTime());
		assertThat(dbProfile.getLastProfileContact()).isCloseTo(GregorianCalendar.getInstance(Locale.GERMANY).getTime(),
				1000);

		// Überprüfung, ob nicht in der Datenbank vorhandene ProfileIds korrekt erkannt
		// werden.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId.replace('a', 'b') + "/" + dateFormat.format(new Date()) + "/false",
				HttpMethod.PUT, new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseEntity.getBody()).containsSequence("ProfileNotFoundException");

		// Überprüfung, ob bei ungültigen ProfileIds ebenfalls eine
		// korrekte Fehlermeldung geworfen wird.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId.substring(1) + "/" + dateFormat.format(new Date()) + "/false",
				HttpMethod.PUT, new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseEntity.getBody()).containsSequence("ProfileNotFoundException");
	}

	/**
	 * Unit-Test für REST-API PUT /v1/profiles/{id}/{clientProfileChange}/true.
	 * 
	 */
	@Test
	public void testPushProfilePreferencesWithOverwriting() {
		String mockPreferences = "Die Präferenzen wurden erfolgreich aktualisiert";
		generatedIds.add(this.mockId);
		ResponseEntity<String> responseEntity;

		// Überprüfung, ob erstes Setzen der Preferences nach Erzeugung des Profils
		// fehlerfrei abläuft und die Eigenschaft lastProfileChange sowie
		// lastProfileContact korrekt angepasst werden.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId + "/" + dateFormat.format(new Date()) + "/true", HttpMethod.PUT,
				new HttpEntity<String>("Initiale Preferences"), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		Profile dbProfile = profileRepository.findOne(this.mockId);
		assertThat(dbProfile.getPreferences()).isEqualTo("Initiale Preferences");
		assertThat(dbProfile.getLastProfileChange()).isCloseTo(GregorianCalendar.getInstance(Locale.GERMANY).getTime(),
				1000);
		assertThat(dbProfile.getLastProfileContact()).isCloseTo(GregorianCalendar.getInstance(Locale.GERMANY).getTime(),
				1000);

		// Überprüfung, ob Überschreiben der Preferences unterhalb des minimalen
		// Zeitabstandes minTimeDifference korrekt überschrieben wird.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId + "/" + dateFormat.format(new Date()) + "/true", HttpMethod.PUT,
				new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		dbProfile = profileRepository.findOne(this.mockId);
		assertThat(dbProfile.getPreferences()).containsSequence(mockPreferences);
		assertThat(dbProfile.getLastProfileChange()).isCloseTo(new Date(), 100);
		assertThat(dbProfile.getLastProfileContact()).isCloseTo(new Date(), 100);

		// Überprüfung, ob Überschreibung der Preferencen mit gültigen Preferences und
		// falschem Format des Datums abgelehnt werden.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId + "/" + dateFormat.format(new Date()).replace("T", "") + "/true",
				HttpMethod.PUT, new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseEntity.getBody()).containsSequence("MethodArgumentTypeMismatchException");

		// Überprüfung, ob Ersetzen der Preferences mit leeren Preferences abgelehnt
		// wird.
		Calendar calendar = GregorianCalendar.getInstance(Locale.GERMANY);
		calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId + "/" + dateFormat.format(calendar.getTime()) + "/true", HttpMethod.PUT,
				new HttpEntity<String>(""), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseEntity.getBody()).containsSequence("HttpMessageNotReadableException");

		// Überprüfung, ob nicht in der Datenbank vorhandene ProfileIds korrekt erkannt
		// werden.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId.replace('a', 'b') + "/" + dateFormat.format(new Date()) + "/true",
				HttpMethod.PUT, new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseEntity.getBody()).containsSequence("ProfileNotFoundException");

		// Überprüfung, ob bei ungültigen ProfileIds ebenfalls eine
		// korrekte Fehlermeldung geworfen wird.
		responseEntity = restTemplate.exchange(
				"/v1/profiles/" + this.mockId.substring(1) + "/" + dateFormat.format(new Date()) + "/true",
				HttpMethod.PUT, new HttpEntity<String>(mockPreferences), String.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseEntity.getBody()).containsSequence("ProfileNotFoundException");
	}

}
