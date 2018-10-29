package com.example.johnnyma.testbench;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Used to transform Facebook profile picture into a circle with a white border.
 */
public class ProfilePicTransformation implements Transformation {

    private final int radius, margin;

    public ProfilePicTransformation(final int radius, final int margin) {
        this.radius = radius;
        this.margin = margin;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        // create round icon
        final Paint p_round = new Paint();
        p_round.setAntiAlias(true);
        p_round.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(output);
        c.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin),
                radius, radius, p_round);

        // create white border
        Paint p_border = new Paint();
        p_border.setColor(Color.WHITE);
        p_border.setStyle(Paint.Style.STROKE);
        p_border.setAntiAlias(true);
        p_border.setStrokeWidth(10);
        c.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin),
                radius, radius, p_border);


        if(source != output)
            source.recycle();

        return output;
    }

    @Override
    public String key() {
        return "formatted";
    }
}
