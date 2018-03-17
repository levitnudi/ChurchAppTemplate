package org.schabi.cog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import org.schabi.cog.download.MainActivity;
import org.schabi.newpipe.R;
import org.schabi.cog.extractor.MediaFormat;
import org.schabi.cog.extractor.VideoStream;

import java.util.List;

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


class ActionBarHandler {
    private static final String TAG = ActionBarHandler.class.toString();

    private AppCompatActivity activity;
    private int selectedVideoStream = -1;

    private SharedPreferences defaultPreferences = null;

    private Menu menu;

    // Only callbacks are listed here, there are more actions which don't need a callback.
    // those are edited directly. Typically VideoItemDetailFragment will implement those callbacks.
    private OnActionListener onShareListener = null;
    private OnActionListener onOpenInBrowserListener = null;
    private OnActionListener onDownloadListener = null;
    private OnActionListener onPlayWithKodiListener = null;
    private OnActionListener onPlayAudioListener = null;


    // Triggered when a stream related action is triggered.
    public interface OnActionListener {
        void onActionSelected(int selectedStreamId);
    }

    public ActionBarHandler(AppCompatActivity activity) {
        this.activity = activity;
    }

    @SuppressWarnings({"deprecation", "ConstantConditions"})
    public void setupNavMenu(AppCompatActivity activity) {
        this.activity = activity;
        try {
            activity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void setupStreamList(final List<VideoStream> videoStreams) {
        if (activity != null) {
            selectedVideoStream = 0;


            // this array will be shown in the dropdown menu for selecting the stream/resolution.
            String[] itemArray = new String[videoStreams.size()];
            for (int i = 0; i < videoStreams.size(); i++) {
                VideoStream item = videoStreams.get(i);
                itemArray[i] = MediaFormat.getNameById(item.format) + " " + item.resolution;
            }
            int defaultResolution = getDefaultResolution(videoStreams);

            ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(activity.getBaseContext(),
                    android.R.layout.simple_spinner_dropdown_item, itemArray);

            ActionBar ab = activity.getSupportActionBar();
            //todo: make this throwsable
            assert ab != null : "Could not get actionbar";
            ab.setListNavigationCallbacks(itemAdapter
                    , new ActionBar.OnNavigationListener() {
                @Override
                public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                    selectedVideoStream = (int) itemId;
                    return true;
                }
            });

            ab.setSelectedNavigationItem(defaultResolution);
        }
    }


    private int getDefaultResolution(final List<VideoStream> videoStreams) {
        String defaultResolution = defaultPreferences
                .getString(activity.getString(R.string.default_resolution_key),
                        activity.getString(R.string.default_resolution_value));

        for (int i = 0; i < videoStreams.size(); i++) {
            VideoStream item = videoStreams.get(i);
            if (defaultResolution.equals(item.resolution)) {
                return i;
            }
        }
        // this is actually an error,
        // but maybe there is really no stream fitting to the default value.
        return 0;
    }

    public void setupMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;

        // CAUTION set item properties programmatically otherwise it would not be accepted by
        // appcompat itemsinflater.inflate(R.menu.videoitem_detail, menu);

        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        inflater.inflate(R.menu.videoitem_detail, menu);

    }

    public boolean onItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_item_share: {
                    /*
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, websiteUrl);
                    intent.setType("text/plain");
                    activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share_dialog_title)));
                    */
                if(onShareListener != null) {
                    onShareListener.onActionSelected(selectedVideoStream);
                }
                return true;
            }

            case R.id.menu_item_download:
                if(onDownloadListener != null) {
                    onDownloadListener.onActionSelected(selectedVideoStream);
                }
                return true;

            case R.id.menu_item_play_audio:
                if(onPlayAudioListener != null) {
                    onPlayAudioListener.onActionSelected(selectedVideoStream);
                }
                return true;

            default:
                Log.e(TAG, "Menu Item not known");
        }
        return false;
    }

    public int getSelectedVideoStream() {
        return selectedVideoStream;
    }

    public void setOnShareListener(OnActionListener listener) {
        onShareListener = listener;
    }

    public void setOnOpenInBrowserListener(OnActionListener listener) {
        onOpenInBrowserListener = listener;
    }

    public void setOnDownloadListener(OnActionListener listener) {
        onDownloadListener = listener;
    }

    public void setOnPlayWithKodiListener(OnActionListener listener) {
        onPlayWithKodiListener = listener;
    }

    public void setOnPlayAudioListener(OnActionListener listener) {
        onPlayAudioListener = listener;
    }

    public void showAudioAction(boolean visible) {
        menu.findItem(R.id.menu_item_play_audio).setVisible(visible);
    }

    public void showDownloadAction(boolean visible) {
        menu.findItem(R.id.menu_item_download).setVisible(visible);
    }
}
