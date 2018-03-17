package org.schabi.cog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
//import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.schabi.cog.adapter.GalleryAdapter;
import org.schabi.cog.model.Image;
import org.schabi.cog.syncengine.ServerFetcher;
import org.schabi.newpipe.R;

import java.util.ArrayList;

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

public class Pinboard extends AppCompatActivity {

    private String TAG = Pinboard.class.getSimpleName();
    private static final String endpoint = "http://cityofgodchristiancentre.org/CityofGodApp/godofcity/getpinboard.php";
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        db = openOrCreateDatabase("COG", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS posters(title TEXT, date TEXT, imgurl TEXT);");
        Intent intnt = new Intent(this, ServerFetcher.class);
        startService(intnt);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Gallery");
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        fetchImages();
    }

    private void fetchImages() {
        pDialog.setMessage("Loading gallery...");
        pDialog.setCancelable(true);
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //pDialog.hide();

                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setName(object.getString("name"));
                                image.setLarge(object.getString("url"));
                                image.setSmall(object.getString("url"));
                                image.setMedium(object.getString("url"));
                                image.setTimestamp(object.getString("timestamp"));

                                String title = object.getString("name").replaceAll("'", "''");
                                String img_url = object.getString("url").replaceAll("'", "''");
                                String time_stamp = object.getString("timestamp").replaceAll("'", "''");
                                //Toast.makeText(getApplicationContext(), title, Toast.LENGTH_LONG).show();
                                Cursor cursor = db.rawQuery("SELECT * FROM posters WHERE title='" + title + "'", null);
                                cursor.moveToFirst();
                                if (cursor.getCount() == 0) {

                                    db.execSQL("INSERT INTO posters(title, date, imgurl) VALUES('" + title + "','" + time_stamp + "' ,'" + img_url + "');");
                                } else {
                                    db.execSQL("UPDATE posters SET date='" + time_stamp + "',imgurl='" +
                                            img_url+ "' WHERE title='" + title + "'");

                                    //"'newValue' WHERE id=6 ");
                                }
                                cursor.moveToNext();
                                cursor.close();
                                images.add(image);

                            } catch (JSONException e) {
                            }

                            if (pDialog.isShowing()) {
                                pDialog.setCancelable(true);
                                pDialog.cancel();
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                       // Toast.makeText(getApplicationContext(), "Images loading, please wait...", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // pDialog.hide();
                syncUrls();
            }
        });

        // Adding request to request queue
        App.getInstance().addToRequestQueue(req);
    }


    public void syncUrls() {

        Cursor c = db.rawQuery("SELECT * FROM posters", null);
        c.moveToFirst();
       // Toast.makeText(getApplicationContext(), "Hey" + c.getCount(), Toast.LENGTH_LONG).show();
        if (c.getCount() == 0) {
            fetchImages();
        } else {
            images.clear();
            pDialog.setCancelable(true);
            pDialog.cancel();
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                //title TEXT, date TEXT, imgurl TEXT
                String title = c.getString(0).replaceAll("''", "'");
                String time = c.getString(1).replaceAll("''", "'");
                String img_url = c.getString(2).replaceAll("''", "'");
                Image image = new Image();
                image.setName(title);
                image.setLarge(img_url);
                image.setSmall(img_url);
                image.setMedium(img_url);
                image.setTimestamp(time);

               // Toast.makeText(getApplicationContext(), title, Toast.LENGTH_LONG).show();
                images.add(image);
                c.moveToNext();
            }
            c.close();
           // Toast.makeText(getApplicationContext(), "Images loading, please wait...", Toast.LENGTH_LONG).show();
            mAdapter.notifyDataSetChanged();

            // Adding request to request queue
        }

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.download_menu, menu);
        getMenuInflater().inflate(R.menu.pinboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == android.R.id.home) {
            startActivity(new Intent(this, Home.class));
            finish();
            return true;
        }
        if (id == R.id.refresh) {
          fetchImages();
            //Toast.makeText(getApplicationContext(), "Running", Toast.LENGTH_LONG).show();
            return true;
        }

        return true;
    }



    public void onBackPressed() {
        startActivity(new Intent(this, Home.class));
        finish();
    }
}
