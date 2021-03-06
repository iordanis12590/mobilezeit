package org.wahlzeit.handlers;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import org.wahlzeit.model.AccessRights;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.services.LogBuilder;
import org.wahlzeit.utils.AuthenticationUtil;
import org.wahlzeit.webparts.WebPart;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * When a user signs in with its Google account, this handler assures that a Wahlzeit user exists for the Google user.
 * If not {@link LoginFormHandler} creates one.
 */
public class LoginFormHandler extends AbstractWebFormHandler {

	public static final String ACCESS_RIGHTS = "accessRights";
	
	private static final Logger log = Logger.getLogger(LoginFormHandler.class.getName());

	public LoginFormHandler() {
		initialize(PartUtil.LOGIN_FORM_FILE, AccessRights.GUEST);
	}

	@Override
	protected void doMakeWebPart(UserSession us, WebPart part) {
		// do nothing as there is no page that should be displayed
		log.info("doMakeWebPart");
	}

	protected String doHandlePost(UserSession us, Map args) {
		String accessRights = us.getAndSaveAsString(args, ACCESS_RIGHTS);
		AuthenticationUtil.setLoginAccessRights(accessRights);
		
		log.info(LogBuilder.createUserMessage().
				addAction("Logging in").
				addParameter("Selected access rights", accessRights).toString());
		
		// This implementation does not handle the user denying authorization.
		try {
		    GoogleAuthorizationCodeFlow authFlow = AuthenticationUtil.initializeFlow();
		    //  redirect to authorization screen.
		    String redirectURL = authFlow.newAuthorizationUrl().setRedirectUri(AuthenticationUtil.getRedirectUri(us)).build();
		    return redirectURL;
		}catch (IOException e){
			
		}
	    return "";
	    
	}
	
	/**
	 * Called when a new user logged in. Make sure that a Wahlzeit user exist.
	 */
//	@Override
//	protected String doHandleGet(UserSession us, String link, Map args) {
//		log.info("Link: " + link);
//		
//		UserService userService = UserServiceFactory.getUserService();
//		com.google.appengine.api.users.User googleUser = userService.getCurrentUser();
//
//		if (googleUser != null) {
//			// googleUser logged in
//			log.config(LogBuilder.createSystemMessage().
//					addMessage("Google user exists").
//					addParameter("E-Mail", googleUser.getEmail()).toString());
//			String userId = googleUser.getUserId();
//			UserManager userManager = UserManager.getInstance();
//			User user = userManager.getUserById(userId);
//			if (user != null) {
//				// Wahlzeit user already exists
//				us.setClient(user);
//				log.config(LogBuilder.createSystemMessage().
//						addMessage("Wahlzeit user exists").
//						addParameter("id", user.getId()).toString());
//			} else {
//				// create new Wahlzeit user
//				String emailAddress = googleUser.getEmail();
//				String nickName = googleUser.getNickname();
//
//				Client previousClient = us.getClient();
//				if (userService.isUserAdmin()) {
//					user = new Administrator(userId, nickName, emailAddress, previousClient);
//				} else {
//					user = new User(userId, nickName, emailAddress, previousClient);
//				}
//				userManager.emailWelcomeMessage(us, user);
//				us.setClient(user);
//
//				log.info(LogBuilder.createUserMessage().addAction("Signup").toString());
//			}
//
////              TODO
////            if (user.getStatus().isDisabled()) {
////                us.setMessage(us.getConfiguration().getUserIsDisabled());
////                return PartUtil.LOGIN_PAGE_NAME;
////            }
//
//			return PartUtil.SHOW_USER_HOME_PAGE_NAME;
//		} else {
//			// googleUser not logged in
//			return PartUtil.SHOW_NOTE_PAGE_NAME;
//		}
//	}

}
