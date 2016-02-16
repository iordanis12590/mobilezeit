package org.wahlzeit.servlets;

import java.io.IOException;

import org.wahlzeit.handlers.PartUtil;
import org.wahlzeit.utils.AuthenticationUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

public class AuthCallbackServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		      throws IOException, ServletException {
		// This implementation does not handle the user denying authorization.
	    GoogleAuthorizationCodeFlow authFlow = AuthenticationUtil.initializeFlow();
	    // Exchange authorization code for user credentials.
	    GoogleTokenResponse tokenResponse = authFlow.newTokenRequest(req.getParameter("code"))
	        .setRedirectUri(AuthenticationUtil.getRedirectUri(req)).execute();
	    // Save the credentials for this user so we can access them from elsewhere.
	    authFlow.createAndStoreCredential(tokenResponse, AuthenticationUtil.getUserId(req));
	    resp.sendRedirect(PartUtil.SHOW_USER_HOME_PAGE_NAME);
		
	}
}
