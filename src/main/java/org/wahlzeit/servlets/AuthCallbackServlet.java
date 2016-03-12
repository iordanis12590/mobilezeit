package org.wahlzeit.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import org.wahlzeit.handlers.LoginFormHandler;
import org.wahlzeit.handlers.PartUtil;
import org.wahlzeit.model.AccessRights;
import org.wahlzeit.model.Administrator;
import org.wahlzeit.model.Client;
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
	    GoogleTokenResponse tokenResponse;
	    try {
		    tokenResponse = authFlow.newTokenRequest(req.getParameter("code")).setRedirectUri(AuthenticationUtil.getRedirectUri(req)).execute();
	    } catch (NullPointerException e){
	    	// user denied access
	    	redirectRequest(resp, PartUtil.LOGIN_PAGE_NAME);
	    	return;
	    }
		
	    Credential credential = authFlow.createAndStoreCredential(tokenResponse, null);	    
	    Oauth2 oauth2 = new Oauth2.Builder(AuthenticationUtil.HTTP_TRANSPORT, AuthenticationUtil.JSON_FACTORY, credential).setApplicationName(
	    		"iordanis-mobilezeit").build();

	    Userinfoplus person =  oauth2.userinfo().get().execute();
	    UserSession us = ensureUserSession(req);
	    if(person != null) {
		    setupWahlzeitClient(us, person); 
	    } else {
	    	redirectRequest(resp, PartUtil.LOGIN_PAGE_NAME);
	    }
	    resp.sendRedirect(PartUtil.SHOW_USER_HOME_PAGE_NAME);
	}
	
	private void setupWahlzeitClient(UserSession us, Userinfoplus personInfo) {
	    AccessRights accessRights = AccessRights.getFromString(AuthenticationUtil.LOGIN_ACCESS_RIGHTS);
    	log.config(LogBuilder.createSystemMessage().
				addMessage("Google user allowed access").
				addParameter("E-Mail", personInfo.getEmail()).toString());
    	String userId = personInfo.getId();
    	UserManager userManager = UserManager.getInstance();
    	User wahlzeitUser = userManager.getUserById(userId);
    	if (wahlzeitUser != null) {
    		//Wahlzeit user already exists
    		//Switch access rights
    		wahlzeitUser.setAccessRights(accessRights);
    		us.setClient(wahlzeitUser);
    		log.config(LogBuilder.createSystemMessage().
					addMessage("Wahlzeit user exists").
					addParameter("id", wahlzeitUser.getId()).toString());
    	} else {
    	    String email = personInfo.getEmail();
    	    String name = personInfo.getName();
    	    Client previousClient = us.getClient();
    	    if (accessRights.equals(AccessRights.USER)) {
    	    	wahlzeitUser = new User(userId, name, email, previousClient);
    	    } else {
    	    	wahlzeitUser = new Administrator(userId, name, email, previousClient);
    	    }
    	    wahlzeitUser.setResourceId(us.getSiteUrl());
    	    //TODO set more attributes: gender, profile pic etc.
    	    userManager.emailWelcomeMessage(us, wahlzeitUser);
    	    us.setClient(wahlzeitUser);
			log.info(LogBuilder.createUserMessage().addAction("Signup").toString());
    	}
	}
}
