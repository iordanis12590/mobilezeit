package org.wahlzeit.api;

import static com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID;

import org.wahlzeit.model.Administrator;
import org.wahlzeit.model.Client;
import org.wahlzeit.model.Guest;
import org.wahlzeit.model.User;
import org.wahlzeit.model.UserManager;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
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
	
	@ApiMethod(name="clients.administrators", 
			path = "clients/administrators/",
			clientIds = {
            Constants.WEB_CLIENT_ID,
            Constants.ANDROID_CLIENT_ID,
            API_EXPLORER_CLIENT_ID },
        audiences = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID },
        scopes = {
            "https://www.googleapis.com/auth/userinfo.email" })
	public Administrator createAdministrator(com.google.appengine.api.users.User user, @Nullable Administrator wahlzeitUser) {
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
	public User createUser(com.google.appengine.api.users.User user, @Nullable User wahlzeitUser) {
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
			httpMethod="post",
			path = "clients/guests/")
	public Guest createGuest() {
		org.wahlzeit.model.Guest result = null;
		
		UserManager um = UserManager.getInstance();
		result = new Guest();
		
		return result;
	}
	
//	@ApiMethod(name = "createGuest")
//	public APIGuest createGuestUser() {
//		return new APIGuest();	
//	}
	
}
