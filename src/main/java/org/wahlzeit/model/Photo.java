/*
 * Copyright (c) 2006-2009 by Dirk Riehle, http://dirkriehle.com
 *
 * This file is part of the Wahlzeit photo rating application.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package org.wahlzeit.model;

import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Base64;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.images.Image;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Parent;
import org.wahlzeit.services.DataObject;
import org.wahlzeit.services.EmailAddress;
import org.wahlzeit.services.Language;
import org.wahlzeit.services.ObjectManager;

import java.util.Map;

/**
 * A photo represents a user-provided (uploaded) photo.
 */
@Entity
@Index
public class Photo extends DataObject {

	/**
	 *
	 */
	public static final String IMAGE = "image";
	public static final String THUMB = "thumb";
	public static final String LINK = "link";
	public static final String PRAISE = "praise";
	public static final String NO_VOTES = "noVotes";
	public static final String CAPTION = "caption";
	public static final String DESCRIPTION = "description";
	public static final String KEYWORDS = "keywords";

	public static final String TAGS = "tags";
	public static final String OWNER_ID = "ownerId";

	public static final String STATUS = "status";
	public static final String IS_INVISIBLE = "isInvisible";
	public static final String UPLOADED_ON = "uploadedOn";

	/**
	 *
	 */
	public static final int MAX_PHOTO_WIDTH = 420;
	public static final int MAX_PHOTO_HEIGHT = 600;
	public static final int MAX_THUMB_PHOTO_WIDTH = 105;
	public static final int MAX_THUMB_PHOTO_HEIGHT = 150;

	@Index
	protected PhotoId id = null;
	
	/**
	 *
	 */
	@Index
	protected String ownerId;
	
	/**
	 * Each photo can be viewed in different sizes (XS, S, M, L, XL)
	 * Images are pre-computed in these sizes to optimize bandwidth when requested.
	 */
	@Ignore
	transient protected Map<PhotoSize, Image> images = new ArrayMap<PhotoSize, Image>();
	@Ignore
	String blobImage;
	@Ignore
	String resourceId;
	@Ignore
	String imagesURI;
	@Ignore
	String ratingURI;

	/**
	 *
	 */
	protected boolean ownerNotifyAboutPraise = false;
	protected EmailAddress ownerEmailAddress = EmailAddress.EMPTY;
	protected Language ownerLanguage = Language.ENGLISH;
	protected String ownerNickName;
	@Ignore
	protected String praisingClientId;

	/**
	 *
	 */
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	protected int width;
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	protected int height;
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	protected PhotoSize maxPhotoSize = PhotoSize.MEDIUM; // derived
	
	/**
	 *
	 */
	protected Tags tags = Tags.EMPTY_TAGS;
	
	/**
	 *
	 */
	@Index
	protected PhotoStatus status = PhotoStatus.VISIBLE;
	
	/**
	 *
	 */
	protected int praiseSum = 10;
	protected int noVotes = 1;
	protected int noVotesAtLastNotification = 1;
	@Ignore
	protected int rating;

	/**
	 *
	 */
	protected long creationTime = System.currentTimeMillis();
	
	/**
	 * The default type is jpg
	 */
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	protected String ending = "jpg";
	
	/**
	 *
	 */
	//TODO: change it to a single long
	@Id
	Long idLong;
	@Parent
	Key parent = ObjectManager.applicationRootKey;

	/**
	 *
	 */
	public Photo() {
		id = PhotoId.getNextId();
		incWriteCount();
	}

	/**
	 * @methodtype constructor
	 */
	public Photo(PhotoId myId) {
		id = myId;

		incWriteCount();
	}

	/**
	 * @methodtype get
	 */
	public Image getImage(PhotoSize photoSize) {
		return images.get(photoSize);
	}

	/**
	 * @methodtype set
	 */
	public void setImage(PhotoSize photoSize, Image image) {
		this.images.put(photoSize, image);
	}

	/**
	 * @methodtype get
	 */
	public String getIdAsString() {
		return id.asString();
	}

	/**
	 * @methodtype get
	 */
	public PhotoId getId() {
		return id;
	}

	/**
	 * @methodtype get
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * @methodtype set
	 */
	public void setOwnerId(String newName) {
		ownerId = newName;
		incWriteCount();
	}

	/**
	 * @methodtype get
	 */
	public String getSummary(ModelConfig cfg) {
		return cfg.asPhotoSummary(ownerId);
	}

	/**
	 * @methodtype get
	 */
	public String getCaption(ModelConfig cfg) {
		String ownerName = UserManager.getInstance().getUserById(ownerId).getNickName();
		return cfg.asPhotoCaption(ownerName);
	}

	/**
	 * @methodtype get
	 */
	public boolean getOwnerNotifyAboutPraise() {
		return ownerNotifyAboutPraise;
	}

	/**
	 * @methodtype set
	 */
	public void setOwnerNotifyAboutPraise(boolean newNotifyAboutPraise) {
		ownerNotifyAboutPraise = newNotifyAboutPraise;
		incWriteCount();
	}

	/**
	 *
	 */
	public Language getOwnerLanguage() {
		return ownerLanguage;
	}

	/**
	 *
	 */
	public void setOwnerLanguage(Language newLanguage) {
		ownerLanguage = newLanguage;
		incWriteCount();
	}

	/**
	 * @methodtype boolean-query
	 */
	public boolean hasSameOwner(Photo photo) {
		return photo.getOwnerEmailAddress().equals(ownerEmailAddress);
	}

	/**
	 * @methodtype get
	 */
	public EmailAddress getOwnerEmailAddress() {
		return ownerEmailAddress;
	}

	/**
	 * @methodtype set
	 */
	public void setOwnerEmailAddress(EmailAddress newEmailAddress) {
		ownerEmailAddress = newEmailAddress;
		incWriteCount();
	}
	
	/**
	 * @methodtype get
	 */
	public String getOwnerNickName() {
		return ownerNickName;
	}
	
	/**
	 * @methodtype set
	 */
	public void setOwnerNickName(String nickName) {
		ownerNickName = nickName;
		incWriteCount();
	}

	/**
	 * @methodtype get
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @methodtype get
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @methodtype get
	 */
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public int getThumbWidth() {
		return isWiderThanHigher() ? MAX_THUMB_PHOTO_WIDTH : (width * MAX_THUMB_PHOTO_HEIGHT / height);
	}

	/**
	 * @methodtype boolean-query
	 */
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public boolean isWiderThanHigher() {
		return (height * MAX_PHOTO_WIDTH) < (width * MAX_PHOTO_HEIGHT);
	}

	/**
	 * @methodtype get
	 */
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public int getThumbHeight() {
		return isWiderThanHigher() ? (height * MAX_THUMB_PHOTO_WIDTH / width) : MAX_THUMB_PHOTO_HEIGHT;
	}

	/**
	 * @methodtype set
	 */
	public void setWidthAndHeight(int newWidth, int newHeight) {
		width = newWidth;
		height = newHeight;

		maxPhotoSize = PhotoSize.getFromWidthHeight(width, height);

		incWriteCount();
	}

	/**
	 * Can this photo satisfy provided photo size?
	 *
	 * @methodtype boolean-query
	 */
	public boolean hasPhotoSize(PhotoSize size) {
		return maxPhotoSize.asInt() >= size.asInt();
	}

	/**
	 * @methodtype get
	 */
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public PhotoSize getMaxPhotoSize() {
		return maxPhotoSize;
	}

	/**
	 * @methodtype get
	 */
	public String getPraiseAsString(ModelConfig cfg) {
		return cfg.asPraiseString(getPraise());
	}

	/**
	 * @methodtype get
	 */
	public double getPraise() {
		return (double) praiseSum / noVotes;
	}

	/**
	 *
	 */
	public void addToPraise(int value) {
		praiseSum += value;
		noVotes += 1;
		incWriteCount();
	}

	/**
	 * @methodtype boolean-query
	 */
	public boolean isVisible() {
		return status.isDisplayable();
	}

	/**
	 * @methodtype get
	 */
	public PhotoStatus getStatus() {
		return status;
	}

	/**
	 * @methodtype set
	 */
	public void setStatus(PhotoStatus newStatus) {
		status = newStatus;
		incWriteCount();
	}

	/**
	 * @methodtype boolean-query
	 */
	public boolean hasTag(String tag) {
		return tags.hasTag(tag);
	}

	/**
	 * @methodtype get
	 */
	public Tags getTags() {
		return tags;
	}

	/**
	 * @methodtype set
	 */
	public void setTags(Tags newTags) {
		tags = newTags;
		incWriteCount();
	}

	/**
	 * @methodtype get
	 */
	public long getCreationTime() {
		return creationTime;
	}


	public String getEnding() {
		return ending;
	}

	public void setEnding(String ending) {
		this.ending = ending;
	}

	/**
	 * @methodtype boolean query
	 */
	public boolean hasNewPraise() {
		return noVotes > noVotesAtLastNotification;
	}

	/**
	 * @methodtype set
	 */
	public void setNoNewPraise() {
		noVotesAtLastNotification = noVotes;
		incWriteCount();
	}
	
	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
	public String getBlobImage() {
        return this.blobImage;
    }

    public byte[] decodeBlobImage() {
        return Base64.decodeBase64(this.blobImage);
    }
    
    public Photo setBlobImage(String blobImage) {
        this.blobImage = blobImage;
        return this;
    }

    public Photo encodeBlobImage(byte[] blobImage) {
        this.blobImage = Base64.encodeBase64URLSafeString(blobImage);
        return this;
    }
    
    public String getPraisingClientId() {
		return praisingClientId;
	}

	public void setPraisingClientId(String praisingClientId) {
		this.praisingClientId = praisingClientId;
	}
	
	public String getResourceId() {
		return resourceId;
	}

	public String getImagesURI() {
		return imagesURI;
	}
	
	public String getRatingURI() {
		return ratingURI;
	}
	
	public void setupNavigationUris(String siteUrl) {
		this.resourceId = siteUrl + "_ah/spi/wahlzeitApi/v1/" + "photos/" + this.getIdAsString();
		this.imagesURI = siteUrl + "_ah/spi/wahlzeitApi/v1/" + "photos/" + this.getIdAsString() + "/" + "images";
		this.ratingURI = siteUrl + "_ah/spi/wahlzeitApi/v1/" + "photos/" + this.getIdAsString() + "/" + "praising";
	}
}
