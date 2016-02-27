package org.wahlzeit.api;

import com.google.api.server.spi.config.Api;

import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.users.User;

import static com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID;

import org.wahlzeit.api.About;


@Api(name="wahlzeitApi",
	version = "v1",
	description = "A multiclient API for Whalzeit"
	)
public class AboutEndpoint {
	
	private static String defaultAboutText = "This site lets you show, discuss, and praise photos. You can upload your own photos and you can search for specific photos.";
	private static About defaultAbout = new About(defaultAboutText);
	
	
	@ApiMethod(name="getAbout", clientIds = {
            Constants.WEB_CLIENT_ID,
            Constants.ANDROID_CLIENT_ID,
            API_EXPLORER_CLIENT_ID },
        audiences = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID },
        scopes = {
            "https://www.googleapis.com/auth/userinfo.email" })
	public About getDefaultAbout(User user) {
		if (user == null) {
			return new About("Hello unknown");
		} else {
			return defaultAbout;
		}
	}
}
