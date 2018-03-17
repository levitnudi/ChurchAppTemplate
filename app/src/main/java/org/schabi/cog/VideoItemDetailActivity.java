package org.schabi.cog;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.schabi.cog.extractor.ServiceList;
import org.schabi.cog.extractor.StreamingService;
import org.schabi.newpipe.R;


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

public class VideoItemDetailActivity extends AppCompatActivity {

    private static final String TAG = VideoItemDetailActivity.class.toString();

    private VideoItemDetailFragment fragment;

    private String videoUrl;// = "https://youtu.be/BkDnI-HhpCA";
    private int currentStreamingService = -1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoitem_detail);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Show the Up button in the action bar.
        try {
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch(Exception e) {}



        Bundle arguments = new Bundle();
        if (savedInstanceState == null) {
            // this means the video was called though another app
            if (getIntent().getData() != null) {
                videoUrl = getIntent().getData().toString();
                StreamingService[] serviceList = ServiceList.getServices();
                //StreamExtractor videoExtractor = null;
                for (int i = 0; i < serviceList.length; i++) {
                    if (serviceList[i].getUrlIdHandlerInstance().acceptUrl(videoUrl)) {
                        arguments.putInt(VideoItemDetailFragment.STREAMING_SERVICE, i);
                        currentStreamingService = i;
                        //videoExtractor = ServiceList.getService(i).getExtractorInstance();
                        break;
                    }
                }
                if(currentStreamingService == -1) {}
                //arguments.putString(VideoItemDetailFragment.VIDEO_URL,
                //        videoExtractor.getVideoUrl(videoExtractor.getVideoId(videoUrl)));//cleans URL
                arguments.putString(VideoItemDetailFragment.VIDEO_URL, videoUrl);

                arguments.putBoolean(VideoItemDetailFragment.AUTO_PLAY,
                        PreferenceManager.getDefaultSharedPreferences(this)
                                .getBoolean(getString(R.string.autoplay_through_intent_key), false));
            } else {
                videoUrl = getIntent().getStringExtra(VideoItemDetailFragment.VIDEO_URL);
                currentStreamingService = getIntent().getIntExtra(VideoItemDetailFragment.STREAMING_SERVICE, -1);
                arguments.putString(VideoItemDetailFragment.VIDEO_URL, videoUrl);
                arguments.putInt(VideoItemDetailFragment.STREAMING_SERVICE, currentStreamingService);
                arguments.putBoolean(VideoItemDetailFragment.AUTO_PLAY, false);
            }

        } else {
            videoUrl = savedInstanceState.getString(VideoItemDetailFragment.VIDEO_URL);
            currentStreamingService = savedInstanceState.getInt(VideoItemDetailFragment.STREAMING_SERVICE);
            arguments = savedInstanceState;
        }

        // Create the detail fragment and add it to the activity
        // using a fragment transaction.
        fragment = new VideoItemDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.videoitem_detail_container, fragment)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        App.checkStartTor(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(VideoItemDetailFragment.VIDEO_URL, videoUrl);
        outState.putInt(VideoItemDetailFragment.STREAMING_SERVICE, currentStreamingService);
        outState.putBoolean(VideoItemDetailFragment.AUTO_PLAY, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this, Home.class));
            finish();
            return true;
        } else {
            return fragment.onOptionsItemSelected(item) ||
                    super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        fragment.onCreateOptionsMenu(menu, getMenuInflater());
        return true;
    }
    public void onBackPressed() {
        startActivity(new Intent(this, Home.class));
        finish();
    }
}
