package org.schabi.cog.fragment;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.schabi.cog.bean.Profile;
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
public class ProfileDetailFragment extends Fragment {

    TextView pdtIdTxt;
    TextView pdtNameTxt;
    ImageView pdtImg;
    Activity activity;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    private ImageLoadingListener imageListener;

    Profile profile;

    public static final String ARG_ITEM_ID = "pdt_detail_fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.buddy)
                .showStubImage(R.drawable.buddy)
                .showImageForEmptyUri(R.drawable.buddy).cacheInMemory()
                .cacheOnDisc().build();

        imageListener = new ImageDisplayListener();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdt_detail, container,
                false);
        findViewById(view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            profile = bundle.getParcelable("singleProduct");
            setProductItem(profile);
        }

        return view;
    }

    private void findViewById(View view) {

        pdtNameTxt = (TextView) view.findViewById(R.id.pdt_name);
        pdtIdTxt = (TextView) view.findViewById(R.id.product_id_text);

        pdtImg = (ImageView) view.findViewById(R.id.product_detail_img);
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

    private void setProductItem(Profile resultProfile) {
        pdtNameTxt.setText("" + resultProfile.getName());
        pdtIdTxt.setText("Profile Id: " + resultProfile.getId());

        imageLoader.displayImage(resultProfile.getImageUrl(), pdtImg, options,
                imageListener);
    }
}
