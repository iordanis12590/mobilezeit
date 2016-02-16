package org.wahlzeit.utils;

import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.appengine.auth.oauth2.AppEngineCredentialStore;
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

import org.wahlzeit.handlers.PartUtil;

@SuppressWarnings("deprecation")
public class AuthenticationUtil {
	private static GoogleClientSecrets clientSecrets = null;
	private static final Set<String> SCOPES = new HashSet<>();
	public static final UrlFetchTransport HTTP_TRANSPORT = new UrlFetchTransport();
	public static final JacksonFactory JSON_FACTORY = new JacksonFactory();
	public static final String MAIN_SERVLET_PATH = PartUtil.SHOW_USER_HOME_PAGE_NAME;
	public static final String AUTH_CALLBACK_SERVLET_PATH = "/oauth2callback";

	private static GoogleClientSecrets getClientSecrets() throws IOException {
		
		String str = System.getProperty("user.dir");
		
	    if (clientSecrets == null) {
	      clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
	          new InputStreamReader(AuthenticationUtil.class.getResourceAsStream(System.getProperty("user.dir") + "/client_secret.json")));
	    }
	    return clientSecrets;
	}
	public static GoogleAuthorizationCodeFlow initializeFlow() throws IOException {		
		SCOPES.add("https://www.googleapis.com/auth/userinfo.email");
		SCOPES.add("https://www.googleapis.com/auth/userinfo.profile");
		return new GoogleAuthorizationCodeFlow.Builder(
			    HTTP_TRANSPORT, 
			    JSON_FACTORY,
			    getClientSecrets(), 
			    SCOPES).setCredentialDataStore(StoredCredential.getDefaultDataStore(AppEngineDataStoreFactory.getDefaultInstance())).build();
		  		//setCredentialStore(StoredCredential.getDefaultDataStore(AppEngineDataStoreFactory.getDefaultInstance())).setAccessType("offline").build();
	}

	public static String getRedirectUri(HttpServletRequest req) {
		GenericUrl requestUrl = new GenericUrl(req.getRequestURL().toString());
		requestUrl.setRawPath(AUTH_CALLBACK_SERVLET_PATH);
		return requestUrl.build();
	}

	  public static String getUserId(HttpServletRequest req) {
	    UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
	    return user.getUserId();
	  }
}
