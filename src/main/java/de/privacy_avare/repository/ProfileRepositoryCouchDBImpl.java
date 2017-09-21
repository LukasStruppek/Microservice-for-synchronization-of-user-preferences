package de.privacy_avare.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import de.privacy_avare.domain.Profile;
import de.privacy_avare.domain.ProfileCouchDB;
import de.privacy_avare.dto.AllProfiles;
import de.privacy_avare.exeption.NoProfilesInDatabaseException;
import de.privacy_avare.exeption.ProfileNotFoundException;

public class ProfileRepositoryCouchDBImpl implements ProfileRepository {

	private final String adress = "http://localhost:5984";
	private final String database = "profiles";

	@Override
	public <S extends Profile> S save(S entity) {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + entity.get_id();
		try {
			ProfileCouchDB dbProfile = restTemplate.getForObject(url, ProfileCouchDB.class);
			dbProfile.setDetails(entity);
			restTemplate.put(url, dbProfile);
		} catch (HttpClientErrorException e) {
			restTemplate.put(url, entity);
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
		String url = this.adress + "/" + this.database + "/" + id;
		Profile profile;
		try {
			profile = restTemplate.getForObject(url, Profile.class);
		} catch (HttpClientErrorException e) {
			profile = null;
		}
		return profile;
	}

	@Override
	public boolean exists(String id) {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + id;
		Profile profile;
		try {
			profile = restTemplate.getForObject(url, Profile.class);
		} catch (HttpClientErrorException e) {
			profile = null;
		}
		boolean exists = (profile != null);
		return exists;
	}

	@Override
	public Iterable<Profile> findAll() {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + "_all_docs";
		AllProfiles allProfiles = new AllProfiles();
		List<Profile> list = new ArrayList<Profile>();

		allProfiles = restTemplate.getForObject(url, AllProfiles.class);
		for (String id : allProfiles.getAllIds()) {
			Profile profile = this.findOne(id);
			list.add(profile);
		}
		return list;
	}

	@Override
	public Iterable<Profile> findAll(Iterable<String> ids) {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/";
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
		String url = this.adress + "/" + this.database + "/" + "_all_docs";
		long counter = 0L;
		AllProfiles allProfiles = restTemplate.getForObject(url, AllProfiles.class);
		counter = allProfiles.getTotal_rows();

		return counter;
	}

	@Override
	public void delete(String id) {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + id;
		try {
			String rev = restTemplate.getForEntity(url, Profile.class).getHeaders().get("etag").get(0);
			rev = rev.substring(1, rev.length() - 1);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("rev", rev);
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
		String url = this.adress + "/" + this.database + "/" + id;
		Profile profile = new Profile();
		try {
			profile = restTemplate.getForObject(url, Profile.class);
		} catch (HttpClientErrorException e) {
			throw new ProfileNotFoundException(e.getMessage());
		}
		Date lastProfileContact = profile.getLastProfileContact();

		return lastProfileContact;
	}

	@Override
	public Date findLastProfileChangeById(String id) throws ProfileNotFoundException {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + id;
		Profile profile = new Profile();
		try {
			profile = restTemplate.getForObject(url, Profile.class);
		} catch (HttpClientErrorException e) {
			throw new ProfileNotFoundException(e.getMessage());
		}
		Date lastProfileChange = profile.getLastProfileChange();

		return lastProfileChange;
	}

	@Override
	public String findPreferencesById(String id) throws ProfileNotFoundException {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + id;
		Profile profile = new Profile();
		try {
			profile = restTemplate.getForObject(url, Profile.class);
		} catch (HttpClientErrorException e) {
			throw new ProfileNotFoundException(e.getMessage());
		}
		String preferences = profile.getPreferences();

		return preferences;
	}
}
