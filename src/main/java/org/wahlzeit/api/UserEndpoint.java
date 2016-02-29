package org.wahlzeit.api;

import static com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID;

import org.wahlzeit.model.Administrator;
import org.wahlzeit.model.Client;
import org.wahlzeit.model.Guest;
import org.wahlzeit.model.User;
import org.wahlzeit.model.UserManager;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;


@Api(name="wahlzeitApi",
version = "v1",
description = "A multiclient API for Whalzeit"
)
public class UserEndpoint {
	
	private static final String APIGUEST_PREFIX = "api_guest#";
	
//	@ApiMethod(name = "guest.create")
//	public APIGuest createGuestUser() {
//		return new APIGuest();	
//	}
	
//	@ApiMethod(name = "guest", httpMethod="post")
//	public Guest createGuest(@Nullable @Named("name") String name) {
//		
//		Guest guest = new Guest();
//		guest.setLanguage(null);
//		if ((name != null) || (name == "")) {
//			guest.setNickName(name);
//		}
//		return guest;
//
//	}
	
	@ApiMethod(name="clients.create", 
			path = "clients/",
			clientIds = {
            Constants.WEB_CLIENT_ID,
            Constants.ANDROID_CLIENT_ID,
            API_EXPLORER_CLIENT_ID },
        audiences = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID },
        scopes = {
            "https://www.googleapis.com/auth/userinfo.email" })
	public Client createClient(com.google.appengine.api.users.User user, @Nullable Client wahlzeitClient) {
		Client result = null;
		UserManager um = UserManager.getInstance();
		if(user != null) {
			if (wahlzeitClient.getAccessRights().toString().equals("ADMINISTRATOR")) {
				result = (Administrator) um.getUserById(wahlzeitClient.getId());
				if(result == null) {
					result = new Administrator(wahlzeitClient.getId(), wahlzeitClient.getNickName(), user.getEmail());
				}
			} else if (wahlzeitClient.getAccessRights().toString().equals("USER")) {
				result = um.getUserById(wahlzeitClient.getId());
				if(result == null)  {
					result = new User(wahlzeitClient.getId(), wahlzeitClient.getNickName(), user.getEmail());
				}
			}
		} else {
			result = new Guest();
		}
		return result;
	}
	
	@ApiMethod(name="clients.administrators", 
			path = "clients/administrators/",
			clientIds = {
            Constants.WEB_CLIENT_ID,
            Constants.ANDROID_CLIENT_ID,
            API_EXPLORER_CLIENT_ID },
        audiences = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID },
        scopes = {
            "https://www.googleapis.com/auth/userinfo.email" })
	public Client createAdministrator(com.google.appengine.api.users.User user, @Nullable Administrator wahlzeitUser) {
		Administrator result = null;
		if(user != null) {
			UserManager um = UserManager.getInstance();
			result = (Administrator) um.getUserById(wahlzeitUser.getId());
			if(result == null && wahlzeitUser != null) {
				//um.addClient(wahlzeitUser);
				result = new Administrator(wahlzeitUser.getId(), wahlzeitUser.getNickName(), user.getEmail());
			}
			
		}
		return result;
	}
	
	@ApiMethod(name="clients.users", 
			path = "clients/users/",
			clientIds = {
            Constants.WEB_CLIENT_ID,
            Constants.ANDROID_CLIENT_ID,
            API_EXPLORER_CLIENT_ID },
        audiences = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID },
        scopes = {
            "https://www.googleapis.com/auth/userinfo.email" })
	public Client createUser(com.google.appengine.api.users.User user, @Nullable User wahlzeitUser) {
		User result = null;
		if(user != null) {
			UserManager um = UserManager.getInstance();
			result = um.getUserById(wahlzeitUser.getId());
			if(result == null && wahlzeitUser != null) {
				//um.addClient(wahlzeitUser);
				result = new User(wahlzeitUser.getId(), wahlzeitUser.getNickName(), user.getEmail());
			}
			
		}
		return result;
	}
	
	
	@ApiMethod(name="clients.guests", 
			path = "clients/guests/",
			clientIds = {
            Constants.WEB_CLIENT_ID,
            Constants.ANDROID_CLIENT_ID,
            API_EXPLORER_CLIENT_ID },
        audiences = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID },
        scopes = {
            "https://www.googleapis.com/auth/userinfo.email" })
	public Client createGuest(com.google.appengine.api.users.User user, @Nullable Guest wahlzeitGuest) {
		org.wahlzeit.model.Guest result = null; 		
		UserManager um = UserManager.getInstance();
		result = new Guest();
		
		return result;
	}
	
//	@ApiMethod(name="clients.guests", 
//			path = "clients/guests/",
//			httpMethod = HttpMethod.GET
//			 )
//	public Client createGuest() {
//		org.wahlzeit.model.Guest result = null;
//		
//		UserManager um = UserManager.getInstance();
//		result = new Guest();
//		
//		return result;
//	}
	
//	@ApiMethod(name = "createGuest")
//	public APIGuest createGuestUser() {
//		return new APIGuest();	
//	}
	
}
