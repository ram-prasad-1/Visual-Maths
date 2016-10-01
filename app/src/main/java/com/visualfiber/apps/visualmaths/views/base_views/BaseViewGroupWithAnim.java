package com.visualfiber.apps.visualmaths.views.base_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.ViewGroup;

import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model.RequestCvPipe;


//   3 PointF, 1 paint, 1 ValueAnimator Free

public abstract class BaseViewGroupWithAnim extends ViewGroup implements RequestCvPipe {


    public PointF p1;
    public PointF p2;
    public PointF p3;

    public Paint paint;

    // Data for View
    protected String[] cvData;

    protected RequestActivityPipe activityHandle;


    // for Animation
    protected int step = 0;
    public float a1 = 1;  // animated value bw 0 & 1
    public ValueAnimator anim;
    public ValueAnimator.AnimatorUpdateListener updatelistener;

    // actual step should remain private
    // as this variable is just a work around for backward button click
    private int actualStep;

    protected boolean fbc; // forward Button Clicked


    public BaseViewGroupWithAnim(Context context) {
        super(context);
        // very important for all custom Views extending ViewGroup
        this.setWillNotDraw(false);


        p1 = new PointF();
        p2 = new PointF();
        p3 = new PointF();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        setupAnimation();
    }


    // default implementation, override again if necessary
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int h = resolveSizeAndState(getSuggestedMinimumHeight(), heightMeasureSpec, 0);

        setMeasuredDimension(widthMeasureSpec, h);


    }

    @Override
    public void setCvData(String[] cvData) {
        this.cvData = cvData;
    }

    @Override
    public void setForwardButtonClicked(boolean forwardButtonClicked) {

        fbc = forwardButtonClicked;
        setForwardButtonClickedInternal(forwardButtonClicked);
    }

    @Override
    public void setActivityHandle(RequestActivityPipe activityHandle) {

        this.activityHandle = activityHandle;
        setActivityHandleInternal(activityHandle);
    }


    private void setupAnimation() {

        anim = ValueAnimator.ofFloat(0, 1).setDuration(300);
        anim.setInterpolator(new FastOutLinearInInterpolator());


        //<editor-fold desc="...... update listener.......">
        updatelistener = new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                if (fbc) {


                    a1 = (Float) anim.getAnimatedValue();

                } else {

                    // i.e. backward button clicked
                    a1 = 1 - (Float) anim.getAnimatedValue();

                }

                invalidate();
            }
        };

        anim.addUpdateListener(updatelistener);
        //</editor-fold>


    }


    // this should be same for all base classes
    // DO NOT OVERRIDE THIS IN SUBCLASSES
    // OVERRIDE playAnimationInternal() INSTEAD
    @Override
    public void playAnimationForStep(int stepNo) {

        // First update local step with actual step
        this.step = actualStep;

        // check which button is clicked - forward or backward
        fbc = (stepNo > this.step);


        // only update local step if forwardButtonClicked
        if (fbc) {
            this.step = stepNo;
        }


        playAnimationInternal();

        // record step in actual step
        actualStep = stepNo;
    }


    // ........... Drawing Helper Methods........

    protected void drawLine(Canvas c, PointF start, PointF end, Paint paint) {
        c.drawLine(start.x, start.y, end.x, end.y, paint);


    }


    //................. Animation Helper Methods..........

    protected void fadeIn() {
        paint.setAlpha((int) (a1 * 255));
    }

    protected void fadeOut() {
        paint.setAlpha((int) ((1 - a1) * 255));
    }

    // jo view apna color use nhi kar rahe h wo alphareset karen
    protected void resetAlpha() {
        paint.setAlpha(255);
    }


    // Abstract methods
    protected abstract void playAnimationInternal();

    // pass activity handle to any internal custom views with animations
    // otherwise do nothing.
    protected abstract void setActivityHandleInternal(RequestActivityPipe activityHandle);

    protected abstract void setForwardButtonClickedInternal(boolean forwardButtonClicked);


}
