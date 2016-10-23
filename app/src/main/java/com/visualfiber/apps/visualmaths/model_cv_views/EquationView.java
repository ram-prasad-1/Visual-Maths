package com.visualfiber.apps.visualmaths.model_cv_views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model_cv.LS.FractionX;
import com.visualfiber.apps.visualmaths.model_cv.LS.LcmFractionX;
import com.visualfiber.apps.visualmaths.model_cv.LS.MX_plus_C;
import com.visualfiber.apps.visualmaths.utils.mth;
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

    //..........................................

    List<LcmFractionX> lcmFractionList;
    public boolean showLcmFractionList = false; // if you want to hide LcmFractionList set it to false again


    protected int operation;

    private static final int CREATE_LCM_FRACTION = 1;

    //...............

    // reusable per method, must not be hacked
    private boolean cmBoolean; // common Boolean
    private boolean animEnd;

    private int cmInt;
    private int cmInt2;
    private int[] arrayFrIndexes;

    private String cmString;  // common String

    private PointF cmPointF = new PointF(); // reusable per method, not per frame


    //<editor-fold desc=".......... Anim ............">
    protected float animatedFraction;

    public ValueAnimator animFloat;

    protected ValueAnimator.AnimatorUpdateListener updatelistener;
    //</editor-fold>

    // activity handle for enabling jumper buttons
    public RequestActivityPipe activityHandle;
    protected AnimatorListenerAdapter enableJumperButtonsAdapter;
    public boolean fbc = true;


    public EquationView(Context context) {
        super(context);

        setupAnimation();
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

//            Log.d("EquationView", "addFraction: xi initial --> " + xi_initial);

            // save final xi
            float increase = (xi - xi_initial);
            rectF.right += increase;  // rf right will increase in both cases

            if (!f.isSideRight) { // left

                es_x_start = xi;
                Log.d("EquationView", "addFraction: es start --> " + es_x_start);

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

            } else {

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

    // for part 1 --> ispart1 = true
    public void createLcmFraction(int howManyIncludingStart,
                                  PointF endPt, int unique_ID, boolean setSideToRight, boolean isPart1) {

        // variables reused --> cmBoolean --> is Lcm possible, cmPointf --> endPoint
        //variables reused --> cmInt2 = record position, cmString --> LCM

        operation = CREATE_LCM_FRACTION;
        showLcmFractionList = true;


        cmInt = 0;
        cmInt2 = 0;
        cmBoolean = false;
        animEnd = false;

        cmPointF.x = endPt.x;
        cmPointF.y = endPt.y;

        arrayFrIndexes = new int[howManyIncludingStart];

        boolean isAlreadyCreated = false;

        //<editor-fold desc=".......... check if already created .................">


        if (lcmFractionList == null) {

            lcmFractionList = new ArrayList<>(howManyIncludingStart);
        } else {

            int i = 0;
            for (LcmFractionX lf : lcmFractionList) {

                if (lf.uniqueID == unique_ID) {

                    isAlreadyCreated = true;

                    cmInt2 = i; // record position

                    break;

                }

                i++;
            }

        }
        //</editor-fold>

        //<editor-fold desc="......... for loop to add desired fraction positions to array ...........">

        int currentIndexArray = 0;
        int i;
        int alreadyMixed = 0;
        int frOnOppositeSide = 0;
        for (i = 0; (i - alreadyMixed - frOnOppositeSide < howManyIncludingStart) && (i < fractionList.size()); i++) {


            FractionX f = fractionList.get(i);


            if (currentIndexArray < arrayFrIndexes.length) {


                // if side matches
                if (f.isSideRight == setSideToRight) {

                    // if already mixed skip it
                    if (f.visibility_GONE) {
                        alreadyMixed++;


                    } else { // otherwise record position

                        // otherwise save its position to array
                        arrayFrIndexes[currentIndexArray] = i;
                        currentIndexArray++;

                    }

                } else {

                    frOnOppositeSide++;
                }
            }
        }
        //</editor-fold>

        //<editor-fold desc=".......... find out which LCM case is applicable ............">
        boolean isAll_M_Zero = true;
        boolean isAll_C_Zero = true;
        boolean bothNonZeroAndMultiple = true;  // case 3: like 2x + 1,  4x + 2

        int mForCase3 = 0; // common m
        int cForCase3 = 0; // common c
        boolean case3FirstGcd = false; // first gcd calculation in case 3

        for (int index = 0; index < arrayFrIndexes.length; index++) {

            FractionX f = fractionList.get(arrayFrIndexes[index]);

            if (!f.denm.m.equals("0")) {

                isAll_M_Zero = false;
            }

            if (!f.denm.c.equals("0")) {

                isAll_C_Zero = false;
            }


            //<editor-fold desc=".......... case 3 (like 2x + 1,  4x + 2) ............">

            if (index < arrayFrIndexes.length - 1) {

                FractionX fNext = fractionList.get(arrayFrIndexes[index + 1]);


                if (f.denm.bothNonZero() && fNext.denm.bothNonZero()) {

                    int m1 = Integer.parseInt(f.denm.m);
                    int c1 = Integer.parseInt(f.denm.c);

                    int m2 = Integer.parseInt(fNext.denm.m);
                    int c2 = Integer.parseInt(fNext.denm.c);

                    boolean ratioEqual = (float) m2 / m1 == (float) c2 / c1;

                    if (!ratioEqual) {

                        bothNonZeroAndMultiple = false;
                    }

                    if (!case3FirstGcd) {

                        mForCase3 = m1;
                        cForCase3 = c1;
                        case3FirstGcd = true;

                    }

                    mForCase3 = mth.gcd(mForCase3, m2);
                    cForCase3 = mth.gcd(cForCase3, c2);


                }


            }
            //</editor-fold>


        }
        //</editor-fold>

        // start adding lcm of numbers, first start with
        int LCM = 1;


        //<editor-fold desc=".......... find LCM ............">
        for (int position : arrayFrIndexes) {

            FractionX fx = fractionList.get(position);

            if (isAll_M_Zero) { // only constants in denominator

                // LCM
                LCM = mth.lcm(LCM, Integer.parseInt(fx.denm.c));

            } else if (isAll_C_Zero) { // only variables in denominator

                // LCM
                LCM = mth.lcm(LCM, Integer.parseInt(fx.denm.m));

            } else if (bothNonZeroAndMultiple) {


                // LCM for m
                // no need to find lcm for c as multiplier for both numbers is same
                LCM = mth.lcm(LCM, Integer.parseInt(fx.denm.m));


            }


        }
        //</editor-fold>

        cmString = Integer.toString(LCM);

        Log.d("EquationView", "createLcmFraction: lcm --> " + LCM);

        if (!isAlreadyCreated) {

            for (int position : arrayFrIndexes) {

                FractionX f = fractionList.get(position);


                int denmOriginal = 1;
                int lcmQ = 1; // Lcm Quotient
                MX_plus_C numLcmFr = new MX_plus_C(f.num);
                ;
                MX_plus_C denmLcmFr = null;

                if (isAll_M_Zero) {

                    denmOriginal = Integer.parseInt(f.denm.c);

                    denmLcmFr = new MX_plus_C("0", cmString); // cmString is lcm

                } else if (isAll_C_Zero) {

                    denmOriginal = Integer.parseInt(f.denm.m);

                    denmLcmFr = new MX_plus_C(cmString, "0");

                } else if (bothNonZeroAndMultiple) {

                    // here we found lcm on m only, so num is also corresponding to this
                    // no need to do this for c as multiplier for both c and m is same
                    denmOriginal = Integer.parseInt(f.denm.m);

                    lcmQ = LCM / denmOriginal;

                    denmLcmFr = new MX_plus_C(f.denm);
                    denmLcmFr.multiplyWithInt(lcmQ);

                } else {

                    // bug alert: add code when nothing is common in denm: denmLcmFr =  ??  here

                }


                // find Lcm Quotient
                lcmQ = LCM / denmOriginal;

                // multiply numLcmFraction with lcm Quotient
                numLcmFr.multiplyWithInt(lcmQ);

                LcmFractionX lcmf = new LcmFractionX(f.sign, numLcmFr, denmLcmFr, lcmQ, unique_ID);

                // add list to
                lcmFractionList.add(lcmf);


                // copy original values
                lcmf.num.copyValues(f.num);
                lcmf.denm.copyValues(f.denm);
                lcmf.copyValues(f);
                Log.d("EquationView", "createLcmFraction: numLcm --> " + numLcmFr.toString());
                Log.d("EquationView", "createLcmFraction: DenumLcm --> " + denmLcmFr.toString());


            }

        }

        if (isPart1) {
            animFloat.start();
        } else {

            animatedFraction = 1;

            invalidate();
        }

    }


    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // left align IMPORTANT
        txt.paint.setTextAlign(Paint.Align.LEFT);

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rectF, paint);

        // draw equal sign
        //
        temp1.x = es_x_start + es_dx;
        temp1.y = txtCenterY;
        txt.draw(canvas, equalSign, temp1, 0, 0, 0);


        if (operation == CREATE_LCM_FRACTION) {

            draw_createLcmFraction(canvas);
        }

        if (fractionList != null) {

            for (FractionX f : fractionList) {

                if (!f.visibility_GONE) {
                    drawFraction(canvas, f);
                }
            }

        }

    }

    private void draw_createLcmFraction(Canvas c) {

        float a1 = animatedFraction < 0.7f? 1.428571429f * animatedFraction : 1;


        float dx = (cmPointF.x - lcmFractionList.get(0).xStart) * a1;
        float dy = (cmPointF.y - rectF.top) * a1;

        float dGap = 0;

        Log.d("EquationView", "draw_createLcmFraction: xStart --> " + lcmFractionList.get(0).xStart);


        for (LcmFractionX f : lcmFractionList) {

            float xStart = f.num.dxm = f.num.dxc = f.denm.dxm = f.denm.dxc = dx + dGap * a1;

            float xEnd;


            if (!f.isDenominator_1) { // denominator != 1

                //<editor-fold desc=".......... numerator ............">
                if (!f.num.m.equals("0")) {

                    // - ve sign
                    if (!f.num.ipm) {
                        c.drawText(ss.Minus, f.num.xm + f.num.dxm - gtl(ss.Minus), rectF.top + maxTxtHeight + dy, txt.paint);
                    }

                    // m
                    c.drawText(f.num.m, f.num.xm + f.num.dxm, rectF.top + maxTxtHeight + dy, txt.paint);

                    // x
                    c.drawText(f.num.x, f.num.xm + f.num.dxm + f.num.dxx, rectF.top + maxTxtHeight + dy, txt.paint);
                }


                if (!f.num.c.equals("0")) {

                    // c sign
                    String minusC = f.num.m.equals("0") ? ss.Minus : ss.Minus_S; // m == 0 ho to no space
                    String sign = f.num.ipc ? ss.Plus_S : minusC;

                    if (!(f.num.m.equals("0") && f.num.ipc)) { // do not draw sign if m = 0 & c +ve
                        c.drawText(sign, f.num.xc + f.num.dxc - gtl(sign), rectF.top + maxTxtHeight + dy, txt.paint);
                    }

                    // c
                    c.drawText(f.num.c, f.num.xc + f.num.dxc, rectF.top + maxTxtHeight + dy, txt.paint);


                }
                //</editor-fold>


                //<editor-fold desc=".......... denominator ............">
                if (!f.denm.m.equals("0")) {

                    // - ve sign
                    if (!f.denm.ipm) {
                        c.drawText(ss.Minus, f.denm.xm + f.denm.dxm - gtl(ss.Minus), rectF.bottom + dy, txt.paint);
                    }

                    // m
                    c.drawText(f.denm.m, f.denm.xm + f.denm.dxm, rectF.bottom + dy, txt.paint);

                    // x
                    c.drawText(f.denm.x, f.denm.xm + f.denm.dxm + f.denm.dxx, rectF.bottom + dy, txt.paint);
                }

                if (!f.denm.c.equals("0")) {

                    // c sign
                    String minusC = f.denm.m.equals("0") ? ss.Minus : ss.Minus_S; // m == 0 ho to no space
                    String sign = f.denm.ipc ? ss.Plus_S : minusC;

                    if (!(f.denm.m.equals("0") && f.denm.ipc)) { // do not draw sign if m = 0 & c +ve
                        c.drawText(sign, f.denm.xc + f.denm.dxc - gtl(sign), rectF.bottom + dy, txt.paint);
                    }

                    // c
                    c.drawText(f.denm.c, f.denm.xc + f.denm.dxc, rectF.bottom + dy, txt.paint);
                }
                //</editor-fold>


                //<editor-fold desc=".......... divider line ............">

                 xStart += f.xStart;

                float lengthC_num = f.num.c.equals("0")? 0: gtl(f.num.c);
                float lengthC_denm = f.denm.c.equals("0")? 0: gtl(f.denm.c);

                xEnd = Math.max(f.num.xc + f.num.dxc + lengthC_num,
                        f.denm.xc + f.denm.dxc + lengthC_denm);
                xEnd += txtFS.Gap;

                c.drawLine(xStart , rectF.centerY() + dy, xEnd, rectF.centerY() + dy, txt.paint);
                //</editor-fold>

                //<editor-fold desc=".......... f.sign ............">
                // sign considerations: only draw if
                // 1. if FF, -ve fr sign & d != 1   OR     2. if !FF & d != 1
                if (f.isFirstOnThisSide && !isSignPlus(f.sign) && !f.isDenominator_1
                        || (!f.isFirstOnThisSide && !f.isDenominator_1)) {

                    float signStartX = xStart - gtl(f.sign);
                    c.drawText(f.sign, signStartX, txtCenterY + dy, txt.paint);

                }
                //</editor-fold>


                //<editor-fold desc=".......... lcm Quotient fraction ............">

                if (animatedFraction > 0.996f) {
                    c.drawText(ss.MultiSign_S, xEnd , txtCenterY + dy, txt.paint);

                    String lcmQ = Integer.toString(f.lcmQuotient);
                    temp1.x = xEnd + gtl(ss.MultiSign_S);
                    temp1.y = rectF.centerY() + dy;
                    txtFS.drawFraction(c,lcmQ, lcmQ, temp1, 0);
                    txt.paint.setTextAlign(Paint.Align.LEFT); // do not forget to reset the text alignment
                }
                //</editor-fold>


            } else { // denominator = 1


                // bug alert: agar denominator == 1 to bhi upar wala code hi kam karega
                // bug alert: as lcm lete wakta 1 ko bhi print karna jaruri h

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


            dGap += f.getAdditionalGaprequired();
        }

    }

    private void drawFraction(Canvas c, FractionX f) {


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

            float lengthC_num = f.num.c.equals("0")? 0: gtl(f.num.c);
            float lengthC_denm = f.denm.c.equals("0")? 0: gtl(f.denm.c);

            float xEnd = Math.max(f.num.xc + f.num.dxc + lengthC_num,
                    f.denm.xc + f.denm.dxc + lengthC_denm);

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

    private void setupAnimation() {

        // floating point anim
        animFloat = ValueAnimator.ofFloat(0, 1);
        animFloat.setInterpolator(new LinearInterpolator());
        animFloat.setDuration(2000);
        animFloat.setStartDelay(2);

        //<editor-fold desc="......... updatelistener.........">

        updatelistener = new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {


                if (fbc) {

                    animatedFraction = (float) animFloat.getAnimatedValue();

                } else {
                    animatedFraction = 1 - (float) animFloat.getAnimatedValue();

                }

                invalidate();


            }


        };
        //</editor-fold>


        // add update listener
        animFloat.addUpdateListener(updatelistener);

        // jumper buttons listener
        enableJumperButtonsAdapter = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // activate movement buttons
                activityHandle.setEnabledJumperButtons(true);
            }
        };
        animFloat.addListener(enableJumperButtonsAdapter);
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
