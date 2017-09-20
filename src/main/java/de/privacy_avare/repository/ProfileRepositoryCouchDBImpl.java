package de.privacy_avare.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.web.client.RestTemplate;

import de.privacy_avare.domain.Profile;
import de.privacy_avare.exeption.ProfileNotFoundException;

public class ProfileRepositoryCouchDBImpl implements ProfileRepository {

	private final String adress = "http://localhost:5984";
	private final String database = "profiles";

	@Override
	public <S extends Profile> S save(S entity) {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + entity.getId();
		restTemplate.put(url, entity);
		return entity;
	}

	@Override
	public <S extends Profile> Iterable<S> save(Iterable<S> entities) {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/";
		for (Profile profile : entities) {
			restTemplate.put(url + profile.getId(), profile);
		}
		return entities;

	}

	@Override
	public Profile findOne(String id) {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + "id";
		Profile profile = restTemplate.getForObject(url, Profile.class);
		return profile;
	}

	@Override
	public boolean exists(String id) {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + "id";
		Profile profile = restTemplate.getForObject(url, Profile.class);
		boolean exists = (profile != null);
		return exists;
	}

	@Override
	public Iterable<Profile> findAll() {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + "_all_docs";
		Profile[] profiles = restTemplate.getForObject(url, Profile[].class);
		List<Profile> list = Arrays.asList(profiles);
		return list;
	}

	@Override
	public Iterable<Profile> findAll(Iterable<String> ids) {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/";
		ArrayList<Profile> list = new ArrayList<Profile>();
		for (String id : ids) {
			Profile profile = restTemplate.getForObject(url + id, Profile.class);
			if (profile != null) {
				list.add(profile);
			}
		}
		return list;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(String id) {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + id;
		restTemplate.delete(url);
	}

	@Override
	public void delete(Profile entity) {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + entity.getId();
		restTemplate.delete(url);
	}

	@Override
	public void delete(Iterable<? extends Profile> entities) {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/";
		for (Profile p : entities) {
			restTemplate.delete(url + p.getId());
		}
	}

	@Override
	public void deleteAll() {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + "_all_docs";
		restTemplate.delete(url);
	}

	@Override
	public List<Profile> findAllByOrderByIdAsc() {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + "_all_docs";
		Profile[] profiles = restTemplate.getForObject(url, Profile[].class);
		List<Profile> list;
		if (profiles != null) {
			list = Arrays.asList(profiles);
			list.sort((a, b) -> a.getId().compareTo(b.getId()));
		} else {
			throw new ProfileNotFoundException();
		}
		return list;
	}

	@Override
	public List<Profile> findAllByLastProfileContactBefore(Date date) {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + "_all_docs";
		Profile[] profiles = restTemplate.getForObject(url, Profile[].class);
		List<Profile> list;
		if (profiles != null) {
			list = Arrays.asList(profiles);
			for (Profile p : list) {
				if (p.getLastProfileContact().before(date) == false) {
					list.remove(p);
				}
			}
		} else {
			throw new ProfileNotFoundException();
		}
		return list;
	}

	@Override
	public Date findLastProfileContactById(String id) throws ProfileNotFoundException {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + id;
		Profile profile = restTemplate.getForObject(url, Profile.class);
		Date lastProfileContact = new Date();
		if (profile != null) {
			lastProfileContact = profile.getLastProfileContact();
		} else {
			throw new ProfileNotFoundException();
		}
		return lastProfileContact;
	}

	@Override
	public Date findLastProfileChangeById(String id) throws ProfileNotFoundException {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + id;
		Profile profile = restTemplate.getForObject(url, Profile.class);
		Date lastProfileChange = new Date();
		if (profile != null) {
			lastProfileChange = profile.getLastProfileChange();
		} else {
			throw new ProfileNotFoundException();
		}
		return lastProfileChange;
	}

	@Override
	public String findPreferencesById(String id) throws ProfileNotFoundException {
		RestTemplate restTemplate = new RestTemplate();
		String url = this.adress + "/" + this.database + "/" + id;
		Profile profile = restTemplate.getForObject(url, Profile.class);
		String preferences = "";
		if (profile != null) {
			preferences = profile.getPreferences();
		} else {
			throw new ProfileNotFoundException();
		}
		return preferences;
	}
}
