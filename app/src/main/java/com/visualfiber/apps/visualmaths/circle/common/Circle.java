package com.visualfiber.apps.visualmaths.circle.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.visualfiber.apps.visualmaths.views.base_views.BaseViewWithAnimation;


public abstract class Circle extends BaseViewWithAnimation {


    protected boolean showDotAtCentre = false;

    protected int colorCircle = Color.BLUE;
    protected int colorDot = Color.BLACK;

    protected float rC = 200.0f;
    protected float radiusDot = 10.0f;



    protected PointF cC = new PointF(); //centerCircle


    protected Paint paintCircle;
    protected Paint paintDot;




    public Circle(Context context) {
      super(context);

        paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCircle.setColor(colorCircle);
        paintCircle.setStyle(Paint.Style.FILL);



        paintDot = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDot.setColor(colorDot);
        paintDot.setStyle(Paint.Style.FILL);

    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        // Draw the circle
        canvas.drawCircle(cC.x, cC.y, rC, paintCircle);

        // DrawDot
        if (showDotAtCentre){
        canvas.drawCircle(cC.x, cC.y,radiusDot, paintDot);}


    }


    // angle clockwise from x axis, angle = 0 is 3 o'clock
    protected void getPointCircle(PointF point,double angle){

        point.x = cC.x + rC *(float)Math.cos(Math.toRadians(angle));

        point.y = cC.y + rC *(float)Math.sin(Math.toRadians(angle));


    }








}