package com.kuchingitsolution.asus.eventmanagement.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class RenderRoundedCornerImage extends BitmapTransformation {

    public RenderRoundedCornerImage(Context context) {
        super(context);
    }

    @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

//        int size = Math.min(source.getWidth(), source.getHeight());
//        int x = source.getWidth();
//        int y = source.getHeight();
//        int x = (source.getWidth() - size) / 2;
//        int y = (source.getHeight() - size) / 2;

        // TODO this could be acquired from the pool too
//        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
//
//        Bitmap result = pool.get(x, y, Bitmap.Config.ARGB_8888);
//        if (result == null) {
//            result = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
//        }

//        Canvas canvas = new Canvas(result);
//        Paint paint = new Paint();
//        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
//        paint.setAntiAlias(true);
//        float r = size / 2f;
//        canvas.drawRoundRect(r,r,r,r,paint);
//        canvas.drawCircle(r, r, r, paint);
//        final Rect rect = new Rect(0, 0, x, y);
//        final RectF rectF = new RectF(rect);

//        canvas.drawRoundRect(rectF, 50, 50, paint);
//
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(result, rect, rect, paint);

        int width = source.getWidth();
        int height = source.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#000000"));

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, 50, 50, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);
        return result;
    }

    @Override public String getId() {
        return getClass().getName();
    }
}

