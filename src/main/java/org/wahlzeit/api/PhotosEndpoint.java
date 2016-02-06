package org.wahlzeit.api;

import java.util.ArrayList;
import java.util.Collection;


import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoManager;
import org.wahlzeit.model.PhotoSize;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.images.Image;

@Api(name="wahlzeitApi",
version = "v1",
description = "A multiclient API for Whalzeit"
)
public class PhotosEndpoint {

	@ApiMethod(name="list.photos", httpMethod="get")
	public Collection<Photo> listAllPhotos() {
		Collection<Photo> result;
		PhotoManager photoManager = PhotoManager.getInstance();
		result = photoManager.getPhotoCache().values();
		
		boolean b = result.isEmpty();
		System.out.println(b);
		
		Collection<Image> images = new ArrayList<Image>();
		
		for(Photo photo:result) {
			Image image = photo.getImage(PhotoSize.MEDIUM);
			images.add(image);
		}
		
		return result;
	}
	
	
}
