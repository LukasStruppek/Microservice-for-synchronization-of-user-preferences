package de.privacy_avare.dao;

import org.springframework.data.repository.Repository;

import de.privacy_avare.domain.Profile;

public interface ProfileDepository extends Repository<Profile, String>{

	Profile findByProfileId(String profileId);

	void insertProfileToDb(Profile profile);

	void updateProfileInDb(Profile profile);

	void removeProfileById(String profileId);

}
