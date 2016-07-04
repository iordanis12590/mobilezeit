package org.wahlzeit.api;

import java.util.List;

import static com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nullable;

import org.wahlzeit.model.Client;
import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoFilter;
import org.wahlzeit.model.PhotoId;
import org.wahlzeit.model.PhotoManager;
import org.wahlzeit.model.PhotoSize;
import org.wahlzeit.model.PhotoStatus;
import org.wahlzeit.model.Tags;
import org.wahlzeit.model.User;
import org.wahlzeit.model.UserManager;
import org.wahlzeit.services.OfyService;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.googlecode.objectify.cmd.Query;

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
public class PhotosEndpoint {
		
	/**
	 * Retrieves photos.
	 * Photos can be either filtered by tag (filter), or by the user they belong to (fromClient). 
	 * 
	 * In addition a paging mechanism allows retrieving only a limited amount of photos, in each request. 
	 * 
	 * @param cursorString	The next page indicator token, used when paging photos
	 * @param limit			The maximum amount of photos to retrieve
	 * @param fromClient	The owner which photos' should be retrieved		
	 * @param filter		The tags 
	 * @return
	 */
	@ApiMethod(name="photos.list",
			path="photos/")
	public CollectionResponse<Photo> listPhoto(
			com.google.appengine.api.users.User user,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit,
			@Nullable @Named("fromClient") String clientId,
			@Nullable @Named("filter") String filter) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("Client application is not authorized");
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
	

	/**
	 * Creates and adds a new photo to the collection
	 * @param photo	Photo to be uploaded
	 * @return
	 */
	@ApiMethod(name="photos.upload", 
			path="photos/")
	public Photo createPhoto(com.google.appengine.api.users.User authenticatedUser, Photo photo) throws UnauthorizedException {
		if (authenticatedUser == null) throw new UnauthorizedException("Client application is not authorized");
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
	
	@ApiMethod(name="photos.get",
			path="photos/{photoId}")
	public Photo getIndividualPhoto(com.google.appengine.api.users.User authenticatedUser, @Named("photoId") String photoIdAsString) throws UnauthorizedException {
		if (authenticatedUser == null) throw new UnauthorizedException("Client application is not authorized");
		PhotoId photoId = PhotoId.getIdFromString(photoIdAsString);
		Photo result = PhotoManager.getInstance().getPhotoFromId(photoId);
		return result;
	}
	
	/**
	 * Updates photos tags and visibility settings
	 * @param photo:	Photo to update
	 * @return: The updates photo
	 */
	@ApiMethod(name="photos.update",
			path="photos/{photoId}")
	public Photo updatePhoto(com.google.appengine.api.users.User authenticatedUser, @Named("photoId") String photoIdAsString, Photo photo) throws UnauthorizedException {
		if (authenticatedUser == null) throw new UnauthorizedException("Client application is not authorized");
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
	
	/**
	 * Deletes a photo
	 * @param authenticatedUser
	 * @param photo: The photo to be deleted
	 * @return
	 * @throws UnauthorizedException
	 * 
	 * @BUG: If the HttpMethod is specified (DELETE), or the method is renamed to delete, remove etc, The method won't work 
	 * as there will be some bug in the generated client stubs causing an exception:
	 * IllegalArgumentException: DELETE with non-zero content length is not supported
	 */
	@ApiMethod(name="photos.erase")
	public Photo erasePhoto(com.google.appengine.api.users.User authenticatedUser, Photo photo) throws UnauthorizedException {
		if (authenticatedUser == null) throw new UnauthorizedException("Client application is not authorized");
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
	
	/**
	 * Praises a single photo
	 * @param authenticatedUser
	 * @param photo:	Photo to be praised
	 * @return Return the photo after calculating the new rating average
	 * @throws UnauthorizedException
	 */
	@ApiMethod(name="photos.praise",
			path="photos/{photoId}/praising")
	public Photo praisePhoto(com.google.appengine.api.users.User authenticatedUser, @Named("photoId") String photoIdAsString, Photo photo) throws UnauthorizedException {
		if (authenticatedUser == null) throw new UnauthorizedException("Client application is not authorized");
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
	
	/**
	 * Adds a photo to the client's skipped photos
	 * @param authenticatedUser
	 * @param photo: Photo to skip
	 * @return
	 * @throws UnauthorizedException
	 */
	@ApiMethod(name="photos.skip",
			path="photos/{photoId}/skipping")
	public Photo skipPhoto(com.google.appengine.api.users.User authenticatedUser, @Named("photoId") String id, Photo photo) throws UnauthorizedException{
		if (authenticatedUser == null) throw new UnauthorizedException("Client application is not authorized");
		PhotoId photoId = PhotoId.getIdFromString(photo.getIdAsString());
		Photo result = PhotoManager.getInstance().getPhotoFromId(photoId);
		Client client = UserManager.getInstance().getClientById(photo.getPraisingClientId());
		client.addSkippedPhotoId(photoId);
		return result;
	}

}

