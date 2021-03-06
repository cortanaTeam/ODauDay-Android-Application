package com.odauday.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.odauday.R;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * Created by infamouSs on 3/6/18.
 */

public class ImageLoader {

    public static final int[] PLACE_HOLDER = new int[]{
              R.drawable.ic_gallery_placeholder1,
              R.drawable.ic_gallery_placeholder2,
              R.drawable.ic_gallery_placeholder3,
              R.drawable.ic_gallery_placeholder4,
              R.drawable.ic_gallery_placeholder5,
              R.drawable.ic_gallery_placeholder6,
              R.drawable.ic_gallery_placeholder7,
              R.drawable.ic_gallery_placeholder8,
              R.drawable.ic_gallery_placeholder9,
              R.drawable.ic_gallery_placeholder10
    };
    
    public static void load(Context context, ImageView imageView, Object image) {
        int placeHolder = randomPlaceHolder();
        Glide.with(context)
                  .load(image)
                  .apply(new RequestOptions()
                            .placeholder(placeHolder)
                            .error(placeHolder))
                  .into(imageView);
    }
    
    
    public static void load(ImageView imageView, Object image) {
        load(imageView.getContext(), imageView, image);
    }
    
    
    public static void loadWithoutOptions(ImageView imageView, Object image) {
        Glide.with(imageView.getContext())
            .load(image)
            .into(imageView);
    }
    public static void loadImageForUser(ImageView imageView, Object image){
        Glide.with(imageView.getContext())
            .load(image)
            .apply(new RequestOptions()
                .placeholder(R.drawable.user_default)
                .error(R.drawable.user_default))
            .into(imageView);
    }
    public static void loadImageForNotification(ImageView imageView,Object  image){
        Glide.with(imageView.getContext())
            .load(image)
            .apply(new RequestOptions()
                .placeholder(R.drawable.ic_notification)
                .error(R.drawable.ic_notification))
            .into(imageView);
    }
    public static void load(ImageView imageView, Object image, RequestOptions options) {
        Glide.with(imageView.getContext())
            .load(image)
            .apply(options)
            .into(imageView);
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            if(TextUtils.isEmpty(src)){
                return null;
            }
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    public static int randomPlaceHolder() {
        return PLACE_HOLDER[new Random().nextInt(PLACE_HOLDER.length)];
    }
}
