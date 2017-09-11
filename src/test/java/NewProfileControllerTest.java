import org.springframework.web.client.RestTemplate;

public class NewProfileControllerTest {
	/**
	 * GET: /v1/newProfiles UserCase1
	 */
	public static Profile newProfiles() {
		System.out.println("*** Beginn Aufruf: GET - http://localhost:8080/v1/newProfiles - noParam ***");
		RestTemplate restTemplate = new RestTemplate();
		Profile profile = restTemplate.getForObject("http://localhost:8080/v1/newProfiles", Profile.class);
		System.out.println("*** Erzeugtes Profil: " + profile + " ***");
		System.out.println("*** Ende Aufruf: GET - http://localhost:8080/v1/newProfiles - noParam ***");

		return profile;
	}

	/**
	 * GET: /v1/newProfiles/{id} UserCase1
	 */
	public static Profile newProfiles(String id) {
		System.out.println("*** Aufruf: GET - http://localhost:8080/v1/newProfiles/" + id + " ***");
		RestTemplate restTemplate = new RestTemplate();
		Profile profile = restTemplate.getForObject("http://localhost:8080/v1/newProfiles/" + id, Profile.class);
		System.out.println("*** Erzeugtes Profil: " + profile + " ***");
		System.out.println("*** Ende Aufruf: GET - http://localhost:8080/v1/newProfiles/" + id + "***");
		
		return profile;
	}
}
