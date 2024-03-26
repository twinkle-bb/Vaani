package com.twink.vaani.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ThumbnailUtils {
    public static int getPixelsFromDp(int dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static void setThumbnail(Bitmap thumbnail, ImageView imageView, Context context) {

            imageView.setImageBitmap(thumbnail);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ThumbnailUtils.getPixelsFromDp(160, context));
            int marginPixel = ThumbnailUtils.getPixelsFromDp(5, context);
            params.setMargins(marginPixel, marginPixel, marginPixel, marginPixel);
            imageView.setLayoutParams(params);


    }
}
