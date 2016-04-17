package org.wahlzeit.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.wahlzeit.model.CaseId;
import org.wahlzeit.model.FlagReason;
import org.wahlzeit.model.Photo;
import org.wahlzeit.model.PhotoCase;
import org.wahlzeit.model.PhotoCaseManager;
import org.wahlzeit.model.PhotoId;
import org.wahlzeit.model.PhotoManager;
import org.wahlzeit.model.PhotoStatus;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;

@Api(name="wahlzeitApi",
version = "v1",
description = "A multiclient API for Whalzeit"
)
public class FlagsEndpoint {

	@ApiMethod(name="flags.create")
	public PhotoCase createPhotoCase(PhotoCase photoCase) {
		PhotoCaseManager pmc = PhotoCaseManager.getInstance();
		PhotoManager pm = PhotoManager.getInstance();
		
		PhotoId photoId = PhotoId.getIdFromString(photoCase.getPhoto().getIdAsString());
		Photo flaggedPhoto = pm.getPhoto(photoId);
		flaggedPhoto.setStatus(flaggedPhoto.getStatus().asFlagged(true));
		pm.savePhoto(flaggedPhoto);
		
		PhotoCase result = new PhotoCase(flaggedPhoto);
		result.setFlagger(photoCase.getFlagger());
		result.setReason(photoCase.getReason());
		result.setExplanation(photoCase.getExplanation());
		pmc.addPhotoCase(result);
		return result;
	}
	
	@ApiMethod(name="flags.list")
	public Collection<PhotoCase> listAllPhotoCases() {
		Collection<PhotoCase> result;
		PhotoCaseManager pcm = PhotoCaseManager.getInstance();
		PhotoCase[] flaggedCases = pcm.getOpenPhotoCasesByAscendingAge();
		result = new HashSet<PhotoCase>(Arrays.asList(flaggedCases));
		return result;
	}
	
	@ApiMethod(name="flags.update")
	public PhotoCase updatePhotoCase(PhotoCase photoCase) {
		String id = photoCase.getIdAsString();
		PhotoCaseManager pcm = PhotoCaseManager.getInstance();
		CaseId caseId = new CaseId(Integer.parseInt(id));
		
		PhotoCase result = pcm.getPhotoCase(caseId);
		Photo photo = result.getPhoto();
		PhotoStatus status = photo.getStatus();
		if (photoCase.getPhoto().getStatus().equals(PhotoStatus.MODERATED)) {
			status = status.asModerated(true);
		} else {
			status = status.asFlagged(false);
		}
		photo.setStatus(status);
		result.setDecided();
		pcm.removePhotoCase(result);
		return result;
	}
}
