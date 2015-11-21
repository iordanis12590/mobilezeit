package org.wahlzeit.api;

import com.google.api.server.spi.config.Api;

import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;

import org.wahlzeit.api.About;


@Api(name="aboutapi",
	version = "v1",
	description = "An API to get the about text"
	)
public class AboutAPI {
	
	private static String defaultAboutText = "This site lets you show, discuss, and praise photos. You can upload your own photos and you can search for specific photos.";
	private static About defaultAbout = new About(defaultAboutText);
	
	
	@ApiMethod(name="get")
	public About getDefaultAbout() {
		return defaultAbout;
	}
}
