package org.schabi.cog.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.schabi.cog.bean.Profile;
import org.schabi.cog.fragment.HomeFragment;
import org.schabi.cog.fragment.ProfileDetailFragment;
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

public class ImageSlideAdapter extends PagerAdapter {
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    private ImageLoadingListener imageListener;
    FragmentActivity activity;
    List<Profile> profiles;
    HomeFragment homeFragment;

    public ImageSlideAdapter(FragmentActivity activity, List<Profile> profiles,
                             HomeFragment homeFragment) {
        this.activity = activity;
        this.homeFragment = homeFragment;
        this.profiles = profiles;
        options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.ic_error)
                .showStubImage(R.drawable.buddy)
                .showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory()
                .cacheOnDisc().build();

        imageListener = new ImageDisplayListener();
    }

    @Override
    public int getCount() {
        return profiles.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.vp_image, container, false);

        ImageView mImageView = (ImageView) view
                .findViewById(R.id.image_display);
        mImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                Fragment fragment = null;
                Log.d("position adapter", "" + position);
                Profile profile = (Profile) profiles.get(position);
                arguments.putParcelable("singleProduct", profile);

                // Start a new fragment
                fragment = new ProfileDetailFragment();
                fragment.setArguments(arguments);

                FragmentTransaction transaction = activity
                        .getSupportFragmentManager().beginTransaction();
                //transaction.replace(R.id.content_frame, fragment,
                       // ProfileDetailFragment.ARG_ITEM_ID);
                transaction.addToBackStack(ProfileDetailFragment.ARG_ITEM_ID);
                transaction.commit();
            }
        });
        imageLoader.displayImage(
                ((Profile) profiles.get(position)).getImageUrl(), mImageView,
                options, imageListener);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private static class ImageDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}