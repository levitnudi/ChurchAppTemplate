package org.schabi.cog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.schabi.cog.download.MainActivity;
import org.schabi.cog.syncengine.ServerFetcher;
import org.schabi.newpipe.R;
import org.schabi.cog.pager.PagerSlidingTabStrip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

import us.shandian.giga.get.DownloadManagerImpl;

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
public class Blog extends FragmentActivity {

    private final Handler handler = new Handler();
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private Drawable oldBackground = null;
    private int currentColor = 0xFF666666;
    SQLiteDatabase db;
    SharedPreferences page,readblog;
    AlertDialog.Builder mAlertDialog;
    String[] title = new String[]{"PRINT RECEIVED CASH STATEMENT", "PRINT M$PENT STATEMENT"};
    String PROJECT_NAME = "CityofGod";
    int pagenum = 0;


    public void onBackPressed() {
        startActivity(new Intent(this, Home.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        Intent intnt = new Intent(this, ServerFetcher.class);
        startService(intnt);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        readblog = getSharedPreferences("BLOG", MODE_PRIVATE);
        page = getSharedPreferences("PAGE", MODE_PRIVATE);

        db = openOrCreateDatabase("COG", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Blogger(title TEXT, author TEXT , date TEXT , profile TEXT, feed TEXT,  message TEXT);");

        //dataItems = new ArrayList<DataClass>();

        updateData();

        //addToPref();
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);

        changeColor(currentColor);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        createFolders();
    }


    public void createFolders(){
        File exportDir = new File(Environment.getExternalStorageDirectory() + File.separator + "COG");

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
    }


    private void changeColor(int newColor) {
        tabs.setIndicatorColor(newColor);

        // change ActionBar color just if an ActionBar is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});

            if (oldBackground == null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                } else {
                    //getActionBar().setBackgroundDrawable(ld);
                }

            } else {

                TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});

                // workaround for broken ActionBarContainer drawable handling on
                // pre-API 17 builds
                // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                } else {
                    //getActionBar().setBackgroundDrawable(td);
                }

                td.startTransition(200);

            }

            oldBackground = ld;

            // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
            //getActionBar().setDisplayShowTitleEnabled(false);
            //getActionBar().setDisplayShowTitleEnabled(true);

        }

        currentColor = newColor;

    }

    public void onColorClicked(View v) {

        int color = Color.parseColor(v.getTag().toString());
        changeColor(color);


        if (v.getId() == R.id.print) {
            //ExportDatabaseEXCVSTask exp = new ExportDatabaseEXCVSTask(this);
            //exp.exportDatabaseIntoCSV();
           // position=1;
            //BuildTable();
            displayPrintManager();

        }

        if (v.getId() == R.id.basket) {
           // App.configureTor(true);
            startActivity(new Intent(this, Profile.class));
            finish();
            //displayProfile();
        }

    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        changeColor(currentColor);
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            //getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };

    public class MyPagerAdapter extends FragmentPagerAdapter {
        Cursor c = db.rawQuery("SELECT * FROM Blogger", null);
        int PAGE_COUNT = c.getCount();
        public String[] TITLES = new String[PAGE_COUNT];



        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            populate();
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return SuperAwesomeCardFragment.newInstance(position);
        }

        public void populate(){
            c.moveToFirst();
            for(int i=0; i<PAGE_COUNT; i++){
                int counting = 1+i;
                TITLES[i] = "Page "+counting;
                c.moveToNext();
            }
            if(c.getCount()==0){
                TITLES = new String[1];
                TITLES[0] = "No content was found, please check your internet connection";
            }
        }

    }



    public void updateData() {
        SharedPreferences.Editor sum = readblog.edit();
        // SharedPreferences.Editor size = readblog.edit();

        Cursor c = db.rawQuery("SELECT * FROM Blogger", null);
        c.moveToFirst();
        String splitter = "#";
        int SIZE_OBSERVER = c.getCount();
        // Toast.makeText(this, "DB Size is " + SIZE_OBSERVER, Toast.LENGTH_LONG).show();
        for (int i = 0; i < SIZE_OBSERVER; i++) {
            // Toast.makeText(this, "DB Context" + c.getString(0) + c.getString(1) + c.getString(2), Toast.LENGTH_LONG).show();
            String content = c.getString(0)+splitter+c.getString(1)+splitter+c.getString(2)+
                    splitter+c.getString(3)+splitter+c.getString(4)+splitter+c.getString(5);
            sum.remove("blog" + i);
            sum.putString("blog" + i, content);
            c.moveToNext();
        } sum.commit();

    }




    public void displayPrintManager() {

        pagenum = page.getInt("page", 0);
        String origin = readblog.getString("blog" + pagenum, "");
        String[] contents = origin.split("#");

        String competeContent = "<html>"+ContentCSS+"<h5>No content was found...</h5></html>";
        if(contents.length>2){
            competeContent  = new DataHelper().generateHtmlContent();
            String profile = contents[3];
            String feed = contents[4];
            competeContent = competeContent.replace("file:///android_asset/profile", profile).replaceAll("''", "'")
                    .replace("file:///android_asset/sharedpic", feed).replaceAll("''", "'");
        }

        final WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.table_render, null);

        view.getSettings().setJavaScriptEnabled(true);
        view.setInitialScale(180);
        view.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        //view.getSettings().setAppCacheEnabled(true);
        view.getSettings().setUseWideViewPort(true);
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // Enable pinch zoom controls on webview
        //view.getSettings().setBuiltInZoomControls(true);


        competeContent = competeContent.replace("file:///android_asset/profile", "http://notonlab.com/AppFiles/Pastors/KennethOkaeme.jpeg")
                .replace("file:///android_asset/sharedpic", "http://notonlab.com/AppFiles/Pastors/worshipteam.jpg");

        view.loadData(competeContent, "text/html", "UTF-8");

        TextView tv = new TextView(this);
        tv.setText(" Printer Manager ");
        tv.setTextColor(getResources().getColor(R.color.liteblue));
        mAlertDialog = new AlertDialog.Builder(this, R.style.OrangeTheme)
                .setView(view)
                .setCustomTitle(tv)
                .setCancelable(false)
                .setPositiveButton("Print", new Dialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {


                                    Picture pic = view.capturePicture();
                                    view.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                                            , View.MeasureSpec.UNSPECIFIED);
                                    view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                                    // view.setDrawingCacheEnabled(true);
                                    //view.buildDrawingCache();
                                    Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                                    Canvas canvas = new Canvas(bitmap);
                                    pic.draw(canvas);
                                    view.draw(canvas);

                                    if (bitmap != null) {
                                        try {
                                            String type_name = "blog";


                                            Document doc = new Document();
                                            File pdf = new File(Environment.getExternalStorageDirectory() + File.separator + "COG" + File.separator + "ALLFILES/Files", PROJECT_NAME + type_name + ".pdf");
                                            String pdfpath = pdf.getAbsolutePath();
                                            PdfWriter.getInstance(doc, new FileOutputStream(pdfpath));
                                            doc.open();
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                            byte[] byteArray = stream.toByteArray();
                                            Image image = Image.getInstance(byteArray);
                                            //image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getWidth());
                                            image.setAlignment(Element.ALIGN_CENTER);

                                            doc.addCreationDate();
                                            // doc.
                                            PdfPTable table = new PdfPTable(1);
                                            table.setWidthPercentage(100);
                                            PdfPCell cell = new PdfPCell(image, true);
                                            //cell.setH
                                            cell.setBorder(PdfPCell.ALIGN_CENTER);
                                            cell.setPadding(5);

                                            //cell.setImage(image.scaleToFit(750f, 750f));
                                            table.addCell(cell);
                                            doc.add(table);
                                            doc.close();
                                            // writer.close();
                                            Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_LONG).show();
                                           bitmap.recycle();
                                            bitmap = null;
                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (NullPointerException npe) {
                                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                ).setNeutralButton("Share", new Dialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Close the activity as they have declined the EULA
                                try {


                                    Picture pic = view.capturePicture();
                                    view.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                                            , View.MeasureSpec.UNSPECIFIED);
                                    view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                                    // view.setDrawingCacheEnabled(true);
                                    //view.buildDrawingCache();
                                    Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                                    Canvas canvas = new Canvas(bitmap);
                                    pic.draw(canvas);
                                    view.draw(canvas);

                                    if (bitmap != null) {
                                        try {
                                            String type_name = "blog";


                                            Document doc = new Document();
                                            File pdf = new File(Environment.getExternalStorageDirectory() + File.separator + "COG" + File.separator + "ALLFILES/Files", PROJECT_NAME + type_name + ".pdf");
                                            String pdfpath = pdf.getAbsolutePath();
                                            PdfWriter.getInstance(doc, new FileOutputStream(pdfpath));
                                            doc.open();
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                            byte[] byteArray = stream.toByteArray();
                                            Image image = Image.getInstance(byteArray);
                                            //image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getWidth());
                                            image.setAlignment(Element.ALIGN_CENTER);

                                            doc.addCreationDate();
                                            // doc.
                                            PdfPTable table = new PdfPTable(1);
                                            table.setWidthPercentage(100);
                                            PdfPCell cell = new PdfPCell(image, true);
                                            //cell.setH
                                            cell.setBorder(PdfPCell.ALIGN_CENTER);
                                            cell.setPadding(5);

                                            //cell.setImage(image.scaleToFit(750f, 750f));
                                            table.addCell(cell);
                                            doc.add(table);
                                            doc.close();

                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            Uri picUri = Uri.parse(pdf.getAbsolutePath());
                                            intent.putExtra(Intent.EXTRA_TEXT,  PROJECT_NAME + " Shared from City of God App.");
                                            intent.putExtra(Intent.EXTRA_STREAM, picUri);
                                            intent.setDataAndType(picUri, "application/pdf");
                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            startActivity(Intent.createChooser(intent, "City of God"));
                                            // writer.close();
                                           // Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_LONG).show();

                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (NullPointerException npe) {
                                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
                                }

                            }

                        }

                ).setNegativeButton("Cancel", new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the activity as they have declined the EULA

                    }

                });
        mAlertDialog.show();

    }



    String ContentCSS = "<style> @font-face { font - family: "+"ebrima"+";"+" src: url('file:///android_asset/fonts/ebrima.TTF'); } body{ font - family: "+"Times New Roman"+", Times, "+"serif"+
            "; font - size: 16px; word - wrap: normal; } p { line - height:22px; padding-bottom:10px; }" +
            " h3{ Times New Roman"+", Times,  serif"+"; color:#4d7de1;" +
            " padding-bottom:3px; border -bottom: 1px solid #444343; font - weight:650; } td { padding:10px; border: 1px solid #2c3e4c; }" +
            " h5 { margin: 20px 0 10px; font - size: 10px; " +
            "font - weight: normal; color:#999; top: -1 px; left: -1 px;}" +
            " h2 { margin: 0px 0px 0px; font - size: 10px; font - weight: normal; color:#7a6bf8; font - family: "+"Times New Roman"
            +"; } /*blue*/ input[type='submit']{ width: 100px; height: 25px; border -radius : 5px; background-color: #4d7de1; border -color: #294c89; color:#fff; margin: 20px 0 0px; background-image:" +
            " url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/Pgo8c3ZnIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSIgdmlld0JveD0iMCAwIDEgMSIgcHJlc2VydmVBc3B" +
            "lY3RSYXRpbz0ibm9uZSI+CiAgPGxpbmVhckdyYWRpZW50IGlkPSJncmFkLXVjZ2ctZ2VuZXJhdGVkIiBncmFkaWVudFVuaXRzPSJ1c2VyU3BhY2VPblVzZSIgeDE9IjAlIiB5MT0iMCUiIHgyPSIwJSIgeTI9IjEwMCUiPgogICAgPHN0b3Agb2Zmc2V0PSIw" +
            "JSIgc3RvcC1jb2xvcj0iIzUzODRlZCIgc3RvcC1vcGFjaXR5PSIxIi8+CiAgICA8c3RvcCBvZmZzZXQ9IjEwMCUiIHN0b3AtY29sb3I9IiMyYzU2OWYiIHN0b3Atb3BhY2l0eT0iMSIvPgogIDwvbGluZWFyR3JhZGllbnQ+CiAgPHJlY3QgeD0iMCIgeT0iMCI" +
            "gd2lkdGg9IjEiIGhlaWdodD0iMSIgZmlsbD0idXJsKCNncmFkLXVjZ2ctZ2VuZXJhdGVkKSIgLz4KPC9zdmc+); background-image: -moz-linear-gradient(top,  rgba(83,132,237,1) 0%, rgba(44,86,159,1) 100%); background-image:" +
            " -webkit-linear-gradient(top,  rgba(83,132,237,1) 0%,rgba(44,86,159,1) 100%); background-image: linear-gradient(top,  rgba(83,132,237,1) 0%,rgba(44,86,159,1) 100%); } /*red*/ input[type='reset']{ width:" +
            " 100px; height: 25px; border -radius : 5px; background-color: #e8311f; border -color: #9d1d14; color:#fff; margin: 20px 20px 0px; background-image: url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA" +
            "/Pgo8c3ZnIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSIgdmlld0JveD0iMCAwIDEgMSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+CiAgPGxpbmVhckdyYWRpZW50IGlkPSJncmFkLXVjZ2ctZ2VuZXJhdGVk" +
            "IiBncmFkaWVudFVuaXRzPSJ1c2VyU3BhY2VPblVzZSIgeDE9IjAlIiB5MT0iMCUiIHgyPSIwJSIgeTI9IjEwMCUiPgogICAgPHN0b3Agb2Zmc2V0PSIwJSIgc3RvcC1jb2xvcj0iI2Y5NzQ1OCIgc3RvcC1vcGFjaXR5PSIxIi8+CiAgICA8c3RvcCBvZmZzZXQ9IjEwMCUiIHN0b3AtY" +
            "29sb3I9IiNkNzIzMTIiIHN0b3Atb3BhY2l0eT0iMSIvPgogIDwvbGluZWFyR3JhZGllbnQ+CiAgPHJlY3QgeD0iMCIgeT0iMCIgd2lkdGg9IjEiIGhlaWdodD0iMSIgZmlsbD0idXJsKCNncmFkLXVjZ2ctZ2VuZXJhdGVkKSIgLz4KPC9zdmc+); background-image: " +
            "-moz-linear-gradient(top,  rgba(249,116,88,1) 0%, rgba(215,35,18,1) 100%); background-image: -webkit-linear-gradient(top,  rgba(249,116,88,1) 0%,rgba(215,35,18,1) 100%); background-image: linear-gradient(top, " +
            " rgba(249,116,88,1) 0%,rgba(215,35,18,1) 100%); } .erb-image-wrapper{ max - width:100%; height:auto; position: relative; display:block; margin:0 auto; } .erb-image-wrapper img{ max - width:100% !important; height:auto;" +
            " display:block; }" +
            ".imgwrap{float : left; width : 75px; height : 75px; border-radius : 50%; overflow : hidden; }"+
            ".txtwrap{float : right; margin: 20px 0 10px; font - size: 10px;" +"font - weight: normal; color:#999; top: -1 px; left: -1 px;" +
            "margin-left : 235px}"+
            " </style>";



    public class  DataHelper{
        StringBuffer html;
        /**
         * Generate HTML data and append input data stored in asset text file
         * @param
         * @return
         */

        public String generateHtmlContent(){
            pagenum = page.getInt("page", 0);
            String origin = readblog.getString("blog"+pagenum, "");
            String[] contents = origin.split("#");

            //title=0, pastor=1, date=2, profile=3, feed=4, message=5
            //int sizes = contents.length;
            // Toast.makeText(getActivity(), "Array Size is " + sizes+origin, Toast.LENGTH_LONG).show();
            //if(!origin.isEmpty()) {

            String title = contents[0].replaceAll("''", "'");;
            PROJECT_NAME = title;
            String pastor = contents[1].replaceAll("''", "'");;
            String date = contents[2].replaceAll("''", "'");;

            String message = contents[5].replaceAll("''", "'");;

            String[] array = message.split("\\*");

            int numOfParagraphs = array.length;
            String author = "Author : ";

            html = new StringBuffer("<html>" + ContentCSS + "<u><h3>" + title + "</h3></u>");
            String body = "<br><div>" +
                    "<img src= file:///android_asset/profile" + " width=" +
                    "75" + "height=" + "75" + " alt=" + "image  class=imgwrap </img></div>" +
                    "<h5>" +author+ pastor + "</h5>" +
                    "</br>";


            html.append(body);


            html.append("<body>");
            for (int i = 0; i < numOfParagraphs; i++) {
                html.append("<p>" + array[i] + "</p>");
            }
            html.append("</body>");

            String footer = "<br><div class=" + "erb-image-wrapper>" +
                    "<td><img src= file:///android_asset/sharedpic" +
                    " alt=" + "image" + " align=" + "center/>" + "</td> </div></br>";

            html.append(footer);


            html.append("<h5>&copy; 2016 City of God Media.</h5>");


            return html.toString();

        }

 }
}