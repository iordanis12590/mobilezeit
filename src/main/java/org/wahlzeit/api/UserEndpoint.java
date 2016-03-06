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
		if(user != null) {
			if (wahlzeitClient.getAccessRights().toString().equals("ADMINISTRATOR")) {
				result = createAuthorizedClient(true, wahlzeitClient.getId(), wahlzeitClient.getNickName(), user.getEmail());
			} else if (wahlzeitClient.getAccessRights().toString().equals("USER")) {
				result = createAuthorizedClient(false, wahlzeitClient.getId(), wahlzeitClient.getNickName(), user.getEmail());
			}
		} else {
			result = new Guest();
		}
		return result;
	}
	
	private Client createAuthorizedClient(boolean createAdmin, String userId, String userName, String email) {
		Client result = null;
		UserManager um = UserManager.getInstance();
		result =  um.getUserById(userId);
		if (result == null && createAdmin) {
			result = new Administrator(userId, userName, email);
		} else {
			result = new User(userId, userName, email);
		}
		return result;
	}

	
}
