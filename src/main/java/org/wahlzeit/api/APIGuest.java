package org.wahlzeit.api;

import org.wahlzeit.model.AccessRights;
import org.wahlzeit.model.Client;
import org.wahlzeit.model.UserManager;
import org.wahlzeit.services.EmailAddress;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index = true)
public class APIGuest extends Client {	
	
	private static final String APIGUEST_PREFIX = "api_guest#";
	
	public APIGuest() {
		String userId = APIGUEST_PREFIX + UserManager.getInstance().getNextClientId();
		initialize(userId, userId, EmailAddress.EMPTY, AccessRights.GUEST, null);
	}
			
	
}
