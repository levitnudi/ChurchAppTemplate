/**
 * Created by k3b on 07.01.2016.
 *
 * Copyright (C) Christian Schabesberger 2015 <chris.schabesberger@mailbox.org>
 * CityOfGodVidSettings.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.schabi.cog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import org.schabi.newpipe.R;

import java.io.File;

import us.shandian.giga.util.Utility;

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

/**
 * Helper for global settings
 */
public class CityOfGodVidSettings {

    private CityOfGodVidSettings() {
    }

    public static void initSettings(Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.settings, false);
        getVideoDownloadFolder(context);
        getAudioDownloadFolder(context);
    }

    public static File getVideoDownloadFolder(Context context) {
        return getFolder(context, R.string.download_path_key, "COG/ALLFILES/");
    }

    public static String getVideoDownloadPath(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String key = context.getString(R.string.download_path_key);
        String downloadPath = prefs.getString(key, "COG/ALLFILES/");

        return downloadPath;
    }

    public static File getAudioDownloadFolder(Context context) {
        return getFolder(context, R.string.download_path_audio_key, "COG/ALLFILES/");
    }

    public static String getAudioDownloadPath(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String key = context.getString(R.string.download_path_audio_key);
        String downloadPath = prefs.getString(key, "COG/ALLFILES/");

        return downloadPath;
    }

    public static String getDownloadPath(Context context, String fileName)
    {
        if(Utility.isVideoFile(fileName)) {
            return CityOfGodVidSettings.getVideoDownloadPath(context);
        }
        return CityOfGodVidSettings.getAudioDownloadPath(context);
    }

    private static File getFolder(Context context, int keyID, String defaultDirectoryName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String key = context.getString(keyID);
        String downloadPath = prefs.getString(key, null);
        if ((downloadPath != null) && (!downloadPath.isEmpty())) return new File(downloadPath.trim());

        final File folder = getFolder(defaultDirectoryName);
        SharedPreferences.Editor spEditor = prefs.edit();
        spEditor.putString(key
                , new File(folder,"Files").getAbsolutePath());
        spEditor.apply();
        return folder;
    }

    @NonNull
    private static File getFolder(String defaultDirectoryName) {
        return new File(Environment.getExternalStorageDirectory(),defaultDirectoryName);
    }
}
