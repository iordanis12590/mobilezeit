package org.wahlzeit.api;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;


import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoId;
import org.wahlzeit.model.PhotoManager;
import org.wahlzeit.model.PhotoSize;
import org.wahlzeit.model.UserManager;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.users.User;

@Api(name="wahlzeitApi",
version = "v1",
description = "A multiclient API for Whalzeit"
)
public class PhotosEndpoint {
	
	//TODO: add cursor and limit parameters to limit transmitted data
	// list all photos
	@ApiMethod(name="photos", httpMethod="get", path="photos/")
	public Collection<Photo> listAllPhotos() {
		Collection<Photo> result;
		PhotoManager photoManager = PhotoManager.getInstance();
		result = photoManager.getPhotoCache().values();
		
		boolean b = result.isEmpty();
		System.out.println(b);
		
		return result;
	}
	
//	// add a new photo to the list
//	@ApiMethod(name="photos.add", httpMethod="post")
//	public Photo addPhoto(/*User user,*/ @Named("photo") Photo photo) {
//		
//		return null;
//	}
	
	// delete photo
	@ApiMethod(name = "photos.delete", httpMethod="delete", path="photos/{photoId}")
	public Photo delete(@Named("photoId") String photoIdAsString) {
		PhotoId photoId = PhotoId.getIdFromString(photoIdAsString);
		Photo photo = PhotoManager.getInstance().getPhotoFromId(photoId);
		if(photo != null) {
			UserManager userManager = UserManager.getInstance();
			org.wahlzeit.model.User photoOwner = userManager.getUserById(photo.getOwnerId());
			photo.setStatus(photo.getStatus().asDeleted(true));
			PhotoManager.getInstance().savePhoto(photo);
			if (photoOwner.getUserPhoto() == photo) {
				photoOwner.setUserPhoto(null);
				userManager.saveClient(photoOwner);
			}
		}
		return photo;
	}
	
	
	// returns all images (of different sizes) from a photo
	@ApiMethod(name="images", httpMethod="get", path="photos/{photoId}/images")
	public Collection<Image> listAllImages(@Named("photoId")String photoIdAsString) {
		PhotoManager photoManager = PhotoManager.getInstance();
		PhotoId photoId = PhotoId.getIdFromString(photoIdAsString);
		Photo photo = photoManager.getPhotoFromId(photoId);
		Collection<Image> result = new ArrayList<Image>();
		for(PhotoSize photoSize: PhotoSize.values()) {
			result.add(photo.getImage(photoSize));
		}
		return result;
	}
	
	// returns a photo's images, as defined defined in imageSizes
//	@ApiMethod(name="images.sizes", httpMethod="get", path="photos/{photoId}/images/size")
//	public Collection<Image> listAllImagesOfSize(@Named("photoId")String photoIdAsString, ImageSizes imageSizes) {
//		PhotoManager photoManager = PhotoManager.getInstance();
//		PhotoId photoId = PhotoId.getIdFromString(photoIdAsString);
//		Photo photo = photoManager.getPhotoFromId(photoId);			
//		Collection<Image> result = new ArrayList<Image>();
//		if(imageSizes.getSizes().length != 0) {
//			for(String mySize: imageSizes.getSizes()) {
//				result.add(photo.getImage(PhotoSize.getFromString(mySize)));
//			}
//		}
//			
//		return null;
//	}
}

