/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
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

package org.schabi.cog;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

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

public class SuperAwesomeCardFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    int x = 0;
    public int position;
    String tr = null;
    List<String> tableContent = new LinkedList<String>();
    String[] tableArray = null;
    WebView mWebView;
    SharedPreferences  readblog, page;
   // List<DataClass> dataItems = new ArrayList<DataClass>();
    FrameLayout fl;
    Button btn;
    TextView tv;
    String[] MPESA;
    FrameLayout frame;
    List<String> list = new ArrayList<String>();
       public static SuperAwesomeCardFragment newInstance(int position) {
           SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
           Bundle b = new Bundle();
           b.putInt(ARG_POSITION, position);
           f.setArguments(b);
           return f;
       }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        readblog = getActivity().getSharedPreferences("BLOG", getActivity().MODE_PRIVATE);
        page = getActivity().getSharedPreferences("PAGE", getActivity().MODE_PRIVATE);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);

        frame = new FrameLayout(getActivity());
        frame.setLayoutParams(params);

        SharedPreferences.Editor pagenum = page.edit();

        String origin = readblog.getString("blog" + position, "");
        String[] contents = origin.split("#");

        String competeContent = "<html>"+ContentCSS+"<h5>No content was found...</h5></html>";
        if(contents.length>2){
            competeContent  = new DataHelper().generateHtmlContent();
            String profile = contents[3];
            String feed = contents[4];
            competeContent = competeContent.replace("file:///android_asset/profile", profile).replaceAll("''", "'");;
            competeContent = competeContent.replace("file:///android_asset/sharedpic", feed).replaceAll("''", "'");;
        }


        mWebView = new WebView(getActivity());
        // Add javascript support to the webview
        mWebView.getSettings().setJavaScriptEnabled(true);
       // mWebView.setInitialScale(150);
        mWebView.getSettings().setAppCacheEnabled(true);
        //mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // Enable pinch zoom controls on webview
       // mWebView.getSettings().setBuiltInZoomControls(true);

        if(Build.VERSION.SDK_INT>=11){
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        }

            mWebView.loadData(competeContent, "text/html", "UTF-8");

         pagenum.remove("page");
         pagenum.putInt("page", position-1);
         pagenum.commit();
            fl.addView(mWebView);

        return fl;
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
            "img{display : inline ; height : auto; max-width : 100%;}"+
            ".imgwrap{float : left; width : 100px; height : 100px; border-radius : 50%; overflow : hidden;}"+
            ".txtwrap{float : right; margin: 20px 0 10px; font - size: 10px;" +"font - weight: normal; color:#999; top: -1 px; left: -1 px;" +
            "margin-left : 250px; margin-top : -1px;}"+
            " </style>";


	public class  DataHelper{
		StringBuffer html;
		/**
		 * Generate HTML data and append input data stored in asset text file
		 * @param
		 * @return
		 */



        public String generateHtmlContent(){

            String origin = readblog.getString("blog"+position, "");
            String[] contents = origin.split("#");

            //title=0, pastor=1, date=2, profile=3, feed=4, message=5
            //int sizes = contents.length;
           // Toast.makeText(getActivity(), "Array Size is " + sizes+origin, Toast.LENGTH_LONG).show();
			//if(!origin.isEmpty()) {

                String title = contents[0];
                String pastor = contents[1];
                String date = contents[2];
            String message = contents[5];

            title = title.replaceAll("''", "'");
            pastor = pastor.replaceAll("''", "'");
            message = message.replaceAll("''", "'");
            date = date.replaceAll("''", "'");

			String[] array = message.split("\\*");

            int numOfParagraphs = array.length;

            String posted = "Posted : ";
            String author = "Author : ";

                html = new StringBuffer("<html>" + ContentCSS + "<u><h3>" + title + "</h3></u>");
                String body = "<br><div style= float : left>" +
                        "<img src= file:///android_asset/profile" + " width=" +
                        "100" + "height=" + "100" + " alt=" + "image  class=imgwrap </img></div>" +
                        "<h5 style= float : right style= margin-left : 250px>" +author+ pastor + "</h5>" +
                        "<h5 style= float : right style= margin-left : 250px>" +posted+ date + "</h5>" +
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


        public  String getTimeAgo(long timestamp) {

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();//get your local time zone.
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            sdf.setTimeZone(tz);//set time zone.
            String localTime = sdf.format(new Date(timestamp * 1000));
            Date date = new Date();
            try {
                date = sdf.parse(localTime);//get local date
            } catch (ParseException e) {}

            if(date == null) {
                return null;
            }

            long time = date.getTime();

            Date curDate = currentDate();
            long now = curDate.getTime();
            if (time > now || time <= 0) {
                return null;
            }

            int timeDIM = getTimeDistanceInMinutes(time);

            String timeAgo = null;

            if (timeDIM == 0) {
                timeAgo = "less than a minute";
            } else if (timeDIM == 1) {
                return "1 minute";
            } else if (timeDIM >= 2 && timeDIM <= 44) {
                timeAgo = timeDIM + " minutes";
            } else if (timeDIM >= 45 && timeDIM <= 89) {
                timeAgo = "about an hour";
            } else if (timeDIM >= 90 && timeDIM <= 1439) {
                timeAgo = "about " + (Math.round(timeDIM / 60)) + " hours";
            } else if (timeDIM >= 1440 && timeDIM <= 2519) {
                timeAgo = "1 day";
            } else if (timeDIM >= 2520 && timeDIM <= 43199) {
                timeAgo = (Math.round(timeDIM / 1440)) + " days";
            } else if (timeDIM >= 43200 && timeDIM <= 86399) {
                timeAgo = "about a month";
            } else if (timeDIM >= 86400 && timeDIM <= 525599) {
                timeAgo = (Math.round(timeDIM / 43200)) + " months";
            } else if (timeDIM >= 525600 && timeDIM <= 655199) {
                timeAgo = "about a year";
            } else if (timeDIM >= 655200 && timeDIM <= 914399) {
                timeAgo = "over a year";
            } else if (timeDIM >= 914400 && timeDIM <= 1051199) {
                timeAgo = "almost 2 years";
            } else {
                timeAgo = "about " + (Math.round(timeDIM / 525600)) + " years";
            }

            return timeAgo + " ago";
        }

        public  Date currentDate() {
            Calendar calendar = Calendar.getInstance();
            return calendar.getTime();
        }

        private  int getTimeDistanceInMinutes(long time) {
            long timeDistance = currentDate().getTime() - time;
            return Math.round((Math.abs(timeDistance) / 1000) / 60);
        }



		/**
		 * Generate CSS styles
		 * @return
		 */
		private String getCSSForTable(){
			String cssString;
			cssString = "<style>"+
					".TableStyle {"+
					"margin:0px;padding:0px;"+
					"width:100%;"+
					"box-shadow: 10px 10px 5px #888888;"+
					"border:1px solid #07214f;"+

					"-moz-border-radius-bottomleft:0px;"+
					"-webkit-border-bottom-left-radius:0px;"+
					"border-bottom-left-radius:0px;"+

					"-moz-border-radius-bottomright:0px;"+
					"-webkit-border-bottom-right-radius:0px;"+
					"border-bottom-right-radius:0px;"+

					"-moz-border-radius-topright:0px;"+
					"-webkit-border-top-right-radius:0px;"+
					"border-top-right-radius:0px;"+

					"-moz-border-radius-topleft:0px;"+
					"-webkit-border-top-left-radius:0px;"+
					"border-top-left-radius:0px;"+
					"}.TableStyle table{"+
					"border-collapse: collapse;"+
					"border-spacing: 0;"+
					"width:100%;"+
					"height:100%;"+
					"margin:0px;padding:0px;"+
					"}.TableStyle tr:last-child td:last-child {"+
					"-moz-border-radius-bottomright:0px;"+
					"-webkit-border-bottom-right-radius:0px;"+
					"border-bottom-right-radius:0px;"+
					"}"+
					".TableStyle table tr:first-child td:first-child {"+
					"-moz-border-radius-topleft:0px;"+
					"-webkit-border-top-left-radius:0px;"+
					"border-top-left-radius:0px;"+
					"}"+
					".TableStyle table tr:first-child td:last-child {"+
					"-moz-border-radius-topright:0px;"+
					"-webkit-border-top-right-radius:0px;"+
					"border-top-right-radius:0px;"+
					"}.TableStyle tr:last-child td:first-child{"+
					"-moz-border-radius-bottomleft:0px;"+
					"-webkit-border-bottom-left-radius:0px;"+
					"border-bottom-left-radius:0px;"+
					"}.TableStyle tr:hover td{"+

					"}"+
					".TableStyle tr:nth-child(odd){ background-color:#ffffff; }"+
					".TableStyle tr:nth-child(even)    { background-color:#a6d3ed; }.TableStyle td{"+
					"vertical-align:middle;"+


					"border:1px solid #07214f;"+
					"border-width:0px 1px 1px 0px;"+
					"text-align:left;"+
					"padding:7px;"+
					"font-size:12px;"+
					"font-family:Arial;"+
					"font-weight:normal;"+
					"color:#020101;"+
					"}.TableStyle tr:last-child td{"+
					"border-width:0px 1px 0px 0px;"+
					"}.TableStyle tr td:last-child{"+
					"border-width:0px 0px 1px 0px;"+
					"}.TableStyle tr:last-child td:last-child{"+
					"border-width:0px 0px 0px 0px;"+
					"}"+
					".TableStyle tr:first-child td{"+
					"background:-o-linear-gradient(bottom, #629edb 5%, #003f7f 100%);	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #629edb), color-stop(1, #003f7f) );"+
					"background:-moz-linear-gradient( center top, #629edb 5%, #003f7f 100% );"+
					"filter:progid:DXImageTransform.Microsoft.gradient(startColorstr=\"#629edb\", endColorstr=\"#003f7f\");	background: -o-linear-gradient(top,#629edb,003f7f);"+

					"background-color:#629edb;"+
					"border:0px solid #07214f;"+
					"text-align:center;"+
					"border-width:0px 0px 1px 1px;"+
					"font-size:15px;"+
					"font-family:Arial;"+
					"font-weight:bold;"+
					"color:#ffffff;"+
					"}"+
					".TableStyle tr:first-child:hover td{"+
					"background:-o-linear-gradient(bottom, #629edb 5%, #003f7f 100%);	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #629edb), color-stop(1, #003f7f) );"+
					"background:-moz-linear-gradient( center top, #629edb 5%, #003f7f 100% );"+
					"filter:progid:DXImageTransform.Microsoft.gradient(startColorstr=\"#629edb\", endColorstr=\"#003f7f\");	background: -o-linear-gradient(top,#629edb,003f7f);"+

					"background-color:#629edb;"+
					"}"+
					".TableStyle tr:first-child td:first-child{"+
					"border-width:0px 0px 1px 0px;"+
					"}"+
					".TableStyle tr:first-child td:last-child{"+
					"border-width:0px 0px 1px 1px;"+
					"}"+
					"</style>";
			return cssString;
		}
	}


}