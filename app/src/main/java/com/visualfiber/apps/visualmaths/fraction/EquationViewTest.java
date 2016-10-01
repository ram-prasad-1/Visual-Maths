package com.visualfiber.apps.visualmaths.fraction;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model_cv.LS.FractionX;
import com.visualfiber.apps.visualmaths.model_cv.LS.MX_plus_C;
import com.visualfiber.apps.visualmaths.model_cv_views.EquationView;
import com.visualfiber.apps.visualmaths.utils.ss;
import com.visualfiber.apps.visualmaths.utils.txt.txt;
import com.visualfiber.apps.visualmaths.views.base_views.BaseViewGroup;

public class EquationViewTest extends BaseViewGroup {


    EquationView ev;


    public EquationViewTest(Context context) {
        super(context);


        p1.x = 30;
        p1.y = 300;

        p2.x = 700;
        p2.y = 900;


        txt.setTextSize(32);


        ev = new EquationView(context);
        addView(ev);


        setBackgroundColor(ContextCompat.getColor(context, R.color.theme_green_background));


    }


    @Override
    protected void setActivityHandleInternal(RequestActivityPipe activityHandle) {
        ev.activityHandle = activityHandle;

    }

    @Override
    protected void setForwardButtonClickedInternal(boolean forwardButtonClicked) {
        ev.fbc = forwardButtonClicked;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 750;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        ev.layout(0, 0, r, b);
        ev.setReferencePoint(p1);
    }


    @Override
    public void playAnimationForStep(int step) {

        switch (step) {

            case 1:

                FractionX f1 = new FractionX("141", 11, false);

                ev.addFraction(f1);
                break;

            case 2:

                MX_plus_C num = new MX_plus_C("-3", "-20");
                MX_plus_C denm = new MX_plus_C("-100", "18");
                FractionX f2 = new FractionX(ss.Plus_S, num, denm, 22, true);

                ev.addFraction(f2);
                break;

            case 3:
                FractionX f3 = new FractionX("-25", "-14", 33, false);

                ev.addFraction(f3);
                break;

            case 4:
                MX_plus_C num2 = new MX_plus_C("-3", "-20");
                MX_plus_C denm2 = new MX_plus_C("-100", "18");
                FractionX f4 = new FractionX(ss.Plus_S, num2, denm2, 44, false);

                ev.addFraction(f4);
                break;


        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

       /* String ss1 = "+";
        String ss2 = "−";

        String ss = ss1 +  ss2;

        Log.d("TAG", "onDraw: " + txt.getTextLength(ss));
//        Log.d("TAG", "onDraw: " + txt.getTextBound(ss).width());

        txtFS.drawFraction(canvas, "-500", "2", new PointF(200, 300), 0);*/
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
