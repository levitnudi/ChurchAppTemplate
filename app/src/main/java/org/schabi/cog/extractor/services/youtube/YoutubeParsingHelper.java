package org.schabi.cog.extractor.services.youtube;

import org.schabi.cog.extractor.ParsingException;

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

public class YoutubeParsingHelper {

    private YoutubeParsingHelper() {
    }

    public static int parseDurationString(String input)
            throws ParsingException, NumberFormatException {
        String[] splitInput = input.split(":");
        String days = "0";
        String hours = "0";
        String minutes = "0";
        String seconds;

        switch(splitInput.length) {
            case 4:
                days = splitInput[0];
                hours = splitInput[1];
                minutes = splitInput[2];
                seconds = splitInput[3];
                break;
            case 3:
                hours = splitInput[0];
                minutes = splitInput[1];
                seconds = splitInput[2];
                break;
            case 2:
                minutes = splitInput[0];
                seconds = splitInput[1];
                break;
            case 1:
                seconds = splitInput[0];
                break;
            default:
                throw new ParsingException("Error duration string with unknown format: " + input);
        }
        return ((((Integer.parseInt(days) * 24)
                + Integer.parseInt(hours) * 60)
                + Integer.parseInt(minutes)) * 60)
                + Integer.parseInt(seconds);
    }
}
