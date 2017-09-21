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
		ProfileRepositoryCouchDBImpl rep = new ProfileRepositoryCouchDBImpl();
		Profile p = new Profile("neues Dokument 2");
		p.setPreferences("Meine Präferenzen 2");
		p.setLastProfileChange(new Date());
		Profile d = new Profile("Noch ein neues Dokument");
		d.setPreferences("andere Präferenzen");
		for(Profile e : rep.findAll(Arrays.asList(p.get_id(), d.get_id())))
			System.out.println(e);
	}
}
