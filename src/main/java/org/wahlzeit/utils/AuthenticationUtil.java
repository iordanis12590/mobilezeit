package org.wahlzeit.utils;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Preconditions;
//import com.google.api.services.plus.PlusScopes;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.wahlzeit.model.UserSession;

@SuppressWarnings("deprecation")
public class AuthenticationUtil {
	private static GoogleClientSecrets clientSecrets = null;
	private static final Set<String> SCOPES = new HashSet<String>(); //Collections.singleton(PlusScopes.PLUS_ME);
	public static final UrlFetchTransport HTTP_TRANSPORT = new UrlFetchTransport();
	public static final JacksonFactory JSON_FACTORY = new JacksonFactory();
	public static final String AUTH_CALLBACK_SERVLET_PATH = "/oauth2callback";
	public static String LOGIN_ACCESS_RIGHTS = "";
	
	public static GoogleClientSecrets getClientSecrets() throws IOException {		
	    if (clientSecrets == null) {
	      clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
	          new InputStreamReader(AuthenticationUtil.class.getResourceAsStream("/client_secret.json")));
	    }
	    return clientSecrets;
	}
	public static GoogleAuthorizationCodeFlow initializeFlow() throws IOException {	
		addScopes();
		return new GoogleAuthorizationCodeFlow.Builder(
			    HTTP_TRANSPORT, 
			    JSON_FACTORY,
			    getClientSecrets(), 
			    SCOPES)
				.build();
	}
	
	private static void addScopes() {
		SCOPES.add("https://www.googleapis.com/auth/userinfo.email");
		SCOPES.add("https://www.googleapis.com/auth/userinfo.profile");
		
	}

	public static String getRedirectUri(HttpServletRequest req) {
		GenericUrl requestUrl = new GenericUrl(req.getRequestURL().toString());
		requestUrl.setRawPath(AUTH_CALLBACK_SERVLET_PATH);
		return requestUrl.build();
	}
	
	public static String getRedirectUri(UserSession us) {
		GenericUrl requestUrl = new GenericUrl(us.getSiteUrl());
		requestUrl.setRawPath(AUTH_CALLBACK_SERVLET_PATH);
		return requestUrl.build();
	}

	public static String getUserId(HttpServletRequest req) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
	    return user.getUserId();
	}
	  
	public static void setLoginAccessRights(String loginAccessRights) {
		LOGIN_ACCESS_RIGHTS = loginAccessRights;
	}
	
	public static String getLoginAccessRights() {
		return LOGIN_ACCESS_RIGHTS;
	}
	
}
