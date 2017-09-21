package de.privacy_avare.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import de.privacy_avare.domain.Profile;
import de.privacy_avare.domain.ProfileCouchDB;
import de.privacy_avare.dto.AllProfiles;
import de.privacy_avare.exeption.NoProfilesInDatabaseException;
import de.privacy_avare.exeption.ProfileNotFoundException;

/**
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see <a href="https://swagger.io/license/">Swagger License</a>
 * @see <a href="https://github.com/springfox/springfox">Springfox License</a>)
 */

public class ProfileRepositoryCouchDBImpl implements ProfileRepository {

	private String address;
	private int port;
	private String database;
	private String url;

	public ProfileRepositoryCouchDBImpl() {
		InputStream inputStream = null;
		try {
			inputStream = getClass().getClassLoader().getResourceAsStream("application.properties");
			Properties properties = new Properties();
			properties.load(inputStream);

			this.address = properties.getProperty("couchdb.adress");
			this.port = Integer.valueOf(properties.get("couchdb.port").toString());
			this.database = properties.getProperty("couchdb.databaseName");
		} catch (Exception e) {
			this.address = "http://localhost";
			this.port = 5984;
			this.database = "profiles";
			e.printStackTrace();
			System.out.println("Verbindungseinstellungen mit CouchDB auf default-Werte gesetzt");
			System.out.println("URL: " + this.url);
		} finally {
			this.url = this.address + ":" + this.port + "/" + this.database + "/";
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public <S extends Profile> S save(S entity) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			ProfileCouchDB dbProfile = restTemplate.getForObject(url + entity.get_id(), ProfileCouchDB.class);
			dbProfile.setDetails(entity);
			restTemplate.put(url + entity.get_id(), dbProfile);
		} catch (HttpClientErrorException e) {
			restTemplate.put(url + entity.get_id(), entity);
		}
		return entity;
	}

	@Override
	public <S extends Profile> Iterable<S> save(Iterable<S> entities) {
		for (Profile profile : entities) {
			this.save(profile);
		}
		return entities;

	}

	@Override
	public Profile findOne(String id) {
		RestTemplate restTemplate = new RestTemplate();
		Profile profile;
		try {
			profile = restTemplate.getForObject(url + id, Profile.class);
		} catch (HttpClientErrorException e) {
			profile = null;
		}
		return profile;
	}

	@Override
	public boolean exists(String id) {
		RestTemplate restTemplate = new RestTemplate();
		Profile profile;
		try {
			profile = restTemplate.getForObject(url + id, Profile.class);
		} catch (HttpClientErrorException e) {
			profile = null;
		}
		boolean exists = (profile != null);
		return exists;
	}

	@Override
	public Iterable<Profile> findAll() {
		RestTemplate restTemplate = new RestTemplate();
		AllProfiles allProfiles = new AllProfiles();
		List<Profile> list = new ArrayList<Profile>();

		allProfiles = restTemplate.getForObject(url + "_all_docs", AllProfiles.class);
		for (String id : allProfiles.getAllIds()) {
			Profile profile = this.findOne(id);
			list.add(profile);
		}
		return list;
	}

	@Override
	public Iterable<Profile> findAll(Iterable<String> ids) {
		RestTemplate restTemplate = new RestTemplate();
		ArrayList<Profile> list = new ArrayList<Profile>();
		try {
			for (String id : ids) {
				Profile profile = this.findOne(id);
				if (profile != null) {
					list.add(profile);
				}
			}
		} catch (HttpClientErrorException e) {
			throw new ProfileNotFoundException(e.getMessage());
		}
		return list;
	}

	@Override
	public long count() throws HttpClientErrorException {
		RestTemplate restTemplate = new RestTemplate();
		long counter = 0L;
		AllProfiles allProfiles = restTemplate.getForObject(url + "_all_docs", AllProfiles.class);
		counter = allProfiles.getTotal_rows();

		return counter;
	}

	@Override
	public void delete(String id) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			String rev = restTemplate.getForEntity(url, Profile.class).getHeaders().get("etag").get(0);
			rev = rev.substring(1, rev.length() - 1);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url + id).queryParam("rev", rev);
			restTemplate.delete(builder.build().encode().toUri());
		} catch (HttpClientErrorException e) {
			throw new ProfileNotFoundException(e.getMessage());
		}
	}

	@Override
	public void delete(Profile entity) {
		this.delete(entity.get_id());
	}

	@Override
	public void delete(Iterable<? extends Profile> entities) {
		RestTemplate restTemplate = new RestTemplate();
		for (Profile profile : entities) {
			this.delete(profile.get_id());
		}
	}

	@Override
	public void deleteAll() {
		Iterable<Profile> profiles = this.findAll();
		this.delete(profiles);
	}

	@Override
	public List<Profile> findAllByOrderByIdAsc() {
		List<Profile> list = new ArrayList<Profile>();
		this.findAll().forEach(list::add);
		if (list.isEmpty() == false) {
			list.sort((a, b) -> a.get_id().compareTo(b.get_id()));
		} else {
			throw new NoProfilesInDatabaseException();
		}
		return list;
	}

	@Override
	public List<Profile> findAllByLastProfileContactBefore(Date date) {
		List<Profile> list = new ArrayList<Profile>();
		this.findAll().forEach(list::add);
		if (list.isEmpty() == false) {
			for (Profile profile : list) {
				if (profile.getLastProfileContact().before(date) == false) {
					list.remove(profile);
				}
			}
		} else {
			throw new NoProfilesInDatabaseException();
		}
		return list;
	}

	@Override
	public Date findLastProfileContactById(String id) {
		RestTemplate restTemplate = new RestTemplate();
		Profile profile = new Profile();
		try {
			profile = restTemplate.getForObject(url + id, Profile.class);
		} catch (HttpClientErrorException e) {
			throw new ProfileNotFoundException(e.getMessage());
		}
		Date lastProfileContact = profile.getLastProfileContact();

		return lastProfileContact;
	}

	@Override
	public Date findLastProfileChangeById(String id) throws ProfileNotFoundException {
		RestTemplate restTemplate = new RestTemplate();
		Profile profile = new Profile();
		try {
			profile = restTemplate.getForObject(url + id, Profile.class);
		} catch (HttpClientErrorException e) {
			throw new ProfileNotFoundException(e.getMessage());
		}
		Date lastProfileChange = profile.getLastProfileChange();

		return lastProfileChange;
	}

	@Override
	public String findPreferencesById(String id) throws ProfileNotFoundException {
		RestTemplate restTemplate = new RestTemplate();
		Profile profile = new Profile();
		try {
			profile = restTemplate.getForObject(url + id, Profile.class);
		} catch (HttpClientErrorException e) {
			throw new ProfileNotFoundException(e.getMessage());
		}
		String preferences = profile.getPreferences();

		return preferences;
	}
}
