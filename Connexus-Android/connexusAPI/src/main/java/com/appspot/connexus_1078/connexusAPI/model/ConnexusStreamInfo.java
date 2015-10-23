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
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-08-03 17:34:38 UTC)
 * on 2015-10-22 at 15:35:41 UTC 
 * Modify at your own risk.
 */

package com.appspot.connexus_1078.connexusAPI.model;

/**
 * Model definition for ConnexusStreamInfo.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the connexusAPI. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ConnexusStreamInfo extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String coverImageUrl;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("create_time")
  private com.google.api.client.util.DateTime createTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("last_newpic_time")
  private com.google.api.client.util.DateTime lastNewpicTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("pic_num") @com.google.api.client.json.JsonString
  private java.lang.Long picNum;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("stream_id") @com.google.api.client.json.JsonString
  private java.lang.Long streamId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<java.lang.String> subscribers;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String tag;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String user;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long viewCount;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCoverImageUrl() {
    return coverImageUrl;
  }

  /**
   * @param coverImageUrl coverImageUrl or {@code null} for none
   */
  public ConnexusStreamInfo setCoverImageUrl(java.lang.String coverImageUrl) {
    this.coverImageUrl = coverImageUrl;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getCreateTime() {
    return createTime;
  }

  /**
   * @param createTime createTime or {@code null} for none
   */
  public ConnexusStreamInfo setCreateTime(com.google.api.client.util.DateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getLastNewpicTime() {
    return lastNewpicTime;
  }

  /**
   * @param lastNewpicTime lastNewpicTime or {@code null} for none
   */
  public ConnexusStreamInfo setLastNewpicTime(com.google.api.client.util.DateTime lastNewpicTime) {
    this.lastNewpicTime = lastNewpicTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * @param name name or {@code null} for none
   */
  public ConnexusStreamInfo setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getPicNum() {
    return picNum;
  }

  /**
   * @param picNum picNum or {@code null} for none
   */
  public ConnexusStreamInfo setPicNum(java.lang.Long picNum) {
    this.picNum = picNum;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getStreamId() {
    return streamId;
  }

  /**
   * @param streamId streamId or {@code null} for none
   */
  public ConnexusStreamInfo setStreamId(java.lang.Long streamId) {
    this.streamId = streamId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getSubscribers() {
    return subscribers;
  }

  /**
   * @param subscribers subscribers or {@code null} for none
   */
  public ConnexusStreamInfo setSubscribers(java.util.List<java.lang.String> subscribers) {
    this.subscribers = subscribers;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getTag() {
    return tag;
  }

  /**
   * @param tag tag or {@code null} for none
   */
  public ConnexusStreamInfo setTag(java.lang.String tag) {
    this.tag = tag;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUser() {
    return user;
  }

  /**
   * @param user user or {@code null} for none
   */
  public ConnexusStreamInfo setUser(java.lang.String user) {
    this.user = user;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getViewCount() {
    return viewCount;
  }

  /**
   * @param viewCount viewCount or {@code null} for none
   */
  public ConnexusStreamInfo setViewCount(java.lang.Long viewCount) {
    this.viewCount = viewCount;
    return this;
  }

  @Override
  public ConnexusStreamInfo set(String fieldName, Object value) {
    return (ConnexusStreamInfo) super.set(fieldName, value);
  }

  @Override
  public ConnexusStreamInfo clone() {
    return (ConnexusStreamInfo) super.clone();
  }

}
