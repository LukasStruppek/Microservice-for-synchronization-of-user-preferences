import org.springframework.web.client.RestTemplate;

public class TestMain {

	public static void main(String[] args) {
		RestTemplate template = new RestTemplate();
		for (int i = 0; i < 10000; ++i) {
			try {
				template.postForEntity("http://localhost:8443/v1/dev", null, String.class);
				template.postForEntity("http://localhost:8443/v1/newProfiles", null, String.class);
			} catch (Exception e) {
			}

		}
	}

}
