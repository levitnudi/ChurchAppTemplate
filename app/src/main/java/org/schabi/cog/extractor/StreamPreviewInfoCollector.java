package org.schabi.cog.extractor;

import org.schabi.cog.extractor.services.youtube.YoutubeStreamUrlIdHandler;

import java.util.List;
import java.util.Vector;

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

public class StreamPreviewInfoCollector {
    private List<StreamPreviewInfo> itemList = new Vector<>();
    private List<Exception> errors = new Vector<>();
    private StreamUrlIdHandler urlIdHandler = null;
    private int serviceId = -1;

    public StreamPreviewInfoCollector(StreamUrlIdHandler handler, int serviceId) {
        urlIdHandler = handler;
        this.serviceId = serviceId;
    }

    public List<StreamPreviewInfo> getItemList() {
        return itemList;
    }

    public List<Exception> getErrors() {
        return errors;
    }

    public void addError(Exception e) {
        errors.add(e);
    }

    public void commit(StreamPreviewInfoExtractor extractor) throws ParsingException {
        try {
            StreamPreviewInfo resultItem = new StreamPreviewInfo();
            // importand information
            resultItem.service_id = serviceId;
            resultItem.webpage_url = extractor.getWebPageUrl();
            if (urlIdHandler == null) {
                throw new ParsingException("Error: UrlIdHandler not set");
            } else {
                resultItem.id = (new YoutubeStreamUrlIdHandler()).getVideoId(resultItem.webpage_url);
            }
            resultItem.title = extractor.getTitle();
            resultItem.stream_type = extractor.getStreamType();

            // optional iformation
            try {
                resultItem.duration = extractor.getDuration();
            } catch (Exception e) {
                addError(e);
            }
            try {
                resultItem.uploader = extractor.getUploader();
            } catch (Exception e) {
                addError(e);
            }
            try {
                resultItem.upload_date = extractor.getUploadDate();
            } catch (Exception e) {
                addError(e);
            }
            try {
                resultItem.view_count = extractor.getViewCount();
            } catch (Exception e) {
                addError(e);
            }
            try {
                resultItem.thumbnail_url = extractor.getThumbnailUrl();
            } catch (Exception e) {
                addError(e);
            }
            itemList.add(resultItem);
        } catch (Exception e) {
            addError(e);
        }
    }
}
