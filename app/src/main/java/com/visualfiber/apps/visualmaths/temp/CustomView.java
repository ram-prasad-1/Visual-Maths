/*
package com.visualfiber.apps.visualmaths.temp;*/
/*
        
 *//*


import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model.RequestCvPipe;

public class CustomView extends View implements RequestCvPipe {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(widthMeasureSpec, 400);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawCircle(200, 200, 100, paint);



    }

    @Override
    public void setCvData(String[] cvData) {

    }

    @Override
    public void setActivityHandle(RequestActivityPipe activityHandle) {

    }

    @Override
    public void playAnimationForStep(int step) {

    }


    @Override
    public int getTotalSteps() {
        return 0;
    }

    @Override
    public void addlistener(AnimatorListenerAdapter adapter) {

    }

    @Override
    public boolean showBothScrollViewAndCustomView() {
        return false;
    }
}
*/
