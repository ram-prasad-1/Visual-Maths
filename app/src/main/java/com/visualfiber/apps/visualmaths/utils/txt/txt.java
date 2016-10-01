package com.visualfiber.apps.visualmaths.utils.txt;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.ColorInt;

public class txt {

    protected static boolean isPaintSetuped = false;

    public static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    protected static PointF pt = new PointF();

    protected static Rect bounds = new Rect();

    protected txt(){}

    protected static int previousColor = Color.BLACK;
    protected static float previousTxtSize = 36;


    //************************ Do not forget to set Color & txtSize before calling other methods ***********
    public static void setColor(@ColorInt int color) {

        previousColor = paint.getColor();
        paint.setColor(color);
        isPaintSetuped = true;
    }

    public static void setTextSize(float txtSize) {

        previousTxtSize = paint.getTextSize();

        paint.setTextSize(txtSize);
        isPaintSetuped = true;
    }

    public static void setAlpha(int alpha) {

        paint.setAlpha(alpha);
    }

    // to reset to just previous state
    public static void resetTextSize(){

        paint.setTextSize(previousTxtSize);
    }
    public static void resetColor(){

        paint.setColor(previousColor);
    }
    public static void resetAlpha() {

        paint.setAlpha(255);
    } // reset to 255


    // draw text
    // angle clockwise from x axis, angle = 0 is 3 o'clock
    public static void draw(Canvas c, String txt, PointF refPt, float distance, float angle, int align_L0C1R2) {

        paint.setStyle(Paint.Style.FILL);

        // text align
        switch (align_L0C1R2) {

            case 0:  // Align Left
                paint.setTextAlign(Paint.Align.LEFT);
                break;

            case 1:   // center
                paint.setTextAlign(Paint.Align.CENTER);
                break;

            case 2: // right
                paint.setTextAlign(Paint.Align.RIGHT);
                break;

            default:
                paint.setTextAlign(Paint.Align.CENTER);
                break;

        }

        if (!isPaintSetuped) {
            paint.setColor(Color.BLACK);
            paint.setTextSize(36);
        }

        getPointAtDistance(pt, refPt, distance, angle);

        c.drawText(txt, pt.x, pt.y, paint);


    }



    public static float getTextLength(String txt) {
        return paint.measureText(txt);
    }

    public static float getTextHeight(String txt) {
        paint.getTextBounds(txt, 0, txt.length(), bounds);
        return bounds.height();

    }

    public static Rect getTextBound(String txt) {
        paint.getTextBounds(txt, 0, txt.length(), bounds);
        return bounds;

    }


    // angle clockwise from x axis, angle = 0 is 3 o'clock
    protected static void getPointAtDistance(PointF point, PointF refPoint, float distance, double angle) {

        point.x = refPoint.x + distance * (float) Math.cos(Math.toRadians(angle));

        point.y = refPoint.y + distance * (float) Math.sin(Math.toRadians(angle));


    }



}
