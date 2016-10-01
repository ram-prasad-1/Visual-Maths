package com.visualfiber.apps.visualmaths.temp;
/*
        
 */


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.views.base_views.BaseViewWithAnimation;

public class CustomView3 extends BaseViewWithAnimation {



    Context context;


    public CustomView3(Context context) {
        super(context);

        this.context = context;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(context, R.color.theme_purple_accent));
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(widthMeasureSpec, 450);

    }

    @Override
    protected void playAnimationInternal() {

        if (step == 1){

            anim1.start();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(ContextCompat.getColor(context, R.color.theme_purple_accent));

        if (step == 0) {
            paint.setAlpha(255);
        }

        if (step == 1) {
            paint.setAlpha((int) ((1- a1) * 255));

        }


        canvas.drawRect(50, 50, 600, 350, paint);









/*  paint.setColor(Color.BLACK);

        paint.setTextSize(50);

        paint.setTextAlign(Paint.Align.LEFT);

        if (cvData != null) {
            canvas.drawText(cvData[0]+ "  " + cvData[1], 70, 225, paint);
        }*/






    }


    @Override
    public int getTotalSteps() {
        return 1;
    }

    @Override
    public boolean showBothScrollViewAndCustomView() {
        return false;
    }


}
