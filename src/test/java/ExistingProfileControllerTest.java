import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

public class ExistingProfileControllerTest {
	/**
	 * DELETE: /v1/profiles/{id} UserCase2
	 */
	public static void deleteProfile(String id) {
		System.out.println("*** Beginn Aufruf: DELETE - http://localhost:8080/v1/profiles/" + id + " ***");
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete("http://localhost:8080/v1/profiles/" + id);
		System.out.println("*** Ende Aufruf: GET - http://localhost:8080/v1/profiles/" + id + " ***");
	}

	/**
	 * PUT: /v1/profiles/{overwrite} UserCase3
	 */
	public static Profile pushProfile(Profile profile, boolean overwrite) {
		System.out.println("*** Beginn Aufruf: PUT - http://localhost:8080/v1/profiles/" + overwrite + " ***");
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.put("http://localhost:8080/v1/profiles/" + overwrite, profile);
		Profile profile2 = restTemplate.getForObject("http://localhost:8080/v1/profiles/" + profile.getId(),
				Profile.class);
		System.out.println("*** Aktualisiertes Profil aus der DB: " + profile2 + " ***");
		System.out.println("*** Der Methode übergebenes Profil: " + profile);
		System.out.println("*** Ende Aufruf: PUT - http://localhost:8080/v1/profiles/" + overwrite + " ***");

		return profile2;
	}

	/**
	 * GET: /v1/profiles/{profileId} UserCase4
	 */
	public static Profile pullProfile(String id, Date clientLastProfileChangeTimestamp) {
		System.out.println("*** Beginn Aufruf: GET - http://localhost:8080/v1/profiles/" + id + " ***");
		RestTemplate restTemplate = new RestTemplate();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("lastProfileChangeTimestamp", formatter.format(clientLastProfileChangeTimestamp));
		Profile profile = restTemplate.getForObject("http://localhost:8080/v1/profiles/", Profile.class, params);
		System.out.println("*** Gefundenes Profil: " + profile);
		System.out.println("*** Ende Aufruf: GET - http://localhost:8080/v1/profiles/" + id + " ***");
		return profile;
	}
	
	public static void saveProvile(Profile p) {
		RestTemplate template = new RestTemplate();
		template.put("http://localhost:8080/v1/profiles/test", p);
	}
}
