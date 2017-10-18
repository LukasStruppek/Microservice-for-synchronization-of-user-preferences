/*
 * Copyright 2017 Lukas Struppek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.privacy_avare.repository;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import de.privacy_avare.config.DefaultProperties;
import de.privacy_avare.couchDBDomain.AllProfiles;
import de.privacy_avare.couchDBDomain.ProfileCouchDB;
import de.privacy_avare.domain.Profile;
import de.privacy_avare.exeption.NoProfilesInDatabaseException;
import de.privacy_avare.exeption.ProfileNotFoundException;

/**
 * Klasse entspricht der Implementierung des ProfileRepository-Interface für die
 * Interaktion mit CouchDB. Die Realisierung ist vollständig mithilfe von
 * REST-Anfragen umgesetzt, um eine flexible Anbindung zu ermöglichen.
 * 
 * @author Lukas Struppek
 * @version 1.0
 * @see <a href="https://swagger.io/license/">Swagger License</a>
 * @see <a href="https://github.com/springfox/springfox">Springfox License</a>
 */

@Service
public class ProfileRepositoryCouchDBImpl implements ProfileRepository {

	private String address;
	private int port;
	private String database;
	private String url;
	private static boolean infoPrinted = false;

	/**
	 * default-Konstruktor, welcher versucht, sich aus der Datei
	 * application.properties die Verbindungsdetails 'couchdb.adress',
	 * 'couchdb.port' und 'couchdb.databaseName' für einen Zugriff auf eine
	 * CouchDB-Datenbank zu besorgen.
	 * 
	 * Schlägt der Versuch fehl, so werden folgende default-Werte genutzt:
	 * 'couchdb.adress = http://localhost', 'couchdb.port = 5984' und
	 * 'couchdb.databaseName = profiles'. Eine entsprechende Mitteilung wird auf der
	 * Konsole ausgegeben.
	 */
	public ProfileRepositoryCouchDBImpl() {
		Reader reader = null;
		try {
			reader = new FileReader("src/main/resources/application.properties");
			Properties properties = new Properties(new DefaultProperties());
			properties.load(reader);

			this.address = properties.getProperty("couchdb.adress");
			this.port = Integer.valueOf(properties.getProperty("couchdb.port"));
			this.database = properties.getProperty("couchdb.databaseName");
		} catch (Exception e) {
			this.address = "http://localhost";
			this.port = 5984;
			this.database = "profiles";

			e.printStackTrace();
			System.out.println("Verbindungseinstellungen mit CouchDB auf default-Werte gesetzt");
		} finally {
			this.url = this.address + ":" + this.port + "/" + this.database + "/";

			if (infoPrinted == false) {
				System.out.println("************************************************");
				System.out.println("Folgende Verbindungseinstellen für CouchDB wurden gesetzt:");
				System.out.println("\t Serveradresse: " + this.address);
				System.out.println("\t Port: " + this.port);
				System.out.println("\t Database Name: " + this.database);
				System.out.println("\t URL: " + this.url);
				System.out.println("************************************************");
				infoPrinted = true;

				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Speichert das im Parameter übergebene Profil in der Datenbank. Falls das
	 * Profil noch nicht in der Datenbank abgelegt ist, wird ein neues Dokument
	 * entsprechend erstellt.
	 * 
	 * @param entity
	 *            Zu speicherndes Profil.
	 * @return Zu speicherndes Profil (entspricht Parameter).
	 */
	@Override
	public <S extends Profile> S save(S entity) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			ProfileCouchDB dbProfile = restTemplate.getForObject(url + entity.get_id(), ProfileCouchDB.class);
			dbProfile.setDetails(entity);
			restTemplate.put(url + entity.get_id(), dbProfile);
		} catch (HttpClientErrorException hcee) {
			try {
				restTemplate.put(url + entity.get_id(), entity);
			} catch (Exception e) {
				restTemplate.put(url + entity.get_id(), entity);
			}
		}
		return entity;
	}

	/**
	 * Speichert eine Liste mit Profilen in der Datenbank. Für jedes einzelne Profil
	 * wird auf die Methode save(Profile) der Klasse zurückgegriffen.
	 * 
	 * @param entities
	 *            Zu speichernde Profile.
	 * @return Iterable mit zu speichernden Profilen (entspricht Parameter).
	 */
	@Override
	public <S extends Profile> Iterable<S> save(Iterable<S> entities) {
		for (Profile profile : entities) {
			this.save(profile);
		}
		return entities;

	}

	/**
	 * Sucht in der Datenbank nach einem Profil mit der im Parameter spezifizierten
	 * ProfileId. Wird kein Profil gefunden, so wird null zurückgeliefert.
	 * 
	 * @param id
	 *            ProfileId des zu suchenden Profils.
	 * @return Gefundenes Profil oder null.
	 */
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

	/**
	 * Sucht in der Datenbank nach einem Profil mit der im Parameter spezifizierten
	 * ProfileId und prüft dessen Existenz.
	 * 
	 * @param id
	 *            ProfileId des zu suchenden Profils.
	 * @return true, falls Profil existiert. Ansonsten false.
	 */
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

	/**
	 * Sucht alle in der Datenbank vorhandenen Profile und liefert ein Iterable mit
	 * allen Profilen zurück. Der Abruf aller Profile ist lediglich in der
	 * Entwicklung zu empfehlen, da es bei einer großen Anzahl an Dokumenten in der
	 * Datenbank zu einer langen Laufzeit kommen kann.
	 * 
	 * @return Iterable mit allen Dokumenten aus der Datenbank.
	 */
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

	/**
	 * Sucht alle im Parameter spezifizierten Profile in der Datenbank anhand ihrer
	 * Id und liefert ein Iterable mit allen Profilen zurück. Wird ein Prof
	 * 
	 * @param ids
	 *            ProfileIds der zu suchenden Profile.
	 * @return Iterable mit allen gefundenen Dokumenten aus der Datenbank.
	 */
	@Override
	public Iterable<Profile> findAll(Iterable<String> ids) {
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

	/**
	 * Zählt alle Dokumente in der Datenbank und gibt die Anzahl der Dokumente
	 * zurück.
	 * 
	 * @return Anzahl der Dokumente in der DB.
	 */
	@Override
	public long count() throws HttpClientErrorException {
		RestTemplate restTemplate = new RestTemplate();
		long counter = 0L;
		AllProfiles allProfiles = restTemplate.getForObject(url + "_all_docs", AllProfiles.class);
		counter = allProfiles.getTotal_rows();

		return counter;
	}

	/**
	 * Löscht ein Profile anhand seiner Id aus der Datenbank. Das Profil ist
	 * grundsätzlich gelöscht und kann nicht wiederhergestellt werden. Eine
	 * Wiederherstellung ist evtl. noch über einen direkten Zugriff auf die DB bzw.
	 * deren Verwaltung möglich.
	 * 
	 * @param id
	 *            ProfileId des zu löschenden Profils.
	 */
	@Override
	public void delete(String id) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			String rev = restTemplate.getForEntity(url + id, Profile.class).getHeaders().get("etag").get(0);
			rev = rev.substring(1, rev.length() - 1);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url + id).queryParam("rev", rev);
			restTemplate.delete(builder.build().encode().toUri());
			HashMap<String, String[]> hashMap = new HashMap<String, String[]>();
			hashMap.put(id, new String[] { rev });
			ResponseEntity<String> response = restTemplate.postForEntity(url + "_purge", hashMap, String.class);
			System.out.println(response.getBody());
		} catch (HttpClientErrorException e) {
			throw new ProfileNotFoundException(e.getMessage());
		} catch (HttpServerErrorException e) {
		}
	}

	/**
	 * Sucht nach der Id des übergebenen Profils in der Datenbank und löscht dieses,
	 * falls vorhanden. Der Löschvorgang entspricht dem des Methodenaufrufs von
	 * delete(String id).
	 * 
	 * @param entity
	 *            Profil, welches aus der DB gelöscht werden soll.
	 */
	@Override
	public void delete(Profile entity) {
		this.delete(entity.get_id());
	}

	/**
	 * Sucht nach den Id der übergebenen Profile in der Datenbank und löscht diese,
	 * falls vorhanden. Der Löschvorgang entspricht jeweils dem des Methodenaufrufs
	 * von delete(String id).
	 * 
	 * @param entities
	 *            Profile, welches aus der DB gelöscht werden soll.
	 */
	@Override
	public void delete(Iterable<? extends Profile> entities) {
		for (Profile profile : entities) {
			this.delete(profile.get_id());
		}
	}

	/**
	 * Sucht und löscht alle Profile, welche sich in der Datenbank befinden.
	 */
	@Override
	public void deleteAll() {
		Iterable<Profile> profiles = this.findAll();
		this.delete(profiles);
	}

	/**
	 * Sucht und liefert alle Dokumente aus der Datenbank zurück, Aufsteigend nach
	 * Ids sortiert.
	 * 
	 * @return Liste aller Profile, aufsteigend nach Id sortiert.
	 */
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

	/**
	 * Sucht alle Profile, welche einen lastProfileContact vor dem im Parameter
	 * spezifizierten Zeitpunkt besitzen. Entsprechende Profile werden
	 * zurückgeliefert.
	 * 
	 * @param date
	 *            lastProfileContact, gemäß welchem die Profile gesucht werden.
	 * 
	 * @return Liste aller Profile mit lastProfileContact vor Parameter.
	 */
	@Override
	public List<Profile> findAllByLastProfileContactBefore(Date date) {
		List<Profile> list = new ArrayList<Profile>();
		for (Profile profile : this.findAll()) {
			if (profile.getLastProfileContact().before(date) == true) {
				list.add(profile);
			}
		}
		return list;
	}

	/**
	 * Sucht nach einem Profil mit der im Parameter spezifizierten Id und liefert,
	 * sofern vorhanden, den Wert von lastProfileContact zurück.
	 * 
	 * @param id
	 *            ProfileId des zu suchenden Profils.
	 * 
	 * @return lastProfileContact des Profils.
	 */
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

	/**
	 * Sucht nach einem Profil mit der im Parameter spezifizierten Id und liefert,
	 * sofern vorhanden, den Wert von lastProfileChange zurück.
	 * 
	 * @param id
	 *            ProfileId des zu suchenden Profils.
	 * 
	 * @return lastProfileChange des Profils.
	 */
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

	/**
	 * Sucht nach einem Profil mit der im Parameter spezifizierten Id und liefert,
	 * sofern vorhanden, den Wert von preferences zurück.
	 * 
	 * @param id
	 *            ProfileId des zu suchenden Profils.
	 * 
	 * @return preferences des Profils.
	 */
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
