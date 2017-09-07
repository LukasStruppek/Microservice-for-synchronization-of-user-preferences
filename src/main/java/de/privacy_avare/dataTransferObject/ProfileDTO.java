/**
 * Representation des Data Transfer Object für die Nutzerdaten. 
 * @author Lukas Struppek
 * @version 1.0
 */

package de.privacy_avare.dataTransferObject;

import java.util.Calendar;

public class ProfileDTO {
	private String id;
	private Calendar timestamp;
	private Calendar lastPrpfileContact;
	private Object profileData;
	
	/**
	 * @ return Getter für ProfileID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id Setter für ProfileID 
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public Calendar getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}
	public Calendar getLastPrpfileContact() {
		return lastPrpfileContact;
	}
	public void setLastPrpfileContact(Calendar lastPrpfileContact) {
		this.lastPrpfileContact = lastPrpfileContact;
	}
	public Object getProfileData() {
		return profileData;
	}
	public void setProfileData(Object profileData) {
		this.profileData = profileData;
	}
	
	

}
