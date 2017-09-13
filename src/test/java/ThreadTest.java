import org.springframework.web.client.RestTemplate;

public class ThreadTest extends Thread {
	@Override
	public void run() {
		for (int i = 0; i < 25000; ++i) {
			RestTemplate restTemplate = new RestTemplate();
			Profile profile = restTemplate.getForObject("http://localhost:8080/v1/newProfiles", Profile.class);
		}
	}
}
