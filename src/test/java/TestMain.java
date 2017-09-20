import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import de.privacy_avare.repository.ProfileRepository;
import de.privacy_avare.service.ClearanceService;
import de.privacy_avare.service.ProfileService;

public class TestMain {

	public static void main(String[] args) throws InterruptedException {
		Date d = new Date(3155760000000L);
		System.out.println(d);
	}
}
