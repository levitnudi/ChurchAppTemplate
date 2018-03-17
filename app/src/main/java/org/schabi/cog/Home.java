package org.schabi.cog;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

import org.schabi.cog.adapter.CircleImageView;
import org.schabi.cog.adapter.CircleLayout;
import org.schabi.cog.download.MainActivity;
import org.schabi.cog.extractor.ServiceList;
import org.schabi.cog.extractor.StreamingService;
import org.schabi.cog.syncengine.Realtime;
import org.schabi.newpipe.R;

import java.util.ArrayList;
import java.util.Calendar;
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
public class Home extends AppCompatActivity implements CircleLayout.OnItemSelectedListener,
        CircleLayout.OnItemClickListener, CircleLayout.OnRotationFinishedListener, CircleLayout.OnCenterClickListener {

    int nextActivity=0;
    SQLiteDatabase db;
    SharedPreferences readblog;
    int currentStreamingServiceId =-1;
    public static final String VIDEO_URL = "video_url";
    public static final String STREAMING_SERVICE = "streaming_service";
    public static final String AUTO_PLAY = "auto_play";
    private AppCompatActivity activity;
    private ActionBarHandler actionBarHandler;
    private ProgressBar progressBar;
    StreamingService streamingService;
    private int streamingServiceId = -1;
    AlertDialog.Builder mAlertDialog;
    private static final float X_SHIFT_DP = 1000;
    private static final float Y_SHIFT_DP = 50;
    private static final float Z_LIFT_DP = 8;
    private static final float ROTATE_DEGREES = 15;
    String ENTERTAINMENT = "https://youtu.be/BkDnI-HhpCA";
    String WEEKLY_TV = "https://youtu.be/xpN6g_QTP4c";
    protected CircleLayout circleLayout;
    protected TextView selectedTextView;

    public AnimatorSet createSet(ArrayList<Animator> items, long startDelay) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(items);
        set.setStartDelay(startDelay);
        return set;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set listeners
        circleLayout = (CircleLayout) findViewById(R.id.circle_layout);
        circleLayout.setOnItemSelectedListener(this);
        circleLayout.setOnItemClickListener(this);
        circleLayout.setOnRotationFinishedListener(this);
        circleLayout.setOnCenterClickListener(this);

        selectedTextView = (TextView) findViewById(R.id.selected_textView);

        String name = null;
        View view = circleLayout.getSelectedItem();
        if (view instanceof CircleImageView) {
            name = ((CircleImageView) view).getName();
        }
        selectedTextView.setText(name);


        db = openOrCreateDatabase("COG", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS media(category TEXT, vidurl TEXT);");

        syncUrls();
        //db = openOrCreateDatabase("COG", Context.MODE_PRIVATE, null);
        //db.execSQL("CREATE TABLE IF NOT EXISTS Blogger(title TEXT, pastor TEXT , date TEXT , image_profile TEXT, image_feed TEXT,  message TEXT);");
        readblog = getSharedPreferences("BLOG", MODE_PRIVATE);
        actionBarHandler = new ActionBarHandler(activity);
        actionBarHandler.setupNavMenu(activity);


        streamingService = null;

        try {
            //------ todo: remove this line when multiservice support is implemented ------
            currentStreamingServiceId = ServiceList.getIdOfService("Youtube");
            streamingService = ServiceList.getService(currentStreamingServiceId);
        } catch (Exception e) {}
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        PreferenceManager.setDefaultValues(this, R.xml.settings, false);



        startBackgroundJob();

        try{
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.app_name);
            //actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setIcon(R.mipmap.ic_launcher);
        }catch (Exception e){}

        // printExpenditure();

        putMedia();
    }



    public void putMedia(){

        Cursor cursor = db.rawQuery("SELECT * FROM media", null);
        cursor.moveToFirst();
        String[] vidlink = new String[]{ENTERTAINMENT, WEEKLY_TV};
        String[] category = new String[]{"others", "weekly"};
        if (cursor.getCount() == 0) {
            for (int i = 0; i<category.length; i++) {
                db.execSQL("INSERT INTO media(category, vidurl) VALUES('" +category[i]+ "','" + vidlink[i] + "');");
                cursor.moveToNext();
            }
        }else{
            cursor.moveToFirst();
            for(int x=0; x<cursor.getCount(); x++) {
                if(cursor.getString(0).contains("others")){
                    ENTERTAINMENT = cursor.getString(1).replaceAll("''", "'");
                }else if(cursor.getString(0).contains("weekly")) {
                    WEEKLY_TV = cursor.getString(1).replaceAll("''", "'");
                }
                //Toast.makeText(getApplicationContext(), "This is enter "+ENTERTAINMENT, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "This is weekly "+WEEKLY_TV, Toast.LENGTH_LONG).show();

                cursor.moveToNext();
            }cursor.close();
        }

    }//cursor.close();






    public void startBackgroundJob(){
        // BroadCase Receiver Intent Object
        Intent alarmIntent = new Intent(getApplicationContext(), Realtime.class);
        // Pending Intent Object
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Alarm Manager Object
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Alarm Manager calls BroadCast for every Ten seconds (10 * 1000), BroadCase further calls service to check if new records are inserted in
        // Remote MySQL DB
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 5000, 10 * 1000, pendingIntent);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.share) {

            Share();
            return true;
        }

        if(id==R.id.info){
         startActivity(new Intent(this, WindowSurface.class));
            finish();
            return true;
        }
       // if(id==R.id.verses){
         //   DailyVerses();
         //  return true;
        //}
        return super.onOptionsItemSelected(item);
    }


    public void Share() {
        final String packageName = getPackageName();
        String share_content = "Hello there, I'm using City of God app to get latest videos, blogs and event posters, don't miss out. Get your own" +
                " from Google playstore, simply follow the link : https://play.google.com/store/apps/details?id="+packageName;
        Intent txtIntent = new Intent(android.content.Intent.ACTION_SEND);
        txtIntent.setType("text/plain");
        txtIntent.putExtra(android.content.Intent.EXTRA_TEXT, share_content);
        startActivity(Intent.createChooser(txtIntent, "City of God"));
    }

    public void syncUrls(){
        Cursor c = db.rawQuery("SELECT * FROM media", null);
        c.moveToFirst();
        //Toast.makeText(getApplicationContext(), c.getCount()+"s", Toast.LENGTH_LONG).show();
            for (int i=0; i<c.getCount(); i++) {
                //title TEXT, date TEXT, imgurl TEXT
                String category =   c.getString(0).replaceAll("''", "'");
                String vid_url=   c.getString(1).replaceAll("''", "'");

                //Toast.makeText(getApplicationContext(), category, Toast.LENGTH_LONG).show();

                c.moveToNext();
            }c.close();
            // Adding request to request queue
        }



    @Override
    public void onItemSelected(View view) {
        final String name;
        if (view instanceof CircleImageView) {
            name = ((CircleImageView) view).getName();
        } else {
            name = null;
        }

        selectedTextView.setText(name);

        switch (view.getId()) {
            case R.id.weekly:

                break;
            case R.id.gallery:

                break;
            case R.id.blogger:

                break;
            case R.id.online:

                break;
            case R.id.downloads:

                break;

            case R.id.editnotes:

                break;
        }
    }

    @Override
    public void onItemClick(View view) {
        String name = null;
        if (view instanceof CircleImageView) {
            name = ((CircleImageView) view).getName();
        }


        switch (view.getId()) {
            case R.id.online:
                startActivity(new Intent(this, Browser.class));
                /*Intent weeklyintent = new Intent(this, VideoItemDetailActivity.class);
                //detailIntent.putExtra(VideoItemDetailFragment.ARG_ITEM_ID, id);
                weeklyintent.putExtra(VideoItemDetailFragment.VIDEO_URL, WEEKLY_TV);
                weeklyintent.putExtra(VideoItemDetailFragment.STREAMING_SERVICE, currentStreamingServiceId);
                startActivity(weeklyintent);
                finish();*/
                break;
            case R.id.gallery:
                startActivity(new Intent(this, Pinboard.class));
                 finish();
                break;
            case R.id.downloads:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.blogger:
                startActivity(new Intent(this, Blog.class));
                 finish();
                break;
            case R.id.editnotes:
                startActivity(new Intent(this, Notes.class));
                 finish();
                break;
            case R.id.weekly:
                Intent musicintent = new Intent(this, VideoItemDetailActivity.class);
                //detailIntent.putExtra(VideoItemDetailFragment.ARG_ITEM_ID, id);
                musicintent.putExtra(VideoItemDetailFragment.VIDEO_URL, ENTERTAINMENT);
                musicintent.putExtra(VideoItemDetailFragment.STREAMING_SERVICE, currentStreamingServiceId);
                startActivity(musicintent);
                 finish();
                break;
        }
    }

    @Override
    public void onRotationFinished(View view) {
        Animation animation = new RotateAnimation(0, 360, view.getWidth() / 2, view.getHeight() / 2);
        animation.setDuration(250);
        view.startAnimation(animation);
    }

    @Override
    public void onCenterClick() {
        //Toast.makeText(getApplicationContext(), R.string.center_click, Toast.LENGTH_SHORT).show();
    }


    public void onBackPressed() {
      finish();
    }
}
