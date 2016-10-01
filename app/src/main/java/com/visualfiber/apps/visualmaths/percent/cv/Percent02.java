package com.visualfiber.apps.visualmaths.percent.cv;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model_cv.FractionSet;
import com.visualfiber.apps.visualmaths.model_cv_views.FractionSetViewBig;
import com.visualfiber.apps.visualmaths.utils.txt.txt;
import com.visualfiber.apps.visualmaths.views.base_views.BaseViewGroup;

public class Percent02 extends BaseViewGroup {


    FractionSet fs;
    FractionSetViewBig fsv;

    public Percent02(Context context) {
        super(context);

        txt.setTextSize(40);


        fs = new FractionSet("40", "100", "400", "x");

        fsv = new FractionSetViewBig(context, fs, p1);
        addView(fsv);


        setBackgroundColor(ContextCompat.getColor(context, R.color.theme_green_background));
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 750;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {


        p1.x = 100;
        p1.y = 400;
        fsv.setReferencePoint(p1);

        fsv.layout(0, 0, r, b);


    }


    @Override
    public void playAnimationForStep(int step) {

        Log.d("Percent01", "playAnimationForStep: step " + step);



        switch (step) {

            case 1:
                fsv.invertFractionSet();
                break;

            default:
                break;
        }

        int change = step -1;

        switch (change) {

            case 1:
                fsv.changeSidesFsFractions();
                break;

            default:
                break;
        }

        int right = change -1;

        switch (right) {

            case 1:
                fsv.moveD1toRight();
                break;


            case 2:
                fs.fillBottomPoint(p2, 40);
                fsv.createSimplifyFraction(p2, true);
                break;


            case 3:
                fsv.replaceWithSimplifiedFr(p2, true);
                break;

            case 4:
                fsv.hideSimplifiedFraction();
                break;
            case 5:
                fsv.drawFinalAnswer();
                break;


        }

    }

    @Override
    protected void setActivityHandleInternal(RequestActivityPipe activityHandle) {

        fsv.activityHandle = activityHandle;
    }

    @Override
    protected void setForwardButtonClickedInternal(boolean forwardButtonClicked) {

        fsv.fbc = forwardButtonClicked;
    }


    @Override
    public int getTotalSteps() {
        return 10;
    }

    @Override
    public boolean showBothScrollViewAndCustomView() {
        return false;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


//        canvas.drawText("" + step, 200, 500, txt.paint);

    }


}
