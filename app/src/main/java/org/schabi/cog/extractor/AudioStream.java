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

public class AudioStream {
    public String url = "";
    public int format = -1;
    public int bandwidth = -1;
    public int sampling_rate = -1;

    public AudioStream(String url, int format, int bandwidth, int samplingRate) {
        this.url = url; this.format = format;
        this.bandwidth = bandwidth; this.sampling_rate = samplingRate;
    }

    // reveals wether two streams are the same, but have diferent urls
    public boolean equalStats(AudioStream cmp) {
        return format == cmp.format
                && bandwidth == cmp.bandwidth
                && sampling_rate == cmp.sampling_rate;
    }

    // revelas wether two streams are equal
    public boolean equals(AudioStream cmp) {
        return cmp != null && equalStats(cmp)
                && url == cmp.url;
    }
}
