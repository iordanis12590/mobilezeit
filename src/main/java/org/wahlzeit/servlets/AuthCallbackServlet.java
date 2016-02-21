package org.wahlzeit.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import org.wahlzeit.handlers.LoginFormHandler;
import org.wahlzeit.handlers.PartUtil;
import org.wahlzeit.model.Administrator;
import org.wahlzeit.model.Client;
import org.wahlzeit.model.Gender;
import org.wahlzeit.model.User;
import org.wahlzeit.model.UserManager;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.services.LogBuilder;
import org.wahlzeit.utils.AuthenticationUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;

public class AuthCallbackServlet extends AbstractServlet {
	
	private static final Logger log = Logger.getLogger(LoginFormHandler.class.getName());
	private static final long serialVersionUID = 1;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		      throws IOException, ServletException {
		
		// This implementation does not handle the user denying authorization.
	    GoogleAuthorizationCodeFlow authFlow = AuthenticationUtil.initializeFlow();
	    // Exchange authorization code for access and refresh tokens.
	    GoogleTokenResponse tokenResponse = authFlow.newTokenRequest(req.getParameter("code")).setRedirectUri(AuthenticationUtil.getRedirectUri(req)).execute();
		//new GoogleCredential().setAccessToken(tokenResponse.getAccessToken());
		
	    Credential credential = authFlow.createAndStoreCredential(tokenResponse, null);
	    
	    //Credential credential = authFlow.loadCredential(AuthenticationUtil.getUserId(req));
	    
	    Oauth2 oauth2 = new Oauth2.Builder(AuthenticationUtil.HTTP_TRANSPORT, AuthenticationUtil.JSON_FACTORY, credential).setApplicationName(
	    		"iordanis-mobilezeit").build();
	    

	    Userinfoplus person =  oauth2.userinfo().get().execute();	   
	    String userId = person.getId();
	    String email = person.getEmail();
	    String name = person.getName();
	    String locale = person.getLocale();
	    String gender = person.getGender();
	    
	    UserSession us = ensureUserSession(req);
	    
	    UserManager userManager = UserManager.getInstance();
	    User user = userManager.getUserById(userId);
	    
	    if (user != null) {
			// Wahlzeit user already exists
			us.setClient(user);
			log.config(LogBuilder.createSystemMessage().
					addMessage("Wahlzeit user exists").
					addParameter("id", user.getId()).toString());
	    } else {
	    	// Create new Wahlzeit user
	    	Client previousClient = us.getClient();
			
			user = new Administrator(userId, name, email, previousClient);
			
//			user.setGender(Gender.getFromString(gender));
			
			userManager.emailWelcomeMessage(us, user);
			us.setClient(user);

			log.info(LogBuilder.createUserMessage().addAction("Signup").toString());
	    }
	    	    
	    resp.sendRedirect(PartUtil.SHOW_USER_HOME_PAGE_NAME);
	}
}
