import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import de.privacy_avare.repository.ProfileRepository;
import de.privacy_avare.repository.ProfileRepositoryCouchDBImpl;
import de.privacy_avare.service.ClearanceService;
import de.privacy_avare.service.ProfileService;

public class TestMain {

	public static void main(String[] args) throws InterruptedException {
		ProfileRepositoryCouchDBImpl rep = new ProfileRepositoryCouchDBImpl();
		System.out.println(rep.findPreferencesById("3764p2481xfbi66v"));
	}
}
