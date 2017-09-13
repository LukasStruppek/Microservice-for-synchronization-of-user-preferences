import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import de.privacy_avare.repository.ProfileRepository;
import de.privacy_avare.service.ClearanceService;
import de.privacy_avare.service.ProfileService;

public class TestMain {

	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new ThreadTest();
		Thread t2 = new ThreadTest();
		Thread t3 = new ThreadTest();
		Thread t4 = new ThreadTest();
		System.out.println("***** Beginn Test um : " + new Date() + " *****");
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		System.out.println("***** Ende Test um : " + new Date() + " *****");
	}
}
