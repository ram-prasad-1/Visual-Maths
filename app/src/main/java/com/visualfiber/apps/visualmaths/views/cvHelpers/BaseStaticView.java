package com.visualfiber.apps.visualmaths.views.cvHelpers;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

/**
 *     Get 1 Point, 1 Path and 1 Paint for Free
 */
public class BaseStaticView {

    protected BaseStaticView(){}

    public static float strokeWidth = 5f;
    public static int alpha = 255;
    public static String paintColor = "black";

    protected static PointF p = new PointF();

    public static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);



    protected static Path path = new Path();


    // STYLE- Stroke       JOIN- Miter
    protected static void setupPaint(){

        paint.setColor(Color.parseColor(paintColor));
        paint.setAlpha(alpha);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.MITER);
        paint.setStrokeWidth(strokeWidth);
        paint.setStrokeCap(Paint.Cap.BUTT);

    }



    // Direction of pt2 from pt1
    protected static double getDirection(PointF pt2, PointF pt1) {
        return Math.atan2((pt2.y - pt1.y), (pt2.x - pt1.x));
    }



}
