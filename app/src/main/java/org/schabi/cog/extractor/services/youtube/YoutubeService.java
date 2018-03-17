package org.schabi.cog.extractor.services.youtube;

import org.schabi.cog.extractor.ExtractionException;
import org.schabi.cog.extractor.SearchEngine;
import org.schabi.cog.extractor.StreamExtractor;
import org.schabi.cog.extractor.StreamUrlIdHandler;
import org.schabi.cog.extractor.StreamingService;
import org.schabi.cog.extractor.Downloader;

import java.io.IOException;


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

public class YoutubeService extends StreamingService {

    public YoutubeService(int id) {
        super(id);
    }

    @Override
    public ServiceInfo getServiceInfo() {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.name = "Youtube";
        return serviceInfo;
    }
    @Override
    public StreamExtractor getExtractorInstance(String url, Downloader downloader)
            throws ExtractionException, IOException {
        StreamUrlIdHandler urlIdHandler = new YoutubeStreamUrlIdHandler();
        if(urlIdHandler.acceptUrl(url)) {
            return new YoutubeStreamExtractor(urlIdHandler, url, downloader, getServiceId());
        }
        else {
            throw new IllegalArgumentException("supplied String is not a valid Youtube URL");
        }
    }
    @Override
    public SearchEngine getSearchEngineInstance(Downloader downloader) {
        return new YoutubeSearchEngine(getUrlIdHandlerInstance(), getServiceId());
    }

    @Override
    public StreamUrlIdHandler getUrlIdHandlerInstance() {
        return new YoutubeStreamUrlIdHandler();
    }
}
