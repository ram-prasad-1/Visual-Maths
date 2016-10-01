package com.visualfiber.apps.visualmaths.circle.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;


/**
 *
 */

public abstract class Tangent extends Circle {


    protected boolean showTangent1 = true ;
    protected boolean showTangent2 = true;

    // extra length of tangent
    protected int extra = 0;

    // contact point on circle will change according to strokeWidth of tng
    protected float swTng = 6f;


    protected double dis_cC;
    protected double dis_cp;  // distance bw tng pt and Contact Point

    // dis_cp variable used for anim, d_along_cp = dis_cp + extra
    protected double d_along_cp;


    protected double alpha;
    protected double beta;

    // tangent angles with x axis
    protected double angle1;
    protected double angle2;


    // external point from where tangent is drawn
    protected PointF tng;

    // contact points of tangents
    protected PointF cp1;
    protected PointF cp2;


    protected Path tngPath;
    protected Paint tngPaint;


    public Tangent(Context context) {
       super(context);

        tngPath = new Path();

        tngPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tngPaint.setColor(Color.BLACK);
        tngPaint.setStyle(Paint.Style.STROKE);
        tngPaint.setStrokeJoin(Paint.Join.MITER);
        tngPaint.setStrokeWidth(swTng);
        tngPaint.setTextSize(30f);

        // reuse available points, just create new pointers
        cp1 = p1;
        cp2 = p2;
        tng = p3;
    }




    // after setting tng points
    protected void tngCalculations() {


        // distance bw centre to tng Point (this is line 1)
        dis_cC = Math.hypot((tng.x - cC.x), (tng.y - cC.y));


        // distance bw centre to tng Point
        dis_cp = Math.sqrt(dis_cC * dis_cC - rC * rC);


        d_along_cp = dis_cp + extra;

        //  Angle made bw line-1 and contact radius
        alpha = Math.acos(rC / dis_cC);

        // slope angle for line 1
        beta = Math.atan2((tng.y - cC.y), (tng.x - cC.x));



        //........................

        cp1.x = (float) ((rC + swTng/2) * Math.cos(beta + alpha) + cC.x);
        cp1.y = (float) ((rC + swTng/2) * Math.sin(beta + alpha) + cC.y);

        cp2.x = (float) ((rC + swTng/2) * Math.cos(beta - alpha) + cC.x);
        cp2.y = (float) ((rC + swTng/2) * Math.sin(beta - alpha) + cC.y);


        //........................
        angle1 = Math.atan2((cp1.y - tng.y), (cp1.x - tng.x));
        angle2 = Math.atan2((cp2.y - tng.y), (cp2.x - tng.x));




        //Define Path for tangent
        if (showTangent1) {

            tngPath.moveTo(tng.x, tng.y);
            tngPath.lineTo((float) (tng.x + d_along_cp * Math.cos(angle1)),

                    (float) (tng.y + d_along_cp * Math.sin(angle1)));
        }


        if (showTangent2) {
            // external point from where tangent is drawn
            tngPath.moveTo(tng.x, tng.y);

            tngPath.lineTo((float) (tng.x + d_along_cp * Math.cos(angle2)),

                    (float) (tng.y + d_along_cp * Math.sin(angle2)));
        }



    }


    // Dont forget to call tngCalculations() method after
    // setting points in onSizeChanged
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        // draw tangent Path
        canvas.drawPath(tngPath, tngPaint);


        // lines connecting cp and cC
        canvas.drawLine(cC.x,cC.y,cp1.x,cp1.y,tngPaint);
        canvas.drawLine(cC.x,cC.y,cp2.x,cp2.y,tngPaint);


    }

}




/*
    @Override
    protected int getSuggestedMinimumHeight() {
        return 560;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int h = resolveSizeAndState(getSuggestedMinimumHeight(), heightMeasureSpec, 0);

        setMeasuredDimension(widthMeasureSpec, h);


    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {




        space = 30;

        extra = 100;

        // actual height for drawing (except text for which whole height is available)
        int hh = h - 2 * space;
        int ww = w - 2 * space;




        //............

        cC.x = ww/3.5f;   cC.y = hh/2.0f;   rC = 200;

        tng.x = 700;   tng.y = hh - 20f;

        tngCalculations();

    }
*/



