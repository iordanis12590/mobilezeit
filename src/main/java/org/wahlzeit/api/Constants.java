package org.wahlzeit.api;

/**
 * A class that holds all string values of the client ids that are necessary for the application.
 * The API implementation and the Client need to be aware of these keys
 * so that the Client can be identified with the Server
 * @author iordanis
 *
 */
public class Constants {
	public static final String WEB_CLIENT_ID = "326325117092-mpckcvum5182l7udgu6sq5du1ivmeo6a.apps.googleusercontent.com";
	public static final String ANDROID_CLIENT_ID = "326325117092-rajdm7mb9dvl4ed60kg2qell98jbdgsn.apps.googleusercontent.com";
	public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;
	public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
	
	
}
