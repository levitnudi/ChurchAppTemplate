package org.schabi.cog.extractor;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.StringReader;
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
public class DashMpdParser {

    private DashMpdParser() {
    }

    static class DashMpdParsingException extends ParsingException {
        DashMpdParsingException(String message, Exception e) {
            super(message, e);
        }
    }

    public static List<AudioStream> getAudioStreams(String dashManifestUrl,
                                                             Downloader downloader)
            throws DashMpdParsingException {
        String dashDoc;
        try {
            dashDoc = downloader.download(dashManifestUrl);
        } catch(IOException ioe) {
            throw new DashMpdParsingException("Could not get dash mpd: " + dashManifestUrl, ioe);
        }
        Vector<AudioStream> audioStreams = new Vector<>();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(dashDoc));
            String tagName = "";
            String currentMimeType = "";
            int currentBandwidth = -1;
            int currentSamplingRate = -1;
            boolean currentTagIsBaseUrl = false;
            for(int eventType = parser.getEventType();
                eventType != XmlPullParser.END_DOCUMENT;
                eventType = parser.next() ) {
                switch(eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if(tagName.equals("AdaptationSet")) {
                            currentMimeType = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "mimeType");
                        } else if(tagName.equals("Representation") && currentMimeType.contains("audio")) {
                            currentBandwidth = Integer.parseInt(
                                    parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "bandwidth"));
                            currentSamplingRate = Integer.parseInt(
                                    parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "audioSamplingRate"));
                        } else if(tagName.equals("BaseURL")) {
                            currentTagIsBaseUrl = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        // actual stream tag
                        if(currentTagIsBaseUrl &&
                                (currentMimeType.contains("audio"))) {
                            int format = -1;
                            if(currentMimeType.equals(MediaFormat.M4A.mimeType)) {
                                format = MediaFormat.M4A.id;
                            } else if(currentMimeType.equals(MediaFormat.M4A.mimeType)) {
                                format = MediaFormat.M4A.id;
                            }
                            audioStreams.add(new AudioStream(parser.getText(),
                                    format, currentBandwidth, currentSamplingRate));
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if(tagName.equals("AdaptationSet")) {
                            currentMimeType = "";
                        } else if(tagName.equals("BaseURL")) {
                            currentTagIsBaseUrl = false;
                        }
                        break;
                }
            }
        } catch(Exception e) {
            throw new DashMpdParsingException("Could not parse Dash mpd", e);
        }
        return audioStreams;
    }
}
