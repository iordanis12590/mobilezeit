package org.wahlzeit.api;

import java.util.List;
import java.util.Set;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import org.wahlzeit.agents.AsyncTaskExecutor;
import org.wahlzeit.model.Client;
import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoFilter;
import org.wahlzeit.model.PhotoId;
import org.wahlzeit.model.PhotoManager;
import org.wahlzeit.model.PhotoSize;
import org.wahlzeit.model.PhotoStatus;
import org.wahlzeit.model.Tags;
import org.wahlzeit.model.UserManager;
import org.wahlzeit.services.OfyService;
import org.wahlzeit.model.User;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow.Builder;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.googlecode.objectify.cmd.Query;

@Api(name="wahlzeitApi",
version = "v1",
description = "A multiclient API for Whalzeit"
)
public class PhotosEndpoint {
		
	@ApiMethod(name="photos.pagination.list")
	public CollectionResponse<Photo> listPhoto(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit,
			@Nullable @Named("fromClient") String clientId,
			@Nullable @Named("filter") String filter) {
		
		Query<Photo> query = OfyService.ofy().load().type(Photo.class);
		Cursor cursor = null;
		List<Photo> photosList = new ArrayList<Photo>();	
		if(filter != null) {
			Tags tags = new Tags(filter);
			PhotoFilter photoFilter = new PhotoFilter();
			photoFilter.setTags(tags);
			photoFilter.generateDisplayablePhotoIds();
			List<PhotoId> filteredPhotoIds = photoFilter.getDisplayablePhotoIds();
			List<String> filteredPhotoIdsAsString = new ArrayList<String>();
			for(PhotoId photoId: filteredPhotoIds) {
				filteredPhotoIdsAsString.add(photoId.getStringValue());
			}
			if(!filteredPhotoIds.isEmpty()) {
				query = query.filter("id.stringValue in", filteredPhotoIdsAsString);				
			}
		}
		if(clientId != null) {
			query = query.filter("ownerId", clientId);
		}
		if(cursorString != null) {
			cursor = Cursor.fromWebSafeString(cursorString);
			query = query.startAt(cursor);
		}
		if(limit != null) {
			query = query.limit(limit);
		}
		QueryResultIterator<Photo> iterator = query.iterator();
		while(iterator.hasNext()) {
			Photo photo = iterator.next();
			photosList.add(photo);
			cursor = iterator.getCursor();
		}
		CollectionResponse.Builder<Photo> result = CollectionResponse.<Photo>builder().setItems(photosList);
		if (cursor != null) {
			result.setNextPageToken(cursor.toWebSafeString());
		}
		return result.build();
	}
	
	@ApiMethod(name="photos.filter.list")
	public CollectionResponse<Photo> filterPhoto(@Nullable @Named("haha") String haha) {
		Query<Photo> query = OfyService.ofy().load().type(Photo.class);
		Collection<Photo> result;
		if(haha != null) {
			Tags tags = new Tags(haha);
			PhotoFilter photoFilter = new PhotoFilter();
			photoFilter.setTags(tags);
			photoFilter.generateDisplayablePhotoIds();
			List<PhotoId> filteredPhotoIds = photoFilter.getDisplayablePhotoIds();
			List<String> filteredPhotoIdsAsString = new ArrayList<String>();
			for(PhotoId photoId: filteredPhotoIds) {
				filteredPhotoIdsAsString.add(photoId.getStringValue());
			}
			if(!filteredPhotoIds.isEmpty()) {
				query = query.filter("id.stringValue in", filteredPhotoIdsAsString);				
			}
		}
		result = query.list();
		CollectionResponse.Builder<Photo> collectionResponse = CollectionResponse.<Photo>builder().setItems(result);
		return collectionResponse.build();
	}
	
	@ApiMethod(name="photos.upload", path="photos/")
	public Photo createPhoto(Photo photo) {
		Photo result = null;
        byte[] decodedString = photo.decodeBlobImage();
        Image image = ImagesServiceFactory.makeImage(decodedString);
		PhotoManager pm = PhotoManager.getInstance();
		UserManager um = UserManager.getInstance();
		User user = um.getUserById(photo.getOwnerId());
		try {
			result = pm.createPhoto(photo.getEnding(), image);
			user.addPhoto(result);
			result.setTags(photo.getTags());
			pm.savePhoto(result);
		} catch (Exception e) {
			
		}
		return result;
	}
	
	@ApiMethod(name="photos.update")
	public Photo updatePhoto(Photo photo) {
		PhotoId photoId = PhotoId.getIdFromString(photo.getIdAsString());
		Photo result = PhotoManager.getInstance().getPhotoFromId(photoId);
		UserManager userManager = UserManager.getInstance();
		User photoOwner = userManager.getUserById(photo.getOwnerId());
		if(photo != null) {
			PhotoStatus status = photo.getStatus();
			boolean isVisible = status.isInvisible();
			result.setStatus(photo.getStatus().asInvisible(isVisible));
			result.setTags(photo.getTags());
			PhotoManager.getInstance().savePhoto(result);
			if (photoOwner.getUserPhoto() == photo) {
				photoOwner.setUserPhoto(null);
				userManager.saveClient(photoOwner);
			}
		}
		return result;
	}

	@ApiMethod(name="photos.praise")
	public Photo praisePhoto(Photo photo){
		PhotoManager pm = PhotoManager.getInstance();
		PhotoId photoId = PhotoId.getIdFromString(photo.getIdAsString());
		Photo result = pm.getPhotoFromId(photoId);
		Client client = UserManager.getInstance().getClientById(photo.getPraisingClientId());
		int rating = photo.getRating();
		result.addToPraise(rating);
		client.addPraisedPhotoId(result.getId());
		pm.savePhoto(result);
		return result;
	}
	
	@ApiMethod(name="photos.skip")
	public Photo skipPhoto(Photo photo){
		PhotoId photoId = PhotoId.getIdFromString(photo.getIdAsString());
		Photo result = PhotoManager.getInstance().getPhotoFromId(photoId);
		Client client = UserManager.getInstance().getClientById(photo.getPraisingClientId());
		client.addSkippedPhotoId(photoId);
		return result;
	}
	
	@ApiMethod(name="photos.setStatusAsDeleted")
	public Photo setStatusAsDeleted(Photo photo){
		PhotoId photoId = PhotoId.getIdFromString(photo.getIdAsString());
		Photo result = PhotoManager.getInstance().getPhotoFromId(photoId);
		if(result != null) {
			UserManager userManager = UserManager.getInstance();
			org.wahlzeit.model.User photoOwner = userManager.getUserById(result.getOwnerId());
			result.setStatus(result.getStatus().asDeleted(true));
			PhotoManager.getInstance().savePhoto(result);
			if (photoOwner.getUserPhoto() == result) {
				photoOwner.setUserPhoto(null);
				userManager.saveClient(photoOwner);
			}
		}
		return result;
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

}

