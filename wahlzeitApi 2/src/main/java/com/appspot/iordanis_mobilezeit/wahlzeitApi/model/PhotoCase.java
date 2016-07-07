/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2016-05-27 16:00:31 UTC)
 * on 2016-06-16 at 22:23:13 UTC 
 * Modify at your own risk.
 */

package com.appspot.iordanis_mobilezeit.wahlzeitApi.model;

/**
 * Model definition for PhotoCase.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the wahlzeitApi. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class PhotoCase extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long creationTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long decisionTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String explanation;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String flagger;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private CaseId id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String idAsString;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Photo photo;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String photoOwnerName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String photoStatus;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String reason;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCreationTime() {
    return creationTime;
  }

  /**
   * @param creationTime creationTime or {@code null} for none
   */
  public PhotoCase setCreationTime(java.lang.Long creationTime) {
    this.creationTime = creationTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getDecisionTime() {
    return decisionTime;
  }

  /**
   * @param decisionTime decisionTime or {@code null} for none
   */
  public PhotoCase setDecisionTime(java.lang.Long decisionTime) {
    this.decisionTime = decisionTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getExplanation() {
    return explanation;
  }

  /**
   * @param explanation explanation or {@code null} for none
   */
  public PhotoCase setExplanation(java.lang.String explanation) {
    this.explanation = explanation;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getFlagger() {
    return flagger;
  }

  /**
   * @param flagger flagger or {@code null} for none
   */
  public PhotoCase setFlagger(java.lang.String flagger) {
    this.flagger = flagger;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public CaseId getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public PhotoCase setId(CaseId id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getIdAsString() {
    return idAsString;
  }

  /**
   * @param idAsString idAsString or {@code null} for none
   */
  public PhotoCase setIdAsString(java.lang.String idAsString) {
    this.idAsString = idAsString;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Photo getPhoto() {
    return photo;
  }

  /**
   * @param photo photo or {@code null} for none
   */
  public PhotoCase setPhoto(Photo photo) {
    this.photo = photo;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPhotoOwnerName() {
    return photoOwnerName;
  }

  /**
   * @param photoOwnerName photoOwnerName or {@code null} for none
   */
  public PhotoCase setPhotoOwnerName(java.lang.String photoOwnerName) {
    this.photoOwnerName = photoOwnerName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPhotoStatus() {
    return photoStatus;
  }

  /**
   * @param photoStatus photoStatus or {@code null} for none
   */
  public PhotoCase setPhotoStatus(java.lang.String photoStatus) {
    this.photoStatus = photoStatus;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getReason() {
    return reason;
  }

  /**
   * @param reason reason or {@code null} for none
   */
  public PhotoCase setReason(java.lang.String reason) {
    this.reason = reason;
    return this;
  }

  @Override
  public PhotoCase set(String fieldName, Object value) {
    return (PhotoCase) super.set(fieldName, value);
  }

  @Override
  public PhotoCase clone() {
    return (PhotoCase) super.clone();
  }

}