package org.schabi.cog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.schabi.cog.AnimationUtils.SupportAnimator;
import org.schabi.cog.AnimationUtils.ViewAnimationUtils;
import org.schabi.cog.networkchecker.NetworkChecker;
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

public class Browser extends AppCompatActivity implements View.OnClickListener{

    public int x= 0;
    String urls[] = new String[]{"http://www.cityofgodchristiancentre.org/",
            "http://www.cityofgodchristiancentre.org/component/sermonspeaker/speaker/10001?limitstart=0",
            "http://www.cityofgodchristiancentre.org/contact-us",
            "http://www.cityofgodchristiancentre.org/about-us",
            "https://www.google.com"
    };
    public static WebView google_webview;
    ProgressBar progressBar;
    NetworkChecker checker;
    ActionBar actionBar;
    boolean hidden = true;
    LinearLayout mRevealView;
    private Toolbar toolbar;
    ImageButton home, podcasts, about;
    ImageButton refresh, chat, internet;
    SupportAnimator mAnimator;
    boolean mPressed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        checker=new NetworkChecker(Browser.this);


        inisilizeComponent();
    }

    public void inisilizeComponent() {
        google_webview = (WebView)findViewById(R.id.google_page);
        google_webview.getSettings().setJavaScriptEnabled(true);
        google_webview.getSettings().setLoadWithOverviewMode(true);
        google_webview.getSettings().setUseWideViewPort(true);
        google_webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        google_webview.setScrollbarFadingEnabled(true);
        google_webview.getSettings().setBuiltInZoomControls(true);
        google_webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        //google_webview.getSettings().setAllowContentAccess(true);
        WebSettings webSettings = google_webview.getSettings();
       // webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
       // webSettings.setLoadWithOverviewMode(true);
        google_webview.setWebChromeClient(new WebChromeClient());

        progressBar=(ProgressBar) findViewById(R.id.progressBar1);
        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);
        podcasts = (ImageButton) findViewById(R.id.podcasts);
        home = (ImageButton) findViewById(R.id.home);
        about = (ImageButton) findViewById(R.id.about);
        chat = (ImageButton) findViewById(R.id.contact);
        internet = (ImageButton) findViewById(R.id.google);
        refresh = (ImageButton) findViewById(R.id.renew);

        refresh.setOnClickListener(this);
        home.setOnClickListener(this);
        chat.setOnClickListener(this);
        about.setOnClickListener(this);
        internet.setOnClickListener(this);
        podcasts.setOnClickListener(this);

        //google_webview.setWebViewClient(new WebViewSearch());

        startWebView(urls[x]);
        try{
            actionBar = getSupportActionBar();
            actionBar.setTitle("Browser");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }catch (Exception e){}
    }

    public void Back(){
        startActivity(new Intent(this, Home.class));
        finish();
    }

    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link

        google_webview.setWebViewClient(new WebViewClient() {


            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                // heading.setText(google_webview.getTitle());
                return true;
            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {


                progressBar.setVisibility(View.VISIBLE);

            }

            public void onPageFinished(WebView view, String url) {
                actionBar.setTitle(google_webview.getTitle());
                progressBar.setVisibility(View.GONE);

            }

        });

        // Javascript inabled on webview
        // Other webview options
        /*
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        */

        /*
         String summary = "<html><body>You scored <b>192</b> points.</body></html>";
         webview.loadData(summary, "text/html", null);
         */

        //Load url in webview
        google_webview.loadUrl(urls[x]);


    }

    public void onBackPressed() {
            // Let the system handle the back button
            startActivity(new Intent(this, Home.class));
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browser, menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.download_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.dash) {
            Fab();
            return true;
        }
        if(id==android.R.id.home) {
            startActivity(new Intent(this, Home.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.home:
                x=0;
                startWebView(urls[x]);
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                mPressed=false;

                break;

            case R.id.podcasts:
                x=1;
                startWebView(urls[x]);
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                mPressed=false;

                break;

            case R.id.about:
                x=3;
                startWebView(urls[x]);
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                mPressed=false;

                break;

            case R.id.renew:
                startWebView(urls[x]);
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                mPressed=false;

                break;

            case R.id.contact:
                x=2;
                startWebView(urls[x]);
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                mPressed=false;

                break;

            case R.id.google:
                x=4;
                startWebView(urls[x]);
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                mPressed=false;

                break;

        }

    }

    public void Fab() {
        if (!mPressed) {
            mPressed = true;


            int cx = (mRevealView.getLeft() + mRevealView.getRight());
            int cy = mRevealView.getTop();
            int endradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());
            mAnimator =
                    ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, endradius);
            mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimator.setDuration(400);


            if (hidden) {
                mRevealView.setVisibility(View.VISIBLE);
                mAnimator.start();
                hidden = false;

            }
        } else {
            if (mAnimator != null && !mAnimator.isRunning()) {
                mAnimator = mAnimator.reverse();
                mAnimator.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        mRevealView.setVisibility(View.INVISIBLE);
                        hidden = true;
                        mPressed = false;

                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }

                });
                mAnimator.start();
            }


        }

    }
    public void Oops(){

        String summary = "<html><body><center><font size=60 color=red><b>Oops! Something's Wrong :(> (please check your internet connection</b></font></center></body></html>";
        google_webview.loadData(summary, "text/html", "UTF-8");
    }


}
