package com.visualfiber.apps.visualmaths.model_cv_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.visualfiber.apps.visualmaths.model_cv.FractionSet;
import com.visualfiber.apps.visualmaths.utils.txt.txtFS;

public class PercentViewBig extends FractionSetViewBig {

    public boolean showCircleL;
    public boolean showCircleR;



    private float radiusL;
    private float radiusR;

    public Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // flag to hide circles on first animation run
    private boolean circlesHidden;


    public PercentViewBig(Context context) {
        super(context);
    }


    public PercentViewBig(Context context, FractionSet fs, PointF pointF) {
        super(context, fs, pointF);

        fs.equalSign = "     =     ";
        txtFS.measure(pointF, fs);

        if (fs.showDividerL) showCircleL = true;
        if (fs.showDividerR) showCircleR = true;


        radiusL = getEnclosingRadius(fs.n1, fs.d1);
        radiusR = getEnclosingRadius(fs.n2, fs.d2);


        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //<editor-fold desc="......... Enclosing Circles ..............">
        if (!circlesHidden) {


            if (animFloat.isRunning() || animObject.isRunning()) {

                paint.setAlpha((int)(255 * (1 - animatedFraction)));

                if (showCircleL){

                    canvas.drawCircle(fs.p1.x, fs.fDividerY, radiusL, paint);

                }

                if (showCircleR){
                    canvas.drawCircle(fs.p3.x, fs.fDividerY, radiusR, paint);
                }


                // hide circles
                if (paint.getAlpha() < 10){

                    circlesHidden = true;
                }

                paint.setAlpha(255);


            } else { // initial


                if (showCircleL){

                    canvas.drawCircle(fs.p1.x, fs.fDividerY, radiusL, paint);

                }

                if (showCircleR){
                    canvas.drawCircle(fs.p3.x, fs.fDividerY, radiusR, paint);
                }


            }


        }
        //</editor-fold>


    }


    // returns required enclosingRadius for that fraction
    public float getEnclosingRadius(String N, String D){

        float height = fs.rectF.height();
        float width = txtFS.getFractionDividerLength(N, D);

        float diameter = txtFS.Gap + (float) Math.hypot(width, height);

        return diameter/2f;
    }

}
