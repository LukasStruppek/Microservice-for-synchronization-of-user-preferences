package de.privacy_avare.Repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.privacy_avare.domain.Profile;

public interface ProfileRepository extends CrudRepository<Profile, String>{

	List<Profile> findAllByOrderByIdAsc();
	
}
