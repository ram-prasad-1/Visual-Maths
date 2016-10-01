package com.visualfiber.apps.visualmaths.fraction;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model_cv.ExtraFraction;
import com.visualfiber.apps.visualmaths.model_cv.FractionSet;
import com.visualfiber.apps.visualmaths.model_cv_views.FractionSetViewBig;
import com.visualfiber.apps.visualmaths.utils.txt.txt;
import com.visualfiber.apps.visualmaths.utils.txt.txtFS;
import com.visualfiber.apps.visualmaths.views.base_views.BaseViewGroup;

public class FractionSetViewTest extends BaseViewGroup {


    FractionSet fs;
    FractionSetViewBig fsv;


    public FractionSetViewTest(Context context) {
        super(context);

        fs = new FractionSet("40", "140", "1", "50");

        p1.x = 30;
        p1.y = 300;

        p2.x = 700;
        p2.y = 900;


        txt.setTextSize(12);

        txtFS.measure(p1, fs);

        fsv = new FractionSetViewBig(context, fs, p1);
        addView(fsv);


        setBackgroundColor(ContextCompat.getColor(context, R.color.theme_green_background));


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
    protected int getSuggestedMinimumHeight() {
        return 750;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        fsv.layout(0, 0, r, b);
        fsv.setReferencePoint(p1);
    }


    @Override
    public void playAnimationForStep(int step) {

        Log.d("FractionSetViewTest", "playAnimationForStep: step " + step);
        switch (step) {

            case 1:
                ExtraFraction ef00 = new ExtraFraction(" + ", "123", "150", 1434351, false);
                fsv.addFraction(ef00);

                ExtraFraction ef_L = new ExtraFraction(" + ", "1345", "100", 13, false);
                fsv.addFraction(ef_L);
                break;


            case 2:
                fs.fillBottomPoint(p2, 40);
                fsv.createLcmFrsFromStart(3, p2, 1551, false);
                break;

            case 3:
                fs.fillBottomPoint(p2, 40);
                fsv.replaceWithLcmFraction(3, p2, 1551, false);
                break;

            case 4:
                fsv.showLcmFractionList = false;
                fsv.mixFromStart(3, false);
                break;

            case 5:
                fsv.drawSumOverMixedNo("5004", 2223, false);
                break;

            case 6:
                fsv.replaceMixedNumbersWithSum("5004", 2223, false);
                break;

            case 7:
                fsv.moveD1toRight();
                break;


            case 8:
                fsv.createSimplifyFraction(p2, true);
                break;

            case 9:
                fsv.replaceWithSimplifiedFr(p2, true);
                break;


            case 10:
                fsv.hideSimplifiedFraction();
                break;

            case 11:
                fsv.drawFinalAnswer();
                break;

        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        String ss1 = "+";
        String ss2 = "−";

        String ss = ss1 +  ss2;

        Log.d("TAG", "onDraw: " + txt.getTextLength(ss));
//        Log.d("TAG", "onDraw: " + txt.getTextBound(ss).width());

        txtFS.drawFraction(canvas, "-500", "2", new PointF(200, 300), 0);
    }

    @Override
    public int getTotalSteps() {
        return 11;
    }

    @Override
    public boolean showBothScrollViewAndCustomView() {
        return false;
    }

}
