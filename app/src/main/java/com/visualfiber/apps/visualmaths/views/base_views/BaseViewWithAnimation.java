package com.visualfiber.apps.visualmaths.views.base_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.View;

import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model.RequestCvPipe;




// OVERRIDE playAnimationInternal() NOT  playAnimationForStep() IN SUBCLASSES

//   3 PointF, 1 paint, 2 ValueAnimator Free
public  abstract class BaseViewWithAnimation extends View implements RequestCvPipe {


    public Paint paint;
    public PointF p1;
    public PointF p2;
    public PointF p3;


    // Data for View
    protected String[] cvData;


    // Animation
    protected int step = 0;
    public float a1 = 1;  // animated value bw 0 & 1
    public ValueAnimator anim1;
    public ValueAnimator anim2;
    public ValueAnimator.AnimatorUpdateListener updatelistener;



    protected boolean fbc;  // forward Button Clicked


    // actual step should remain private
    // as this variable is just a work around for backward button click
    private int actualStep;
    private RequestActivityPipe activityHandle;


    public BaseViewWithAnimation(Context context) {
        super(context);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        p1 = new PointF();
        p2 = new PointF();
        p3 = new PointF();

        setupAnimation();

    }



    @Override
    public void setCvData(String[] cvData) {
        this.cvData = cvData;
    }

    @Override
    public void setForwardButtonClicked(boolean forwardButtonClicked) {

        fbc = forwardButtonClicked;
    }

    @Override
    public void setActivityHandle(RequestActivityPipe activityHandle) {

        this.activityHandle = activityHandle;
    }



    protected String[] getCvData() {
        return cvData;
    }


    // default implementation, override again if necessary
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int h = resolveSizeAndState(getSuggestedMinimumHeight(), heightMeasureSpec, 0);

        setMeasuredDimension(widthMeasureSpec, h);


    }


    // use anim1.reverse for backward animation
    // todo set appropriate duration and interpolator for value animators
    private void setupAnimation() {

        anim1 = ValueAnimator.ofFloat(0, 1).setDuration(300);
        anim1.setInterpolator(new FastOutLinearInInterpolator());
        anim1.setStartDelay(2);


        anim2 = ValueAnimator.ofFloat(0, 1).setDuration(600);
        anim2.setInterpolator(new FastOutLinearInInterpolator());
        anim2.setStartDelay(2);




        //<editor-fold desc="...... update listener.......">
        updatelistener = new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                if (fbc) {
                    if (anim2.isRunning()) {
                        a1 = (Float) anim2.getAnimatedValue();
                    }

                    if (anim1.isRunning()) {
                        a1 = (Float) anim1.getAnimatedValue();
                    }
                } else {

                    // i.e. backward button clicked
                    if (anim2.isRunning()) {
                        a1 = 1 - (Float) anim2.getAnimatedValue();
                    }

                    if (anim1.isRunning()) {
                        a1 = 1 - (Float) anim1.getAnimatedValue();
                    }
                }

                invalidate();
            }
        };

        anim1.addUpdateListener(updatelistener);
        anim2.addUpdateListener(updatelistener);
        //</editor-fold>



    }







   // Public methods


    public void endAnimation() {
        step = 0;
        anim1.end();
        anim2.end();

        anim1.removeAllListeners();
        anim2.removeAllListeners();


    }



    // this should be same for all base classes
    // DO NOT OVERRIDE THIS IN SUBCLASSES
    // OVERRIDE playAnimationInternal() INSTEAD
    @Override
    public void playAnimationForStep(int stepNo) {

        // First update local step with actual step
        this.step = actualStep;

        // check which button is clicked - forward or backward
        fbc =  (stepNo > this.step);


        // only update local step if forwardButtonClicked
        if (fbc) {this.step = stepNo;}


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


}
