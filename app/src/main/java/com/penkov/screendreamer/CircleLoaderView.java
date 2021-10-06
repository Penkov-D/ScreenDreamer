package com.penkov.screendreamer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.ImageView;

public class CircleLoaderView {

    private final ImageView mView;
    private final Context mContext;

    public CircleLoaderView (ImageView view, Context context)
    {
        mView = view;
        mContext = context;
    }

    public void drawCircle(float percent)
    {
        Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mContext.getResources().getColor(R.color.circle_stroke));
        canvas.drawCircle(500, 500, 500, paint);

        if (percent < 0.5f) // from 0%, 0xFF0000 (red) to 50%, 0xFFFF00 (yellow)
            paint.setColor(RGBAnalogToDigitalConvertor(1.0f, 1.0f, (percent * 2), 0.0f));
        else // from 50%, 0xFFFF00 (yellow) to 100%, 0x00FF00 (green)
            paint.setColor(RGBAnalogToDigitalConvertor(1.0f, ((1.0f - percent) * 2), 1.0f , 0.0f));
        canvas.drawArc(new RectF(0, 0, 1000, 1000),
                270.0f, 360.0f * percent, true, paint);

        paint.setColor(mContext.getResources().getColor(R.color.black));
        canvas.drawCircle(500, 500, 490, paint);

        mView.setImageBitmap(bitmap);
    }

    @SuppressWarnings("SameParameterValue") // for general use
    private static int RGBAnalogToDigitalConvertor (float a, float r, float g, float b)
    {
        return  (((int) (0xff * a) & 0xff) << 24) |
                (((int) (0xff * r) & 0xff) << 16) |
                (((int) (0xff * g) & 0xff) <<  8) |
                (((int) (0xff * b) & 0xff));
    }

}
