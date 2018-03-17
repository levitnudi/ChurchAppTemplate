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

public class VideoStream {
    //url of the stream
    public String url = "";
    public int format = -1;
    public String resolution = "";

    public VideoStream(String url, int format, String res) {
        this.url = url; this.format = format; resolution = res;
    }

    // reveals wether two streams are the same, but have diferent urls
    public boolean equalStats(VideoStream cmp) {
        return format == cmp.format
                && resolution == cmp.resolution;
    }

    // revelas wether two streams are equal
    public boolean equals(VideoStream cmp) {
        return cmp != null && equalStats(cmp)
                && url == cmp.url;
    }
}
