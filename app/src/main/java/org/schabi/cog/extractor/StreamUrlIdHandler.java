package org.schabi.cog.extractor;

/*
 * Copyright 2015 Levit Nudi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public interface StreamUrlIdHandler {
    String getVideoUrl(String videoId);
    String getVideoId(String siteUrl) throws ParsingException;
    String cleanUrl(String siteUrl) throws ParsingException;

    /**When a VIEW_ACTION is caught this function will test if the url delivered within the calling
     Intent was meant to be watched with this Service.
     Return false if this service shall not allow to be called through ACTIONs.*/
    boolean acceptUrl(String videoUrl);
}
