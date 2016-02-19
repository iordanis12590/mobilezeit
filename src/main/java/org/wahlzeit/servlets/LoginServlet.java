package org.wahlzeit.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wahlzeit.utils.AuthenticationUtil;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

public class LoginServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// This implementation does not handle the user denying authorization.
	    GoogleAuthorizationCodeFlow authFlow = AuthenticationUtil.initializeFlow();
	    
	    
	    //  redirect to authorization screen.
	    resp.sendRedirect(
	        authFlow.newAuthorizationUrl().setRedirectUri(AuthenticationUtil.getRedirectUri(req)).build());
	    return;
	}
}
