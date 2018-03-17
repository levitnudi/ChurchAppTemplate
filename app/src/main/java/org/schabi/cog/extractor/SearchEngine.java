package org.schabi.cog.extractor;

import java.io.IOException;
import java.util.ArrayList;

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

@SuppressWarnings("ALL")
public abstract class SearchEngine {
    public static class NothingFoundException extends ExtractionException {
        public NothingFoundException(String message) {
            super(message);
        }
    }

    private StreamPreviewInfoSearchCollector collector;

    public SearchEngine(StreamUrlIdHandler urlIdHandler, int serviceId) {
        collector = new StreamPreviewInfoSearchCollector(urlIdHandler, serviceId);
    }

    protected StreamPreviewInfoSearchCollector getStreamPreviewInfoSearchCollector() {
        return collector;
    }

    public abstract ArrayList<String> suggestionList(
            String query,String contentCountry, Downloader dl)
            throws ExtractionException, IOException;

    //Result search(String query, int page);
    public abstract StreamPreviewInfoSearchCollector search(
            String query, int page, String contentCountry, Downloader dl)
            throws ExtractionException, IOException;
}
