package org.wahlzeit.api;

import static com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID;

import org.wahlzeit.model.Client;
import org.wahlzeit.model.Guest;

import org.wahlzeit.model.UserManager;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.api.users.User;

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
	
	@ApiMethod(name = "guest", httpMethod="post")
	public Guest createGuest(@Nullable @Named("name") String name) {
		
		Guest guest = new Guest();
		guest.setLanguage(null);
		if ((name != null) || (name == "")) {
			guest.setNickName(name);
		}
		return guest;

	}
	
	
	@ApiMethod(name="user", httpMethod="post", clientIds = {
            Constants.WEB_CLIENT_ID,
            Constants.ANDROID_CLIENT_ID,
            API_EXPLORER_CLIENT_ID },
        audiences = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID },
        scopes = {
            "https://www.googleapis.com/auth/userinfo.email"})
	public org.wahlzeit.model.User createUser(User user) {
		UserManager um = UserManager.getInstance();
		org.wahlzeit.model.User result = null;
		if (user != null) {
			// user already exists
			org.wahlzeit.model.User userByEmail = um.getUserByEmailAddress(user.getEmail());
			result = um.getUserById(user.getUserId());
			if (result == null) {
				// create new user
				result = new org.wahlzeit.model.User(user.getUserId(), user.getNickname(), user.getEmail());
			}
		}
		return result;
	}
	
//	@ApiMethod(name = "createGuest")
//	public APIGuest createGuestUser() {
//		return new APIGuest();	
//	}
	
}
