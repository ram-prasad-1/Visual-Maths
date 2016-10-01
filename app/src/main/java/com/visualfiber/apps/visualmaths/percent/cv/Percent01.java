package com.visualfiber.apps.visualmaths.percent.cv;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model_cv.FractionSet;
import com.visualfiber.apps.visualmaths.model_cv_views.FractionSetViewBig;
import com.visualfiber.apps.visualmaths.utils.txt.txt;
import com.visualfiber.apps.visualmaths.views.base_views.BaseViewGroup;

public class Percent01 extends BaseViewGroup {


    FractionSet fs;
    FractionSetViewBig fsv;

    TextView tv1;
    TextView tv2;
    TextView tv3;

    public Percent01(Context context) {
        super(context);

        txt.setTextSize(40);


        fs = new FractionSet("x", "100", "108", "675");

        fsv = new FractionSetViewBig(context, fs, p1);
        fsv.setVisibility(View.INVISIBLE);
        LayoutParams lp0 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(fsv, lp0);

        tv1 = getEmptyTextView();
        tv2 = getEmptyTextView();
        tv3 = getEmptyTextView();

        setBackgroundColor(ContextCompat.getColor(context, R.color.theme_green_background));
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 750;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        measureChildWithMargins(tv1, widthMeasureSpec, 0, heightMeasureSpec, 0);
        measureChildWithMargins(tv2, widthMeasureSpec, 0, heightMeasureSpec, 0);
        measureChildWithMargins(tv3, widthMeasureSpec, 0, heightMeasureSpec, 0);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int left = getPaddingLeft();
        int top = getPaddingTop();

        // tv1
        layoutView(tv1, left, top, getW_M(tv1), getH_M(tv1));
        top += getH(tv1);

        // tv2
        layoutView(tv2, left, top, getW_M(tv2), getH_M(tv2));
        top += getH(tv2);

        // fsv
        float txtSize = (tv1 == null) ? 32 : tv1.getTextSize();
        txt.setTextSize(txtSize);

        p1.x = tv2.getPaddingLeft() + 40;
        p1.y = top + tv2.getPaddingTop() + 40;
        fsv.setReferencePoint(p1);

        fsv.layout(0, 0, r, b);
        p2.x = fs.rectF.left;
        p2.y = fs.rectF.bottom + 80;

        // tv3
        float tv3Left = fs.p1.x - tv3.getPaddingLeft();
        top = (int) fs.txtCenterY + 24;
        layoutView(tv3, (int) tv3Left, top, getW_M(tv3), getH_M(tv3));


    }


    @Override
    public void playAnimationForStep(int step) {

        Log.d("Percent01", "playAnimationForStep: step " + step);

        switch (step) {

            case 1:
                tv1 = getAndShowTV(cvData[0]);
                break;

            case 2:
                tv2 = getAndShowTV(cvData[1]);
                break;

           /* case 3:
                fsv.createSimplifyFraction(step, 1, p2).start();
                break;*/

            case 3:
                fsv.setVisibility(View.VISIBLE);
                break;

            case 4:
//                fsv.moveD1toRight(fbc);
                fsv.moveD1toRight();
            /*    ExtraFraction ef00 = new ExtraFraction(" + ", "66666", "50");
                ExtraFraction ef000 = new ExtraFraction(" + ", "444", "30");

                fsv.addFraction(1, ef00);
                fsv.addFraction(1, ef000);*/

                break;


            case 5:
                fsv.createSimplifyFraction(p2, true);
                break;


            case 6:
                fsv.replaceWithSimplifiedFr(p2, true);
                break;

            case 7:
                fsv.hideSimplifiedFraction();
                break;

            /*case 7:
                tv3 = getAndShowTV(cvData[2]);
                break;*/

            case 8:
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


        canvas.drawText("" + step, 200, 500, txt.paint);

    }


}
