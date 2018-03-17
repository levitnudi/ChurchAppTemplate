package org.schabi.cog;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.LinearLayout;
import android.widget.TextView;

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

/**
 * Demonstrates how to take over the Surface from a window to do direct
 * drawing to it (without going through the view hierarchy).
 */
public class WindowSurface extends Activity implements SurfaceHolder.Callback2 {
    DrawingThread mDrawingThread;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Tell the activity's window that we want to do our own drawing
        // to its surface.  This prevents the view hierarchy from drawing to
        // it, though we can still add views to capture input if desired.

        LinearLayout layout = new LinearLayout(this);
        TextView tv = new TextView(this);
        tv.setTextColor(Color.parseColor("#DAA520"));
        tv.setTextSize(20);
        tv.setText("City of God App");
        layout.addView(tv);

        setContentView(layout);

        getWindow().takeSurface(this);
        
        // This is the thread that will be drawing to our surface.
        mDrawingThread = new DrawingThread();
        mDrawingThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        // Make sure the drawing thread is not running while we are paused.
        synchronized (mDrawingThread) {
            mDrawingThread.mRunning = false;
            mDrawingThread.notify();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Let the drawing thread resume running.
        synchronized (mDrawingThread) {
            mDrawingThread.mRunning = true;
            mDrawingThread.notify();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Make sure the drawing thread goes away.
        synchronized (mDrawingThread) {
            mDrawingThread.mQuit = true;
            mDrawingThread.notify();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // Tell the drawing thread that a surface is available.
        synchronized (mDrawingThread) {
            mDrawingThread.mSurface = holder;
            mDrawingThread.notify();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Don't need to do anything here; the drawing thread will pick up
        // new sizes from the canvas.
    }

    public void surfaceRedrawNeeded(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // We need to tell the drawing thread to stop, and block until
        // it has done so.
        synchronized (mDrawingThread) {
            mDrawingThread.mSurface = holder;
            mDrawingThread.notify();
            while (mDrawingThread.mActive) {
                try {
                    mDrawingThread.wait();
                } catch (InterruptedException e) {}
            }
        }
    }

    // Tracking of a single point that is moving on the screen.
    static final class MovingPoint {
        float x, y, dx, dy;
        
        void init(int width, int height, float minStep) {
            x = (float)((width-1)* Math.random());
            y = (float)((height-1)* Math.random());
            dx = (float)(Math.random()*minStep*2) + 1;
            dy = (float)(Math.random()*minStep*2) + 1;
        }
        
        float adjDelta(float cur, float minStep, float maxStep) {
            cur += (Math.random()*minStep) - (minStep/2);
            if (cur < 0 && cur > -minStep) cur = -minStep;
            if (cur >= 0 && cur < minStep) cur = minStep;
            if (cur > maxStep) cur = maxStep;
            if (cur < -maxStep) cur = -maxStep;
            return cur;
        }
        
        void step(int width, int height, float minStep, float maxStep) {
            x += dx;
            if (x <= 0 || x >= (width-1)) {
                if (x <= 0) x = 0;
                else if (x >= (width-1)) x = width-1;
                dx = adjDelta(-dx, minStep, maxStep);
            }
            y += dy;
            if (y <= 0 || y >= (height-1)) {
                if (y <= 0) y = 0;
                else if (y >= (height-1)) y = height-1;
                dy = adjDelta(-dy, minStep, maxStep);
            }
        }
    }
    
    /**
     * This is a thread that will be running a loop, drawing into the
     * window's surface.
     */
    class DrawingThread extends Thread {
        // These are protected by the Thread's lock.
        SurfaceHolder mSurface;
        boolean mRunning;
        boolean mActive;
        boolean mQuit;
        
        // Internal state.
        int mLineWidth;
        float mMinStep;
        float mMaxStep;
        
        boolean mInitialized;
        final MovingPoint mPoint1 = new MovingPoint();
        final MovingPoint mPoint2 = new MovingPoint();
        
        static final int NUM_OLD = 100;
        int mNumOld = 0;
        final float[] mOld = new float[NUM_OLD*4];
        final int[] mOldColor = new int[NUM_OLD];
        int mBrightLine = 0;
        
        // X is red, Y is blue.
        final MovingPoint mColor = new MovingPoint();
        
        final Paint mBackground = new Paint();
        final Paint mForeground = new Paint();
        final Paint mText = new Paint();
        final Paint mText1 = new Paint();
        
        int makeGreen(int index) {
            int dist = Math.abs(mBrightLine - index);
            if (dist > 10) return 0;
            return (255-(dist*(255/10))) << 8;
        }
        
        @Override
        public void run() {
            mLineWidth = (int)(getResources().getDisplayMetrics().density * 1.5);
            if (mLineWidth < 1) mLineWidth = 1;
            mMinStep = mLineWidth * 2;
            mMaxStep = mMinStep * 3;
            
            mBackground.setColor(0xff000000);
            mForeground.setColor(0xff00ffff);
            mText.setColor(Color.parseColor("#DAA520"));
            mText1.setColor(Color.parseColor("#DAA520"));
            //mText.setAntiAlias(false);
            mForeground.setAntiAlias(false);
            mForeground.setStrokeWidth(mLineWidth);
            
            while (true) {
                // Synchronize with activity: block until the activity is ready
                // and we have a surface; report whether we are active or inactive
                // at this point; exit thread when asked to quit.
                synchronized (this) {
                    while (mSurface == null || !mRunning) {
                        if (mActive) {
                            mActive = false;
                            notify();
                        }
                        if (mQuit) {
                            return;
                        }
                        try {
                            wait();
                        } catch (InterruptedException e){}
                    }
                    
                    if (!mActive) {
                        mActive = true;
                        notify();
                    }
                    
                    // Lock the canvas for drawing.
                    Canvas canvas = mSurface.lockCanvas();
                    if (canvas == null) {
                        continue;
                    }

                    // Update graphics.
                    if (!mInitialized) {
                        mInitialized = true;
                        mPoint1.init(canvas.getWidth(), canvas.getHeight(), mMinStep);
                        mPoint2.init(canvas.getWidth(), canvas.getHeight(), mMinStep);
                        mColor.init(127, 127, 1);

                    } else {
                        mPoint1.step(canvas.getWidth(), canvas.getHeight(),
                                mMinStep, mMaxStep);
                        mPoint2.step(canvas.getWidth(), canvas.getHeight(),
                                mMinStep, mMaxStep);
                        mColor.step(127, 127, 1, 3);
                    }
                    mBrightLine+=2;
                    if (mBrightLine > (NUM_OLD*2)) {
                        mBrightLine = -2;
                    }
                    
                    // Clear background.
                    canvas.drawColor(mBackground.getColor());

                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    String about = "City of God App V1.2";
                    String power = "Powered by City of God Media";
                    Rect r = new Rect();
                    canvas.getClipBounds(r);

                    int height = r.height();
                    int width = r.width();

                    int size = 0;
                    int size1  =0;
                   do{
                       mText.setTextSize(++size);
                   }while (mText.measureText(about)<width/2);

                    do{
                        mText1.setTextSize(++size1);
                    }while (mText1.measureText(power)<width/2);

                    int mText1Size = size/2;
                    mText.setTextAlign(Paint.Align.LEFT);
                    mText.getTextBounds(about, 0, about.length(), r);
                    mText.setTypeface(Typeface.DEFAULT);
                    mText.setStyle(Paint.Style.FILL);
                    mText.setColor(Color.parseColor("#DAA520"));
                    mText.setTextSize(size);
                    float X = width/2f-r.width()/2f-r.left;
                    float Y = height/2f+r.height()/2f-r.bottom;
                    canvas.drawText(about, X, Y, mText);


                    int mText1Size1 = size1/2;
                    mText1.setTextAlign(Paint.Align.LEFT);
                    mText1.getTextBounds(power, 0, power.length(), r);
                    mText1.setTypeface(Typeface.DEFAULT);
                    mText.setStyle(Paint.Style.FILL);
                    mText1.setColor(Color.parseColor("#DAA520"));
                    mText1.setTextSize(size1);
                    float X1 = width/2f-r.width()/2f-r.left;
                    float Y2 = height/2f+r.height()/2f-r.bottom;
                    canvas.drawText(power, X1, Y2+size, mText1);
                    
                    // Draw old lines.
                    for (int i=mNumOld-1; i>=0; i--) {
                        mForeground.setColor(mOldColor[i] | makeGreen(i));
                        mForeground.setAlpha(((NUM_OLD-i) * 255) / NUM_OLD);
                        int p = i*4;
                        canvas.drawLine(mOld[p], mOld[p+1], mOld[p+2], mOld[p+3], mForeground);
                    }
                    
                    // Draw new line.
                    int red = (int)mColor.x + 128;
                    if (red > 255) red = 255;
                    int blue = (int)mColor.y + 128;
                    if (blue > 255) blue = 255;
                    int color = 0xff000000 | (red<<16) | blue;
                    mForeground.setColor(color | makeGreen(-2));
                    canvas.drawLine(mPoint1.x, mPoint1.y, mPoint2.x, mPoint2.y, mForeground);
                    
                    // Add in the new line.
                    if (mNumOld > 1) {
                        System.arraycopy(mOld, 0, mOld, 4, (mNumOld - 1) * 4);
                        System.arraycopy(mOldColor, 0, mOldColor, 1, mNumOld - 1);
                    }
                    if (mNumOld < NUM_OLD) mNumOld++;
                    mOld[0] = mPoint1.x;
                    mOld[1] = mPoint1.y;
                    mOld[2] = mPoint2.x;
                    mOld[3] = mPoint2.y;
                    mOldColor[0] = color;
                    // All done!
                    mSurface.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public void onBackPressed() {
        startActivity(new Intent(this, Home.class));
        finish();
    }
}
