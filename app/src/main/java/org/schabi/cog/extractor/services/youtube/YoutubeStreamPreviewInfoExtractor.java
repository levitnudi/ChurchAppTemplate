package org.schabi.cog.extractor.services.youtube;

import org.jsoup.nodes.Element;
import org.schabi.cog.extractor.AbstractVideoInfo;
import org.schabi.cog.extractor.Parser;
import org.schabi.cog.extractor.ParsingException;
import org.schabi.cog.extractor.StreamPreviewInfoExtractor;

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

public class YoutubeStreamPreviewInfoExtractor implements StreamPreviewInfoExtractor {

    private final Element item;

    public YoutubeStreamPreviewInfoExtractor(Element item) {
        this.item = item;
    }

    @Override
    public String getWebPageUrl() throws ParsingException {
        try {
            Element el = item.select("div[class*=\"yt-lockup-video\"").first();
            Element dl = el.select("h3").first().select("a").first();
            return dl.attr("abs:href");
        } catch (Exception e) {
            throw new ParsingException("Could not get web page url for the video", e);
        }
    }

    @Override
    public String getTitle() throws ParsingException {
        try {
            Element el = item.select("div[class*=\"yt-lockup-video\"").first();
            Element dl = el.select("h3").first().select("a").first();
            return dl.text();
        } catch (Exception e) {
            throw new ParsingException("Could not get title", e);
        }
    }

    @Override
    public int getDuration() throws ParsingException {
        try {
            return YoutubeParsingHelper.parseDurationString(
                    item.select("span[class=\"video-time\"]").first().text());
        } catch(Exception e) {
            if(isLiveStream(item)) {
                // -1 for no duration
                return -1;
            } else {
                throw new ParsingException("Could not get Duration: " + getTitle(), e);
            }


        }
    }

    @Override
    public String getUploader() throws ParsingException {
        try {
            return item.select("div[class=\"yt-lockup-byline\"]").first()
                    .select("a").first()
                    .text();
        } catch (Exception e) {
            throw new ParsingException("Could not get uploader", e);
        }
    }

    @Override
    public String getUploadDate() throws ParsingException {
        try {
            return item.select("div[class=\"yt-lockup-meta\"]").first()
                    .select("li").first()
                    .text();
        } catch(Exception e) {
            throw new ParsingException("Could not get uplaod date", e);
        }
    }

    @Override
    public long getViewCount() throws ParsingException {
        String output;
        String input;
        try {
            input = item.select("div[class=\"yt-lockup-meta\"]").first()
                    .select("li").get(1)
                    .text();
        } catch (IndexOutOfBoundsException e) {
            if(isLiveStream(item)) {
                // -1 for no view count
                return -1;
            } else {
                throw new ParsingException(
                        "Could not parse yt-lockup-meta although available: " + getTitle(), e);
            }
        }

        output = Parser.matchGroup1("([0-9,\\. ]*)", input)
                .replace(" ", "")
                .replace(".", "")
                .replace(",", "");

        try {
            return Long.parseLong(output);
        } catch (NumberFormatException e) {
            // if this happens the video probably has no views
            if(!input.isEmpty()) {
                return 0;
            } else {
                throw new ParsingException("Could not handle input: " + input, e);
            }
        }
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        try {
            String url;
            Element te = item.select("div[class=\"yt-thumb video-thumb\"]").first()
                    .select("img").first();
            url = te.attr("abs:src");
            // Sometimes youtube sends links to gif files which somehow seem to not exist
            // anymore. Items with such gif also offer a secondary image source. So we are going
            // to use that if we've caught such an item.
            if (url.contains(".gif")) {
                url = te.attr("abs:data-thumb");
            }
            return url;
        } catch (Exception e) {
            throw new ParsingException("Could not get thumbnail url", e);
        }
    }

    @Override
    public AbstractVideoInfo.StreamType getStreamType() {
        if(isLiveStream(item)) {
            return AbstractVideoInfo.StreamType.LIVE_STREAM;
        } else {
            return AbstractVideoInfo.StreamType.VIDEO_STREAM;
        }
    }

    private boolean isLiveStream(Element item) {
        Element bla = item.select("span[class*=\"yt-badge-live\"]").first();

        if(bla == null) {
            // sometimes livestreams dont have badges but sill are live streams
            // if video time is not available we most likly have an offline livestream
            if(item.select("span[class*=\"video-time\"]").first() == null) {
                return true;
            }
        }
        return bla != null;
    }
}
