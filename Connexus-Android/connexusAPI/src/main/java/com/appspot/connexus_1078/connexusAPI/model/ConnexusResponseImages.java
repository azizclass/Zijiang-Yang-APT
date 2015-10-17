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
 * on 2015-10-16 at 23:19:27 UTC 
 * Modify at your own risk.
 */

package com.appspot.connexus_1078.connexusAPI.model;

/**
 * Model definition for ConnexusResponseImages.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the connexusAPI. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ConnexusResponseImages extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("image_urls")
  private java.util.List<java.lang.String> imageUrls;

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getImageUrls() {
    return imageUrls;
  }

  /**
   * @param imageUrls imageUrls or {@code null} for none
   */
  public ConnexusResponseImages setImageUrls(java.util.List<java.lang.String> imageUrls) {
    this.imageUrls = imageUrls;
    return this;
  }

  @Override
  public ConnexusResponseImages set(String fieldName, Object value) {
    return (ConnexusResponseImages) super.set(fieldName, value);
  }

  @Override
  public ConnexusResponseImages clone() {
    return (ConnexusResponseImages) super.clone();
  }

}
