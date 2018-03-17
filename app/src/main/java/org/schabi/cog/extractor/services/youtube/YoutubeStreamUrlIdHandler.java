package org.schabi.cog.extractor.services.youtube;

import org.schabi.cog.extractor.StreamUrlIdHandler;
import org.schabi.cog.extractor.Parser;
import org.schabi.cog.extractor.ParsingException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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

public class YoutubeStreamUrlIdHandler implements StreamUrlIdHandler {
    @SuppressWarnings("WeakerAccess")
    @Override
    public String getVideoUrl(String videoId) {
        return "https://www.youtube.com/watch?v=" + videoId;
    }

    @SuppressWarnings("WeakerAccess")
    @Override
    public String getVideoId(String url) throws ParsingException {
        String id;

        if(url.contains("youtube")) {
            if(url.contains("attribution_link")) {
                try {
                    String escapedQuery = Parser.matchGroup1("u=(.[^&|$]*)", url);
                    String query = URLDecoder.decode(escapedQuery, "UTF-8");
                    id = Parser.matchGroup1("v=([\\-a-zA-Z0-9_]{11})", query);
                } catch(UnsupportedEncodingException uee) {
                    throw new ParsingException("Could not parse attribution_link", uee);
                }
            } else {
                id = Parser.matchGroup1("[?&]v=([\\-a-zA-Z0-9_]{11})", url);
            }
        }
        else if(url.contains("youtu.be")) {
            if(url.contains("v=")) {
                id = Parser.matchGroup1("v=([\\-a-zA-Z0-9_]{11})", url);
            } else {
                id = Parser.matchGroup1("youtu\\.be/([a-zA-Z0-9_-]{11})", url);
            }
        }
        else {
            throw new ParsingException("Error no suitable url: " + url);
        }


        if(!id.isEmpty()){
            return id;
        } else {
            throw new ParsingException("Error could not parse url: " + url);
        }
    }

    public String cleanUrl(String complexUrl) throws ParsingException {
        return getVideoUrl(getVideoId(complexUrl));
    }

    @Override
    public boolean acceptUrl(String videoUrl) {
        return videoUrl.contains("youtube") ||
                videoUrl.contains("youtu.be");
    }
}
