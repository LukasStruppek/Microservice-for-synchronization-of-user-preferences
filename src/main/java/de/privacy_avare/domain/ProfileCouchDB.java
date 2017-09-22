package de.privacy_avare.domain;

import com.couchbase.client.java.repository.annotation.Field;

public class ProfileCouchDB extends Profile {
	@Field
	String _rev;

	public ProfileCouchDB() {
		super();
	}

	public String get_rev() {
		return _rev;
	}

	public void set_rev(String _rev) {
		this._rev = _rev;
	}
	
	public void setDetails(Profile profile) {
		this._id = profile.get_id();
		this.lastProfileChange = profile.getLastProfileChange();
		this.lastProfileContact = profile.getLastProfileContact();
		this.preferences = profile.getPreferences();
	}

}
