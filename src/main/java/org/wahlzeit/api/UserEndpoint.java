package org.wahlzeit.api;

import static com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.wahlzeit.handlers.EditUserProfileFormHandler;
import org.wahlzeit.handlers.PartUtil;
import org.wahlzeit.model.AccessRights;
import org.wahlzeit.model.Administrator;
import org.wahlzeit.model.Client;
import org.wahlzeit.model.Gender;
import org.wahlzeit.model.Guest;
import org.wahlzeit.model.User;
import org.wahlzeit.model.UserManager;
import org.wahlzeit.services.Language;
import org.wahlzeit.services.LogBuilder;
import org.wahlzeit.utils.StringUtil;

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
		
	private static final Logger log = Logger.getLogger(UserEndpoint.class.getName());
	
	@ApiMethod(name="clients.update", 
			clientIds = {
            Constants.WEB_CLIENT_ID,
            Constants.ANDROID_CLIENT_ID,
            API_EXPLORER_CLIENT_ID },
        audiences = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID },
        scopes = {
            "https://www.googleapis.com/auth/userinfo.email" })
	public Client updateClient(com.google.appengine.api.users.User user, HttpServletRequest req, @Nullable User wahlzeitClient) {
		// Downcast client to user or admin
//		Client modifiedWahlzeitClient;
//		if(wahlzeitClient.getAccessRights().toString().equals("ADMINISTRATOR")) {
//			modifiedWahlzeitClient = (Administrator) wahlzeitClient;
//		} else {
//			modifiedWahlzeitClient = (User) wahlzeitClient;
//		}
		
		UserManager userManager = UserManager.getInstance();
		User actualWahlzeitClient = userManager.getUserById(wahlzeitClient.getId());
		
		String nickName = wahlzeitClient.getNickName();
		String gender = wahlzeitClient.getGender().toString();
		String language = wahlzeitClient.getLanguage().asString();
		boolean notify =  wahlzeitClient.getNotifyAboutPraise();
		
		wahlzeitClient.setNotifyAboutPraise(notify);
		
		try {
			if (!nickName.equals(actualWahlzeitClient.getNickName())) {
				actualWahlzeitClient.setNickName(nickName);
			}
		} catch (IllegalArgumentException e) {
			return actualWahlzeitClient;
		}
		
		if (!StringUtil.isNullOrEmptyString(language)) {
			Language langValue = Language.getFromString(language);
			actualWahlzeitClient.setLanguage(langValue);
			log.info(LogBuilder.createUserMessage().
					addParameter("Language", langValue.asString()).toString());
		}

		if (!StringUtil.isNullOrEmptyString(gender)) {
			actualWahlzeitClient.setGender(Gender.getFromString(gender.toLowerCase()));
			log.info(LogBuilder.createUserMessage().
					addParameter("Gender", gender).toString());
		}
		
		userManager.saveClient(actualWahlzeitClient);
		return actualWahlzeitClient;
	}
	
	@ApiMethod(name="clients.create", 
			path = "clients/",
			clientIds = {
            Constants.WEB_CLIENT_ID,
            Constants.ANDROID_CLIENT_ID,
            API_EXPLORER_CLIENT_ID },
        audiences = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID },
        scopes = {
            "https://www.googleapis.com/auth/userinfo.email" })
	public Client createClient(com.google.appengine.api.users.User user, HttpServletRequest req, @Nullable Client wahlzeitClient) {
		Client result = null;
		String uriPrefix = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/";
		if(user != null) {
			if (wahlzeitClient.getAccessRights().toString().equals("ADMINISTRATOR")) {
				result = createAuthorizedClient(true, wahlzeitClient.getId(), wahlzeitClient.getNickName(), user.getEmail());
			} else if (wahlzeitClient.getAccessRights().toString().equals("USER")) {
				result = createAuthorizedClient(false, wahlzeitClient.getId(), wahlzeitClient.getNickName(), user.getEmail());
			}
		} else {
			result = new Guest();
		}
		result.setResourceId(uriPrefix);
		return result;
	}
	
	private Client createAuthorizedClient(boolean createAdmin, String userId, String userName, String email) {
		Client result = null;
		UserManager um = UserManager.getInstance();
		result = um.getUserById(userId);
		if (result == null) {
			result = createAdmin ? new Administrator(userId, userName, email) : new User(userId, userName, email);
		} else {
			//switch user rights
			result.setAccessRights(createAdmin ? AccessRights.ADMINISTRATOR : AccessRights.USER);
		}
		return result;
	}
	
}
