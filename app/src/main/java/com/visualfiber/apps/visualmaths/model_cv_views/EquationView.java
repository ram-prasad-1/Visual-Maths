package com.visualfiber.apps.visualmaths.model_cv_views;

import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model_cv.LS.FractionX;
import com.visualfiber.apps.visualmaths.utils.ss;
import com.visualfiber.apps.visualmaths.utils.txt.txt;
import com.visualfiber.apps.visualmaths.utils.txt.txtFS;

import java.util.ArrayList;
import java.util.List;

public class EquationView extends View {   // minus sign -->  −


    //NOTE: Text align is LEFT for whole view

    public String equalSign = " = ";

    public float es_x_start; // start coordinate of equal sign
    public float es_dx; // dx in equal sign
    public float es_length; // equal sign length

    // gap to left of equal sign
    // used when multiplying etc.
    public float gapL = 0;
    public float gapR = 0; // gap just after equal sign
    public float gapStart = 0; // gap at start


    public List<FractionX> fractionList;

    public float txtCenterY;
    public float maxTxtHeight;

    // enclosing rect for Equation
    public RectF rectF = new RectF();


    public PointF temp1 = new PointF();


    // activity handle for enabling jumper buttons
    public RequestActivityPipe activityHandle;
    protected AnimatorListenerAdapter enableJumperButtonsAdapter;
    public boolean fbc = true;

    //..........................................

    protected int operation;

    //...............


    public EquationView(Context context) {
        super(context);
    }


    // MUST be first call on this view object (call in onLayout)
    public void setReferencePoint(PointF refPoint) {

        // left, top
        rectF.left = refPoint.x;
        rectF.top = refPoint.y;

        es_length = txt.getTextLength(equalSign);

        es_x_start = rectF.left + gapStart + gapL;


        // right, bottom
        rectF.right = es_x_start + es_length + gapR;

        maxTxtHeight = txt.getTextHeight("89");

        // rect height distribution= | N | Gap | Gap | D |
        rectF.bottom = rectF.top + 2 * (txtFS.Gap + maxTxtHeight);

        // txtCenterY
        // txtCenterY > fDividerY (in mobile)
        txtCenterY = rectF.centerY() + maxTxtHeight / 2f - 2;


        //NOTE: align LEFT
        txt.paint.setTextAlign(Paint.Align.LEFT);
        txt.paint.setStrokeWidth(txtFS.dividerSW);

    }


    // adding fractions
    // NOTE: absolute values are given only when added
    // for all other operations use x and dx of corresponding numbers
    public void addFraction(FractionX f) {

        // no fraction is broken when initially added

        reset_dx_values();

        operation = -9794;


        boolean isAlreadyAdded = false;
        int addedAtIndex = 0;
        //<editor-fold desc=".......... check if already added ............">
        int i = 0;
        if (fractionList != null) {

            for (FractionX fx : fractionList) {

                if (fx.uniqueID == f.uniqueID) {
                    isAlreadyAdded = true;
                    addedAtIndex = i;

                } else {

                    if (fx.isSideRight == f.isSideRight) { // id not same but side same
                        f.isFirstOnThisSide = false;

                    }

                }

                i++;
            }

        }
        //</editor-fold>

        if (fbc) { // fbc


            //<editor-fold desc=".......... update fraction x values ............">
            float xi = f.isSideRight ? rectF.right : es_x_start;
            float xi_initial = xi;

            // NOTE: for first fraction with d != 1; only care for fr sign if it's -ve
            if (f.isFirstOnThisSide && !isSignPlus(f.sign) && !f.isDenominator_1) {
                xi += gtl(f.sign);

            }

            // NOTE: for not first fraction with d != 1
            if (!f.isFirstOnThisSide && !f.isDenominator_1) {
                xi += gtl(f.sign);

            }


            // update fraction start
            f.xStart = xi;

            // when d != 1, fraction to dikhani hi padegi
            if (!f.isDenominator_1) {
                xi += txtFS.Gap;
            }

            float numLength = gtl(f.num.toString());
            float denmLength = gtl(f.denm.toString());

            Log.d("EquationView", "addFraction: numLength --> " + numLength + "  denmLength --> "
                    + denmLength + "  xi --> " + xi + "  xi_initial --> " + xi_initial);


            float shorterNumberStartX = Math.abs(numLength / 2f - denmLength / 2f);

            // add space around leading sign or not if d_1_and_not_FF
            boolean d_1_and_not_FF = f.isDenominator_1 && !f.isFirstOnThisSide;
            float end1;
            float end2;

            //<editor-fold desc=".......... set xm & xc values for numerator and denominator ............">

            if (numLength >= denmLength) { // numLength is longer

                // numerator x values
                end1 = f.num.set_x_Values(xi, d_1_and_not_FF);


                xi = xi + shorterNumberStartX;

                // denominator x values
                end2 = f.denm.set_x_Values(xi, false);

            } else { // denmLength is longer

                // denominator x values
                end1 = f.denm.set_x_Values(xi, false);


                xi = xi + shorterNumberStartX;

                // numerator x values
                end2 = f.num.set_x_Values(xi, false);

            }
            //</editor-fold>

            xi = Math.max(end1, end2);


            if (!f.isDenominator_1) {
                xi += txtFS.Gap;
            }


            // save final xi
            float increase = (xi - xi_initial);
            rectF.right += increase;  // rf right will increase in both cases

            if (!f.isSideRight) { // left

                es_x_start = xi;

                if (fractionList != null) {
                    for (FractionX fr : fractionList) {

                        if (fr.isSideRight) {
                            // num
                            fr.num.xm += increase;
                            fr.num.xc += increase;

                            // denum
                            fr.denm.xm += increase;
                            fr.denm.xc += increase;

                            // fraction x start
                            fr.xStart += increase;
                        }

                    }
                }
            }


            //</editor-fold>


            if (!isAlreadyAdded) {

                if (fractionList == null) {
                    fractionList = new ArrayList<>(4);
                }

                // add to fractionList
                fractionList.add(f);

            }else{

                fractionList.get(addedAtIndex).visibility_GONE = false; // show it

            }


        } else {  // bbc

            fractionList.get(addedAtIndex).visibility_GONE = true; // hide it

            //<editor-fold desc=".......... update xi for rest ............">
            float xi = f.isSideRight ? rectF.right : es_x_start;
            float xi_initial = xi;

            // NOTE: for first fraction with d != 1; only care for fr sign if it's -ve
            if (f.isFirstOnThisSide && !isSignPlus(f.sign) && !f.isDenominator_1) {
                xi += gtl(f.sign);

            }

            // NOTE: for not first fraction with d != 1
            if (!f.isFirstOnThisSide && !f.isDenominator_1) {
                xi += gtl(f.sign);

            }


            // when d != 1, fraction to dikhani hi padegi
            if (!f.isDenominator_1) {
                xi += txtFS.Gap;
            }

            float numLength = gtl(f.num.toString());
            float denmLength = gtl(f.denm.toString());


            float shorterNumberStartX = Math.abs(numLength / 2f - denmLength / 2f);

            // add space around leading sign or not if d_1_and_not_FF
            boolean d_1_and_not_FF = f.isDenominator_1 && !f.isFirstOnThisSide;
            float end1;
            float end2;

            //<editor-fold desc=".......... set xm & xc values for numerator and denominator ............">

            if (numLength >= denmLength) { // numLength is longer

                // numerator x values
                end1 = f.num.set_x_Values(xi, d_1_and_not_FF);


                xi = xi + shorterNumberStartX;

                // denominator x values
                end2 = f.denm.set_x_Values(xi, false);

            } else { // denmLength is longer

                // denominator x values
                end1 = f.denm.set_x_Values(xi, false);


                xi = xi + shorterNumberStartX;

                // numerator x values
                end2 = f.num.set_x_Values(xi, false);

            }
            //</editor-fold>

            xi = Math.max(end1, end2);


            if (!f.isDenominator_1) {
                xi += txtFS.Gap;
            }


            // save final xi
            float increase = (xi - xi_initial);
            rectF.right -= increase; // rf right will increase in both sides

            if (!f.isSideRight) { // left

                es_x_start -= increase;
                if (fractionList != null) {
                    for (FractionX fr : fractionList) {

                        if (fr.isSideRight) {
                            // num
                            fr.num.xm -= increase;
                            fr.num.xc -= increase;

                            // denum
                            fr.denm.xm -= increase;
                            fr.denm.xc -= increase;

                            // fraction x start
                            fr.xStart -= increase;
                        }

                    }
                }
            }

            //</editor-fold>
        }

        invalidate();
    }

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rectF, paint);

        temp1.x = es_x_start + es_dx;
        temp1.y = txtCenterY;
        txt.draw(canvas, equalSign, temp1, 0, 0, 0);

        if (fractionList != null) {

            for (FractionX f : fractionList) {

                if (!f.visibility_GONE) {
                    drawFraction(canvas, f);
                }
            }

        }

    }


//......... private methods ................

    private void drawFraction(Canvas c, FractionX f) {

        // left align
        txt.paint.setTextAlign(Paint.Align.LEFT);


        if (!f.isDenominator_1) { // denominator != 1

            //<editor-fold desc=".......... numerator ............">
            if (!f.num.m.equals("0")) {

                // - ve sign
                if (!f.num.ipm) {
                    c.drawText(ss.Minus, f.num.xm + f.num.dxm - gtl(ss.Minus), rectF.top + maxTxtHeight, txt.paint);
                }

                // m
                c.drawText(f.num.m, f.num.xm + f.num.dxm, rectF.top + maxTxtHeight, txt.paint);

                // x
                c.drawText(f.num.x, f.num.xm + f.num.dxm + f.num.dxx, rectF.top + maxTxtHeight, txt.paint);
            }


            if (!f.num.c.equals("0")) {

                // c sign
                String minusC = f.num.m.equals("0") ? ss.Minus : ss.Minus_S; // m == 0 ho to no space
                String sign = f.num.ipc ? ss.Plus_S : minusC;

                if (!(f.num.m.equals("0") && f.num.ipc)) { // do not draw sign if m = 0 & c +ve
                    c.drawText(sign, f.num.xc + f.num.dxc - gtl(sign), rectF.top + maxTxtHeight, txt.paint);
                }

                // c
                c.drawText(f.num.c, f.num.xc + f.num.dxc, rectF.top + maxTxtHeight, txt.paint);


            }
            //</editor-fold>


            //<editor-fold desc=".......... denominator ............">
            if (!f.denm.m.equals("0")) {

                // - ve sign
                if (!f.denm.ipm) {
                    c.drawText(ss.Minus, f.denm.xm + f.denm.dxm - gtl(ss.Minus), rectF.bottom, txt.paint);
                }

                // m
                c.drawText(f.denm.m, f.denm.xm + f.denm.dxm, rectF.bottom, txt.paint);

                // x
                c.drawText(f.denm.x, f.denm.xm + f.denm.dxm + f.denm.dxx, rectF.bottom, txt.paint);
            }

            if (!f.denm.c.equals("0")) {

                // c sign
                String minusC = f.denm.m.equals("0") ? ss.Minus : ss.Minus_S; // m == 0 ho to no space
                String sign = f.denm.ipc ? ss.Plus_S : minusC;

                if (!(f.denm.m.equals("0") && f.denm.ipc)) { // do not draw sign if m = 0 & c +ve
                    c.drawText(sign, f.denm.xc + f.denm.dxc - gtl(sign), rectF.bottom, txt.paint);
                }

                // c
                c.drawText(f.denm.c, f.denm.xc + f.denm.dxc, rectF.bottom, txt.paint);
            }
            //</editor-fold>


            //<editor-fold desc=".......... divider line ............">

            float xStart = f.xStart;
            float xEnd = Math.max(f.num.xc + f.num.dxc + gtl(f.num.c),
                    f.denm.xc + f.denm.dxc + gtl(f.denm.c));

            c.drawLine(xStart, rectF.centerY(), xEnd + txtFS.Gap, rectF.centerY(), txt.paint);
            //</editor-fold>

            //<editor-fold desc=".......... f.sign ............">
            // sign considerations: only draw if
            // 1. if FF, -ve fr sign & d != 1   OR     2. if !FF & d != 1
            if (f.isFirstOnThisSide && !isSignPlus(f.sign) && !f.isDenominator_1
                    || (!f.isFirstOnThisSide && !f.isDenominator_1)) {

                float signStartX = f.xStart - gtl(f.sign);
                c.drawText(f.sign, signStartX, txtCenterY, txt.paint);

            }
            //</editor-fold>


        } else { // denominator = 1

            // NOTE: no use of fraction sign if d = 1

            //<editor-fold desc=".......... if d == 1, only draw numerator ............">
            if (!f.num.m.equals("0")) {

                // m sign
                String minusM = f.isFirstOnThisSide ? ss.Minus : ss.Minus_S; // minus with space if not FF & d == 1
                String sign = f.num.ipm ? ss.Plus_S : minusM; //

                if (!(f.isFirstOnThisSide && f.num.ipm)) {  // do not draw sign if it is first fraction with m +ve
                    c.drawText(sign, f.num.xm + f.num.dxm - gtl(sign), txtCenterY, txt.paint);
                }


                // m
                c.drawText(f.num.m, f.num.xm + f.num.dxm, txtCenterY, txt.paint);

                // x
                c.drawText(f.num.x, f.num.xm + f.num.dxm + f.num.dxx, txtCenterY, txt.paint);

            }

            if (!f.num.c.equals("0")) {


                String minusSignC = "$$";

                // CASE A: m = 0, c != 0 & -ve
                if (f.num.m.equals("0") && !f.num.ipc) {
                    minusSignC = f.isFirstOnThisSide ? ss.Minus : ss.Minus_S; // minus with space if not FF & d == 1
                }

                // CASE B: m != 0, c != 0
                if (!f.num.m.equals("0")) {
                    minusSignC = ss.Minus_S;
                }

                // c sign
                String sign = f.num.ipc ? ss.Plus_S : minusSignC;

                if (!(f.isFirstOnThisSide && f.num.ipc && f.num.m.equals("0"))) { // do not draw sign if it is first fraction with m =0 & c +ve
                    c.drawText(sign, f.num.xc + f.num.dxc - gtl(sign), txtCenterY, txt.paint);
                }

                // c
                c.drawText(f.num.c, f.num.xc + f.num.dxc, txtCenterY, txt.paint);

            }
            //</editor-fold>

        }

    }

    // reset dx values for whole fractionList
    private void reset_dx_values() {

        if (fractionList != null) {

            for (FractionX fx : fractionList) {

                // n
                fx.num.dxm = 0;
                fx.num.dxc = 0;

                // d
                fx.denm.dxm = 0;
                fx.denm.dxc = 0;
            }


        }
    }

    // is sign plus
    private boolean isSignPlus(String sign) {

        boolean isPositive = false;

        for (int i = 0; i < sign.length(); i++) {

            char c = sign.charAt(i);

            if (c == '+') {
                isPositive = true;
                break;
            }
        }

        return isPositive;
    }

    // get Text Length
    private float gtl(String str) {
        return txt.getTextLength(str);
    }
}
