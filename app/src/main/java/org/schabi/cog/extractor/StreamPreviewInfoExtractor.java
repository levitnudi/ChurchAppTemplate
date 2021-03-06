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

public interface StreamPreviewInfoExtractor {
    AbstractVideoInfo.StreamType getStreamType() throws ParsingException;
    String getWebPageUrl() throws ParsingException;
    String getTitle() throws ParsingException;
    int getDuration() throws ParsingException;
    String getUploader() throws ParsingException;
    String getUploadDate() throws ParsingException;
    long getViewCount() throws  ParsingException;
    String getThumbnailUrl() throws  ParsingException;
}
