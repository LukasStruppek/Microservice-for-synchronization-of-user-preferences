import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import de.privacy_avare.repository.ProfileRepository;
import de.privacy_avare.repository.ProfileRepositoryCouchDBImpl;
import de.privacy_avare.domain.Profile;

public class TestMain {

	public static void main(String[] args) throws InterruptedException {
		RestTemplate template = new RestTemplate();
		String id = template.postForObject("https://localhost:8443/v1/newProfiles", null, String.class);
		System.out.println(id);
	}
}
