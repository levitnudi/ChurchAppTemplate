package org.schabi.cog;

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

import android.graphics.Bitmap;
import java.util.List;

/**
 * Singleton:
 * Used to send data between certain Activity/Services within the same process.
 * This can be considered as an ugly hack inside the Android universe. **/
public class ActivityCommunicator {

    private static ActivityCommunicator activityCommunicator = null;

    public static ActivityCommunicator getCommunicator() {
        if(activityCommunicator == null) {
            activityCommunicator = new ActivityCommunicator();
        }
        return activityCommunicator;
    }

    // Thumbnail send from VideoItemDetailFragment to BackgroundPlayer
    public volatile Bitmap backgroundPlayerThumbnail;

    // Sent from any activity to ErrorActivity.
    public volatile List<Exception> errorList;
    public volatile Class returnActivity;
}
