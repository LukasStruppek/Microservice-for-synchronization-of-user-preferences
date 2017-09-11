import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.springframework.web.client.RestTemplate;

public class TestMain {

	public static void main(String[] args) {
		RestTemplate restTemplate = new RestTemplate();
		Profile profile = new Profile("1234567890asdf" + " Erste Ausgabe");
		System.out.println(profile);
		restTemplate.put("http://localhost:8080/v1/profiles/test", profile);
		Profile profile2 = restTemplate.getForObject("http://localhost:8080/v1/profiles/1234567890asdf", Profile.class);
		System.out.println(profile2);
	}
}
