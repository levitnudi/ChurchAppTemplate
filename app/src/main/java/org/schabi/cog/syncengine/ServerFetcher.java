package org.schabi.cog.syncengine;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.schabi.cog.App;
import org.schabi.cog.Home;
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

public class ServerFetcher extends Service {
    SQLiteDatabase db;
    String[] URL_ITEM =new String[]{"http://cityofgodchristiancentre.org/CityofGodApp/godofcity/getpinboard.php",
                                    "http://cityofgodchristiancentre.org/CityofGodApp/godofcity/getBlog.php",
                                     "http://cityofgodchristiancentre.org/CityofGodApp/godofcity/getVideos.php",
                                    "http://cityofgodchristiancentre.org/CityofGodApp/godofcity/getVerses.php"};

    String TITLE = "City of God Media";

    int notecount = 0;
    public ServerFetcher() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        	//Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStart(Intent intent, int startId) {


        db = openOrCreateDatabase("COG", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS media(category TEXT, vidurl TEXT);");

        db = openOrCreateDatabase("COG", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS posters(title TEXT, date TEXT, imgurl TEXT);");

        db = openOrCreateDatabase("COG", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Blogger(title TEXT, author TEXT , date TEXT , profile TEXT, feed TEXT,  message TEXT);");

        fetchBlog();
        fetchImages();
        fetchMedia();

    }

    @Override
    public void onDestroy() {
        //System.exit(0);
    }


    private void fetchBlog() {

        JsonArrayRequest req = new JsonArrayRequest(URL_ITEM[1],
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Cursor c = db.rawQuery("SELECT * FROM Blogger", null);
                        c.moveToFirst();
                        if(response.length()<c.getCount()){
                            db.execSQL("DELETE FROM Blogger");
                        }c.close();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);

                                String title = object.getString("title").replaceAll("'", "''");
                                String pastor = object.getString("author").replaceAll("'", "''");
                                String date = object.getString("timestamp").replaceAll("'", "''");
                                String profile = object.getString("profile").replaceAll("'", "''");
                                String feed = object.getString("feed").replaceAll("'", "''");
                                String message = object.getString("message").replaceAll("'", "''");


                                Cursor cursor =  db.rawQuery("SELECT * FROM Blogger WHERE title='" + title+ "'", null);
                                cursor.moveToFirst();

                                if(response.length()<cursor.getCount()){
                                    db.execSQL("DELETE FROM Blogger");
                                }

                                if(cursor.getCount()==0) {
                                    //Toast.makeText(getApplicationContext(), title + pastor + date + profile, Toast.LENGTH_LONG).show();
                                    db.execSQL("INSERT INTO Blogger(title, author, date, profile, feed, message) VALUES('" + title + "','" + pastor + "' ,'" + date + "','" + profile + "','" + feed + "','" + message + "');");
                                    if(i==response.length()-1) {
                                        Note(getApplicationContext(), message, notecount++);
                                        TITLE = title;
                                    }
                                }else{
                                    db.execSQL("UPDATE Blogger SET date='"+date+"',message='"+
                                            message+"',feed='"+feed+"',profile='"+profile+"',author='"+pastor+"' WHERE title='"+title+"'");

                                    //"'newValue' WHERE id=6 ");
                                }
                                cursor.moveToNext();
                                cursor.close();

                            } catch (JSONException e) {
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Adding request to request queue
        App.getInstance().addToRequestQueue(req);
    }


    private void fetchImages() {

        JsonArrayRequest req = new JsonArrayRequest(URL_ITEM[0],
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Cursor c = db.rawQuery("SELECT * FROM posters", null);
                        c.moveToFirst();
                        if(response.length()<c.getCount()){
                            db.execSQL("DELETE FROM posters");
                        }c.close();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                String title = object.getString("name").replaceAll("'", "''");
                                String img_url = object.getString("url").replaceAll("'", "''");
                                String time_stamp = object.getString("timestamp").replaceAll("'", "''");
                                //Toast.makeText(getApplicationContext(), title, Toast.LENGTH_LONG).show();
                                Cursor cursor = db.rawQuery("SELECT * FROM posters WHERE title='" + title + "'", null);
                                cursor.moveToFirst();
                                if (cursor.getCount() == 0) {

                                    db.execSQL("INSERT INTO posters(title, date, imgurl) VALUES('" + title + "','" + time_stamp + "' ,'" + img_url + "');");
                                    if(i==response.length()-1) {
                                        Note(getApplicationContext(), title, notecount++);
                                        TITLE = title;
                                    }
                                } else {
                                    db.execSQL("UPDATE posters SET date='" + time_stamp + "',imgurl='" +
                                            img_url+ "' WHERE title='" + title + "'");

                                    //"'newValue' WHERE id=6 ");
                                }
                                cursor.moveToNext();
                                cursor.close();


                            } catch (JSONException e) {
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Adding request to request queue
        App.getInstance().addToRequestQueue(req);
    }



    private void fetchMedia() {

        JsonArrayRequest req = new JsonArrayRequest(URL_ITEM[2],
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Cursor c = db.rawQuery("SELECT * FROM media", null);
                        c.moveToFirst();
                        if(response.length()<c.getCount()){
                            db.execSQL("DELETE FROM media");
                        }c.close();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                String category = object.getString("category").replaceAll("'", "''");
                                String vid_url = object.getString("url").replaceAll("'", "''");

                                Cursor cursor =  db.rawQuery("SELECT * FROM media WHERE category='" + category+ "'", null);
                                cursor.moveToFirst();
                                if(cursor.getCount()==0) {
                                      //  Toast.makeText(getApplicationContext(), category+vid_url, Toast.LENGTH_LONG).show();
                                    db.execSQL("INSERT INTO media(category, vidurl) VALUES('" + category + "','" + vid_url + "');");
                                    if(i==response.length()-1) {
                                        Note(getApplicationContext(), "Check out our new video", notecount++);
                                        TITLE = "New Video!";
                                    }
                                }else{
                                    db.execSQL("UPDATE media SET vidurl='"+vid_url+"' WHERE category='"+category+"'");

                                    //"'newValue' WHERE id=6 ");
                                }
                                cursor.moveToNext();
                                cursor.close();


                            } catch (JSONException e) {
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Adding request to request queue
        App.getInstance().addToRequestQueue(req);
    }

   public void Note(Context context, String content, int number){
       // define sound URI, the sound to be played when there's a notification

       Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
       Intent intent = new Intent(this, Home.class);
       PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
       Notification mNotification = new Notification.Builder(context)
               .setContentTitle(TITLE)
               .setContentText(content)
               .setSmallIcon(R.drawable.ic_stat_notify)
               .setContentIntent(pIntent)
               .setSound(soundUri)
               .addAction(R.drawable.ic_stat_notify, "View", pIntent)
               .addAction(0, "Remind", pIntent)
               .build();
       NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

       notificationManager.notify(0, mNotification);

   }
    public void cancelNotification(int notificationId){


        if (Context.NOTIFICATION_SERVICE!=null) {

            String ns = Context.NOTIFICATION_SERVICE;

            NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);

            nMgr.cancel(notificationId);

        }

    }

}