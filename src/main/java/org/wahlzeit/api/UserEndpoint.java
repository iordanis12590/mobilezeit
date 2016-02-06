package org.wahlzeit.api;

import org.wahlzeit.model.Guest;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;

@Api(name="wahlzeitApi",
version = "v1",
description = "A multiclient API for Whalzeit"
)
public class UserEndpoint {
	
	private static final String APIGUEST_PREFIX = "api_guest#";
	
//	@ApiMethod(name = "guest.create")
//	public APIGuest createGuestUser() {
//		return new APIGuest();	
//	}
	
	@ApiMethod(name = "create_guest", httpMethod="post")
	public Guest createGuest(@Named("name") String name) {
		return new Guest();
	}
	
	
}
