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

import java.util.List;

/**Scrapes information from a video streaming service (eg, YouTube).*/


@SuppressWarnings("ALL")
public abstract class StreamExtractor {

    private int serviceId;
    private String url;
    private StreamUrlIdHandler urlIdHandler;
    private Downloader downloader;
    private StreamPreviewInfoCollector previewInfoCollector;

    public class ExctractorInitException extends ExtractionException {
        public ExctractorInitException(String message) {
            super(message);
        }
        public ExctractorInitException(Throwable cause) {
            super(cause);
        }
        public ExctractorInitException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public class ContentNotAvailableException extends ParsingException {
        public ContentNotAvailableException(String message) {
            super(message);
        }
        public ContentNotAvailableException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public StreamExtractor(StreamUrlIdHandler urlIdHandler, String url, Downloader dl, int serviceId) {
        this.serviceId = serviceId;
        this.urlIdHandler = urlIdHandler;
        previewInfoCollector = new StreamPreviewInfoCollector(urlIdHandler, serviceId);
    }

    protected StreamPreviewInfoCollector getStreamPreviewInfoCollector() {
        return previewInfoCollector;
    }

    public String getUrl() {
        return url;
    }

    public StreamUrlIdHandler getUrlIdHandler() {
        return urlIdHandler;
    }

    public Downloader getDownloader() {
        return downloader;
    }

    public abstract int getTimeStamp() throws ParsingException;
    public abstract String getTitle() throws ParsingException;
    public abstract String getDescription() throws ParsingException;
    public abstract String getUploader() throws ParsingException;
    public abstract int getLength() throws ParsingException;
    public abstract long getViewCount() throws ParsingException;
    public abstract String getUploadDate() throws ParsingException;
    public abstract String getThumbnailUrl() throws ParsingException;
    public abstract String getUploaderThumbnailUrl() throws ParsingException;
    public abstract List<AudioStream> getAudioStreams() throws ParsingException;
    public abstract List<VideoStream> getVideoStreams() throws ParsingException;
    public abstract List<VideoStream> getVideoOnlyStreams() throws ParsingException;
    public abstract String getDashMpdUrl() throws ParsingException;
    public abstract int getAgeLimit() throws ParsingException;
    public abstract String getAverageRating() throws ParsingException;
    public abstract int getLikeCount() throws ParsingException;
    public abstract int getDislikeCount() throws ParsingException;
    public abstract StreamPreviewInfoExtractor getNextVideo() throws ParsingException;
    public abstract StreamPreviewInfoCollector getRelatedVideos() throws ParsingException;
    public abstract String getPageUrl();
    public abstract StreamInfo.StreamType getStreamType() throws ParsingException;
    public int getServiceId() {
        return serviceId;
    }
}
