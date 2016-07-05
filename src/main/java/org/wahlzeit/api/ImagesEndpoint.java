package org.wahlzeit.api;

import static com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nullable;

import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoId;
import org.wahlzeit.model.PhotoManager;
import org.wahlzeit.model.PhotoSize;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.images.Image;

@Api(name="wahlzeitApi",
	version = "v1",
	description = "A multiclient API for Whalzeit",
	clientIds = {
	    Constants.WEB_CLIENT_ID,
	    Constants.ANDROID_CLIENT_ID,
	    API_EXPLORER_CLIENT_ID },
	audiences = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID },
	scopes = {
	    "https://www.googleapis.com/auth/userinfo.email" }
	)
public class ImagesEndpoint {
	
	/**
	 *  returns all images of all sizes from a photo, or only the sizes defined
	 * @param photoIdAsString
	 * @return
	 */
	@ApiMethod(name="images", httpMethod="get", path="photos/{photoId}/images")
	public Collection<Image> listAllImages(com.google.appengine.api.users.User user, @Named("photoId")String photoIdAsString, @Named("imageSizes") @Nullable PhotoSize[] sizes) throws UnauthorizedException{
		if (user == null) throw new UnauthorizedException("Client application is not authorized");
		PhotoManager photoManager = PhotoManager.getInstance();
		PhotoId photoId = PhotoId.getIdFromString(photoIdAsString);
		Photo photo = photoManager.getPhotoFromId(photoId);
		Collection<Image> result = new ArrayList<Image>();
		// Check if the client requests only some particular sizes
		if(sizes != null) {
			for(PhotoSize size: sizes) {
				result.add(photo.getImage(size));
			}
		} else {
			// Return all available sizes
			for(PhotoSize photoSize: PhotoSize.values()) {
				result.add(photo.getImage(photoSize));
			}
		}
		return result;
	}
}
