package org.schabi.cog.extractor;

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

public abstract class StreamingService {
    public class ServiceInfo {
        public String name = "";
    }

    private int serviceId;

    public StreamingService(int id) {
        serviceId = id;
    }

    public abstract ServiceInfo getServiceInfo();

    public abstract StreamExtractor getExtractorInstance(String url, Downloader downloader)
            throws IOException, ExtractionException;
    public abstract SearchEngine getSearchEngineInstance(Downloader downloader);
    public abstract StreamUrlIdHandler getUrlIdHandlerInstance();

    public final int getServiceId() {
        return serviceId;
    }
}
