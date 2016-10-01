package com.visualfiber.apps.visualmaths.utils.txt;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.visualfiber.apps.visualmaths.model_cv.FractionSet;


// class for drawing FractionSet and single fraction
public class txtFS extends txt {
    // Note it use paint, txtColor etc from txt Class so watch that out


    public static final int Gap = 10;  // gap for fraction

    public static int dividerSW = Gap / 3;


    protected txtFS() {
    }  // prevent default initiation


    // draw single fraction
    // returns divider length
    public static float drawFraction(Canvas c, String N, String D, PointF refPt, int refPtAt_L0C1R2) {


        float dividerLength = getFractionDividerLength(N, D);

        // copy values from ref pt to pt of this class
        pt.y = refPt.y;
        switch (refPtAt_L0C1R2) { // for x values

            case 0:  // ref Pt is Left pt of fraction
                pt.x = refPt.x;
                break;

            case 1:   // center
                pt.x = (refPt.x - dividerLength / 2.0f);
                break;

            case 2: // right
                pt.x = (refPt.x - dividerLength);
                break;

            default:
                pt.x = refPt.x;
                break;

        }


        paint.setTextAlign(Paint.Align.CENTER);
        c.drawText(N, pt.x + dividerLength / 2.0f, pt.y - Gap, paint);

        c.drawText(D, pt.x + dividerLength / 2.0f, pt.y + Gap + getTextHeight(D), paint);

        // divider line
        paint.setStrokeWidth(dividerSW);
        c.drawLine(pt.x, pt.y, pt.x + dividerLength, pt.y, paint);


        return dividerLength;

    }

    // call measure at least once before calling this method
    public static void draw(Canvas c, FractionSet fs) {

        // align center
        paint.setTextAlign(Paint.Align.CENTER);

        //<editor-fold desc="......Move Number To CenterY Code..........">
        // if a number should be drawn at center y instead
        if (fs.moveToCenterNumberIndex > 0) {
            float centerY = fs.txtCenterY;

            switch (fs.moveToCenterNumberIndex) {

                case 1: //n1
                    c.drawText(fs.n1, fs.p1.x, fs.p1.y + fs.animatedFraction * (centerY - fs.p1.y), paint);
                    fs.showN1 = false;
                    break;

                case 2:  // d1
                    c.drawText(fs.d1, fs.p2.x, fs.p2.y + fs.animatedFraction * (centerY - fs.p2.y), paint);
                    fs.showD1 = false;
                    break;

                case 3: // n2
                    c.drawText(fs.n2, fs.p3.x, fs.p3.y + fs.animatedFraction * (centerY - fs.p3.y), paint);
                    fs.showN2 = false;
                    break;

                case 4: // d2
                    c.drawText(fs.d2, fs.p4.x, fs.p4.y + fs.animatedFraction * (centerY - fs.p4.y), paint);
                    fs.showD2 = false;
                    break;

                default:
                    break;

            }

        }
        //</editor-fold>

        //<editor-fold desc=".... Invert Fraction Code .......">
        if (fs.invertFraction) {

            c.drawText(fs.n1, fs.p1.x, fs.p1.y + fs.animatedFraction * (fs.p2.y - fs.p1.y), paint);
            c.drawText(fs.n2, fs.p3.x, fs.p3.y + fs.animatedFraction * (fs.p4.y - fs.p3.y), paint);

            c.drawText(fs.d1, fs.p2.x, fs.p2.y + fs.animatedFraction * (fs.p1.y - fs.p2.y), paint);
            c.drawText(fs.d2, fs.p4.x, fs.p4.y + fs.animatedFraction * (fs.p3.y - fs.p4.y), paint);


            // discard default drawings
            fs.showN1 = false;
            fs.showN2 = false;
            fs.showD1 = false;
            fs.showD2 = false;

        }
        //</editor-fold>


        // fraction on Left
        if (fs.showN1) {
            c.drawText(fs.n1, fs.p1.x, fs.p1.y, paint);
        }

        if (fs.showD1) {
            c.drawText(fs.d1, fs.p2.x, fs.p2.y, paint);
        }

        // fraction on right
        if (fs.showN2) {
            c.drawText(fs.n2, fs.p3.x, fs.p3.y, paint);
        }
        if (fs.showD2) {
            c.drawText(fs.d2, fs.p4.x, fs.p4.y, paint);
        }


        // fraction divider lines
        paint.setStrokeWidth(dividerSW);
        if (fs.showDividerL) {
            c.drawLine(fs.rectF.left, fs.fDividerY, fs.rectF.left + fs.fDividerL, fs.fDividerY, paint); // left
        }

        if (fs.showDividerR) {
            c.drawLine(fs.rectF.right - fs.fDividerR - fs.gapR, fs.fDividerY, fs.rectF.right - fs.gapR, fs.fDividerY, paint); // right
        }

        // draw equal sign
        if (fs.showEqualSign) {
            c.drawText(fs.equalSign, fs.rectF.left + fs.fDividerL + fs.gapL + fs.equalSignLength / 2f, fs.txtCenterY, paint);
        }

        // Temp String  NOTE: DO NOT FORGET TO ADJUST FOR MULTISIGN LENGTH
        // AS TXT ALIGNMENT IS CENTER
        if (fs.showTemp1) {

            c.drawText(fs.temp1, fs.tempHandle.x + (fs.animatedFraction - 1) * getTextLength(FractionSet.multiSign) / 2f, fs.tempHandle.y, paint);
        }

    }

    // refP --> left, top point of fraction set
    public static void measure(PointF refP, FractionSet fs) {


        fs.equalSignLength = getTextLength(fs.equalSign);

        fs.fDividerL = getFractionDividerLength(fs.n1, fs.d1); // left fraction divider length
        fs.fDividerR = getFractionDividerLength(fs.n2, fs.d2);

        fs.maxTxtHeight = getTextHeight("89");

        // Set rect bounds
        fs.rectF.left = refP.x;
        fs.rectF.top = refP.y;

        // rect width distribution= | leftFraction | GapL | = | Right fraction | GapR |
        fs.rectF.right = fs.rectF.left + fs.fDividerL + fs.gapL + fs.equalSignLength +
                fs.fDividerR + fs.gapR;

        // rect height distribution= | N | Gap | Gap | D |
        fs.rectF.bottom = fs.rectF.top + 2 * (Gap + fs.maxTxtHeight);

        //co ordinates of n1
        fs.p1.x = fs.rectF.left + fs.fDividerL / 2f;
        fs.p1.y = fs.rectF.top + fs.maxTxtHeight;

        // co ordinates of d1
        fs.p2.x = fs.rectF.left + fs.fDividerL / 2f;
        fs.p2.y = fs.rectF.bottom;

        //co ordinates of n2
        fs.p3.x = fs.rectF.right - fs.gapR - fs.fDividerR / 2f;
        fs.p3.y = fs.rectF.top + fs.maxTxtHeight;

        //d2
        fs.p4.x = fs.rectF.right - fs.gapR - fs.fDividerR / 2f;
        fs.p4.y = fs.rectF.bottom;

        // divider y coordinate = rectF.centerY
        fs.fDividerY = fs.rectF.centerY();


        // txtCenterY
        // txtCenterY > fDividerY (in mobile)
        fs.txtCenterY = fs.fDividerY + fs.maxTxtHeight/2f - 2;

    }


    public static float getFractionDividerLength(String N, String D) {

        return ((getTextLength(D) > getTextLength(N)) ? 2 * Gap + getTextLength(D) : 2 * Gap + getTextLength(N));
    }

}
