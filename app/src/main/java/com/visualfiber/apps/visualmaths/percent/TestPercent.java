package com.visualfiber.apps.visualmaths.percent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model_cv.FractionSet;
import com.visualfiber.apps.visualmaths.model_cv_views.PercentViewBig;
import com.visualfiber.apps.visualmaths.utils.txt.txt;
import com.visualfiber.apps.visualmaths.utils.txt.txtFS;
import com.visualfiber.apps.visualmaths.views.base_views.BaseViewGroupWithAnim;

public class TestPercent extends BaseViewGroupWithAnim implements View.OnClickListener {


    FractionSet fs;
    FractionSet fs2;
    PercentViewBig pcv;
    PercentViewBig pcv2;

    Handler handler = new Handler();

    PointF p2 = new PointF(20, 180);

    Button b1;

    int step;


    public TestPercent(Context context) {
        super(context);

        txt.setTextSize(40);

        fs = new FractionSet("65", "105", "x", "105");

//        fs.showEqualSign = false;
//        fs.showFractionRight(false);

        p1.x = 80;
        p1.y = 400;

        p2.x = 700;
        p2.y = 900;

        paint.setStyle(Paint.Style.STROKE);


        txtFS.measure(p1, fs);

        pcv = new PercentViewBig(context, fs, p1);

        addView(pcv);

        b1 = new Button(context);
        b1.setOnClickListener(this);
        b1.setText("Next");
        addView(b1);


        fs2 = new FractionSet("part", "whole", "percent", "100");
        fs2.equalSign = "     =     ";

        p3.x = fs.rectF.left - 60;
        p3.y = fs.rectF.bottom + 70;


        txtFS.measure(p3, fs2);

       /* pcv2 = new PercentView(context, fs2, p3);
        addView(pcv2);*/


        setBackgroundColor(ContextCompat.getColor(context, R.color.amber_600));
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        pcv.layout(0, 0, (int) p2.x, (int) p2.y);

//        pcv2.layout( (int)pcv.fs.rectF.left,(int) pcv.fs.rectF.bottom + 40, (int) p2.x, (int) p2.y);


        b1.layout(100, h - 200, 350, h - 100);


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        paint.setStyle(Paint.Style.STROKE);


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


    @Override
    public void onClick(View v) {
        step++;
        updateNextClick();


    }

    private void updateNextClick() {

        switch (step) {

            case 1:

                fs.showEqualSign = true;
                fs.showFractionRight(true);
                pcv.showCircleR = true;
                pcv.invalidate();
                break;


           /* case 2:
                pcv.moveD1toRight(step, new Handler()).start();
                break;*/

            case 2:
                pcv.changeSidesFsFractions();
                break;

           /* case 3:
                pcv.createSimplifyFraction(step, 1, p3).start();
                break;*/

            case 3:
                pcv.moveD1toRight();
                break;


            case 4:
//                pcv.createSimplifyFraction(step, false, p3);
                break;
            case 5:
//                pcv.replaceWithSimplifiedFr(step, false, p3);
                break;


        }


    }




    @Override
    protected void playAnimationInternal() {

    }

    @Override
    protected void setActivityHandleInternal(RequestActivityPipe activityHandle) {

    }

    @Override
    protected void setForwardButtonClickedInternal(boolean forwardButtonClicked) {

    }

    @Override
    public int getTotalSteps() {
        return 0;
    }

    @Override
    public boolean showBothScrollViewAndCustomView() {
        return false;
    }

}
