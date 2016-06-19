package org.wahlzeit.api;

import static com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

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
import com.google.api.server.spi.config.Nullable;


@Api(name="wahlzeitApi",
	version = "v1",
	description = "A multiclient API for Whalzeit",
	clientIds = {
	        Constants.WEB_CLIENT_ID,
	        Constants.ANDROID_CLIENT_ID,
	        API_EXPLORER_CLIENT_ID },
	    audiences = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID },
	    scopes = {
        "https://www.googleapis.com/auth/userinfo.email" }
)
public class UserEndpoint {
		
	private static final Logger log = Logger.getLogger(UserEndpoint.class.getName());
	
	/**
	 * Endpoint method that updates a client
	 * @param user Injected type for authentication
	 * @param req Injected type to determine the servers URI path
	 * @param wahlzeitClient The client to be updated
	 * @return
	 */
	@ApiMethod(name="clients.update")
	public Client updateClient(com.google.appengine.api.users.User user, HttpServletRequest req, @Nullable Client wahlzeitClient) {
		UserManager userManager = UserManager.getInstance();
		User actualWahlzeitClient = userManager.getUserById(wahlzeitClient.getId());
		
		String nickName = wahlzeitClient.getNickName();
		String gender = wahlzeitClient.getGender().toString();
		String language = wahlzeitClient.getLanguage().asString();
		boolean notify =  wahlzeitClient.getNotifyAboutPraise();
		
		actualWahlzeitClient.setNotifyAboutPraise(notify);
		
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
	
	/**
	 * Endpoint method that registers a new client or returns the relevant client if he has been registered before.
	 * @param user
	 * @param req
	 * @param wahlzeitClient
	 * @return
	 */
	@ApiMethod(name="clients.create")
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
	
	/**
	 * A method that either creates a new Client instance or return an existing one by checking and switching 
	 * its access rights accordingly.
	 * @param createAdmin	A boolean value to determine whether to create a user with Administrator access rights or not
	 * @param userId	The user's Google ID
	 * @param userName	The user's name and surname
	 * @param email	The user's email address
	 * @return
	 */
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
