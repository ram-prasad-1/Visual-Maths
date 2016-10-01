package com.visualfiber.apps.visualmaths.model_cv_views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.text.TextUtils;
import android.view.animation.AccelerateInterpolator;

import com.visualfiber.apps.visualmaths.model_cv.ExtraFraction;
import com.visualfiber.apps.visualmaths.model_cv.FractionSet;
import com.visualfiber.apps.visualmaths.model_cv.LcmFraction;
import com.visualfiber.apps.visualmaths.utils.txt.txt;
import com.visualfiber.apps.visualmaths.utils.txt.txtFS;

import java.util.ArrayList;
import java.util.List;


// this view includes support for ExtraFractions
// not just 2 fractions in a fraction set
public class FractionSetViewBig extends FractionSetView {

    // Available operations
    private static final int MIX_FROM_START = 101;
    private static final int CREATE_LCM_FRACTION_FROM_START = 102;
    private static final int REPLACE_WITH_LCM_FRACTION = 103;


    // if you want to hide LcmFractionList set it to false again
    public boolean showLcmFractionList = false;


    // Additional Fractions
    public List<ExtraFraction> extraFractions = null;


    //...... LCM Operation ............
    private List<LcmFraction> lcmFractionList = null;
    private String strngLCM;


    //.......... used in mixing 'fractions with equal denominator' operations ..............
    private int[] arrayFrIndexes;
    private float mixedFrDividerLength = 0;
    private float original_x_Denominator = -99999;
    private float originalGapFS = -99999;
    private float lastF_DivLength;
    private float correction_lastF;
    private float correctionFS;// needed when f.N and f.D are of different text lengths
    private float FS_Fr_Hit_animValue;
    private String mixedNumerator;
    private ExtraFraction lastFractionToBeMixed;

    //...........................................................
    private boolean onHitAnimValueCalculated;


    public FractionSetViewBig(Context context) {
        super(context);

        fs = new FractionSet();
        txt.setTextSize(25);

    }


    //..............  Public Methods...................................................


    // MOSTLY USE THIS
    public FractionSetViewBig(Context context, FractionSet fs, PointF leftTopPoint) {
        super(context);

        txt.setTextSize(32);

        p00.x = leftTopPoint.x;
        p00.y = leftTopPoint.y;


        this.fs = fs;
        txtFS.measure(p00, fs);

    }


    // adding extra fractions
    public void addFraction(ExtraFraction f) {

        // reset operation to no operation
        operation = -999;
        showLcmFractionList = false;

        boolean isAlreadyAdded = false;
        int addedAtIndex = 0;
        if (extraFractions != null) {

            for (ExtraFraction extraFraction : extraFractions) {
                if (extraFraction.uniqueID == f.uniqueID) {
                    isAlreadyAdded = true;
                    break;
                }
                addedAtIndex++;
            }

        }

        if (!isAlreadyAdded) {
            //<editor-fold desc=".......... Add ExtraFraction ............">
            float signLength = txt.getTextLength(f.sign);
            float dividerLength = txtFS.getFractionDividerLength(f.n, f.d);


            if (!f.isSideRight) { // left

                //<editor-fold desc="........ Left .........">

                if (!f.isVisibility_GONE) {
                    fs.gapL = fs.gapL + signLength + dividerLength;
                    txtFS.measure(p00, fs);
                }

                // get the x coordinate for fraction
                float x = fs.rectF.left + fs.fDividerL;

                if (extraFractions != null) {
                    for (ExtraFraction fi : extraFractions) {

                        if (fi.isSideRight) {
                            continue;
                        }

                        if (!fi.isVisibility_GONE) {
                            x = x + fi.getRequiredGap();
                        }

                    }
                }

                // x displacement due to current fraction
                x = x + signLength + dividerLength / 2f;

                float y = fs.rectF.top + fs.maxTxtHeight;


                // save co ordinates of fraction numbers
                // NOTE: DO NOT FORGET TO UPDATE COORDINATES OF
                // RIGHT SIDE EXTRA FRACTIONS IN ON_DRAW
                // WHENEVER A FRACTION IS ADDED ON LEFT SIDE
                f.p1 = new PointF(x, y);

                y = fs.rectF.bottom;
                f.p2 = new PointF(x, y);

                // sign point ( keep Align center when drawing)
                x = x - dividerLength / 2f - signLength / 2f;
                y = fs.txtCenterY;
                f.pSign = new PointF(x, y);


                //</editor-fold>

            } else { // right

                // get the x coordinate for fraction
                // NOTE rectf.right is before any txtFS.measure() call
                float x = fs.rectF.right + signLength + dividerLength / 2;
                float y = fs.rectF.top + fs.maxTxtHeight;

                // save co ordinates of num & den
                f.p1 = new PointF(x, y);

                y = fs.rectF.bottom;
                f.p2 = new PointF(x, y);


                // sign point ( keep Align center when drawing)
                x = x - dividerLength / 2f - signLength / 2f;
                y = fs.txtCenterY;
                f.pSign = new PointF(x, y);


                if (!f.isVisibility_GONE) {
                    fs.gapR = fs.gapR + signLength + dividerLength;
                    txtFS.measure(p00, fs);
                }

            }


            if (extraFractions == null) {
                extraFractions = new ArrayList<>(3);

                if (tempL == null) {
                    tempR = new PointF();
                    tempL = new PointF();
                }
            }

            extraFractions.add(f);
            //</editor-fold>


        } else { // ie isAlreadyAdded

            ExtraFraction efAdded = extraFractions.get(addedAtIndex);

            float signLength = txt.getTextLength(efAdded.sign);
            float dividerLength = txtFS.getFractionDividerLength(efAdded.n, efAdded.d);
            float deltaGap = signLength + dividerLength;

            //<editor-fold desc=".......... fbc clicks ............">
            if (fbc) {
                efAdded.isVisibility_GONE = false;
                if (!f.isSideRight) { // left

                    fs.gapL += deltaGap;

                } else { // right

                    fs.gapR += deltaGap;

                }

            } else { // back click
                efAdded.isVisibility_GONE = true;
                if (!f.isSideRight) { // left

                    fs.gapL -= deltaGap;

                } else { // right

                    fs.gapR -= deltaGap;

                }

            }
            //</editor-fold>

            txtFS.measure(p00, fs);
        }

        invalidate();
    }

    // create & draws lcm fractions at endPt (with endPt as Left-Top corner)
    // if you want to hide created LcmFractionList
    // set showLcmFractionList flag to false

    public void createLcmFrsFromStart(int howManyIncludingStart,
                                      PointF endPt, int unique_ID, boolean setSideToRight) {

        // this method will always get its values from extraFractions list
        // and will add lcmFr's to LcmFrList if not already added
        // will also update oldN, oldD of lcmFr's

        // NOTE THAT FS FRACTION IS FIRST ELEMENT IN lcmFractionList List addition

        // variables reused: endPt --> temp, arrayFrIndex, commonInt --> first desired position in list


        operation = CREATE_LCM_FRACTION_FROM_START;
        isSide_Right = setSideToRight;
        showLcmFractionList = true;

        removeAllListeners();


        // reuse
        arrayFrIndexes = new int[howManyIncludingStart - 1];
        // copy values not just get refs
        temp.x = endPt.x;
        temp.y = endPt.y;

        // reset
        commonInt = 0;


        boolean isAlreadyCreated = false;

        //<editor-fold desc=".......... check if already created .................">


        // NOTE THAT FS FRACTION IS FIRST ELEMENT IN lcmFractionList List
        if (lcmFractionList == null) {

            lcmFractionList = new ArrayList<>(howManyIncludingStart);
        } else {

            int i = 0;
            for (LcmFraction lf : lcmFractionList) {

                if (lf.uniqueID == unique_ID) {

                    isAlreadyCreated = true;

                    commonInt = i; // record position

                    break;

                }

                i++;
            }

        }
        //</editor-fold>


        // start adding lcm of numbers, first start with
        int LCM = (!isSide_Right) ? Integer.parseInt(fs.d1) : Integer.parseInt(fs.d2);

        //<editor-fold desc="......... for loop to add desired ef positions to array and also getting LCM ...........">
        // for loops - add extra fraction on same side to arrayIndex
        int currentIndexArray = 0;
        int i;
        int alreadyMixed = 0;
        int frOnOppositeSide = 0;
        for (i = 0; (i - alreadyMixed - frOnOppositeSide < howManyIncludingStart) && (i < extraFractions.size()); i++) {


            ExtraFraction f = extraFractions.get(i);


            if (currentIndexArray < arrayFrIndexes.length) {


                // if side matches
                if (f.isSideRight == isSide_Right) {

                    // if already mixed skip it
                    if (f.isVisibility_GONE) {
                        alreadyMixed++;


                    } else { // otherwise record position

                        // otherwise save its position to array
                        arrayFrIndexes[currentIndexArray] = i;
                        currentIndexArray++;

                        // LCM
                        LCM = getLCM(LCM, Integer.parseInt(f.d));

                    }

                } else {

                    frOnOppositeSide++;
                }
            }
        }
        //</editor-fold>

        strngLCM = Integer.toString(LCM);


        if (!isAlreadyCreated) {

            // also update oldN, oldD of lcmFrs, right now they are same as N, D of lcm fr

            //<editor-fold desc="........... Add 1st element to list(= fraction from FS) & record position ........">
            String fsN = (isSide_Right) ? fs.n2 : fs.n1;
            String fsD = (isSide_Right) ? fs.d2 : fs.d1;

            PointF pN = (isSide_Right) ? fs.p3 : fs.p1;
            PointF pD = (isSide_Right) ? fs.p4 : fs.p2;

            int fsLcmQ = LCM / Integer.parseInt(fsD);

            // sign for FS is emptystring
            LcmFraction fsLcmFr = new LcmFraction("", fsN, fsD, fsLcmQ, unique_ID);
            fsLcmFr.copyRefPts(new PointF(fs.rectF.left, fs.fDividerY), pN, pD);
            fsLcmFr.oldN = fsN;
            fsLcmFr.oldD = fsD;

            // add lcmFr to list as 1st element of list
            lcmFractionList.add(fsLcmFr);

            // record position in commonInt
            commonInt = lcmFractionList.size() - 1;

            //</editor-fold>


            //<editor-fold desc="........ add remaining lcmFrs to list ...........">

            // add remaining lcmFrs to list
            for (int j = 0; j < arrayFrIndexes.length; j++) {

                ExtraFraction f = extraFractions.get(arrayFrIndexes[j]);

                // find Lcm Quotient
                int lcmQ = LCM / Integer.parseInt(f.d);

                LcmFraction lcmf = new LcmFraction(f.sign, f.n, f.d, lcmQ, unique_ID);
                lcmf.copyRefPts(f.pSign, f.p1, f.p2);
                lcmf.oldN = f.n;
                lcmf.oldD = f.d;

                lcmFractionList.add(lcmf);


            }
            //</editor-fold>
        }


        animFloat.setStartDelay(2);
        animFloat.start();

    }

    // REUSE PARAMETER VALUES FROM createLcmFrsFromStart method
    public void replaceWithLcmFraction(final int howManyIncludingStart,

                                       PointF endPt, int unique_ID, final boolean setSideToRight) {


        // variables reused: endPt --> temp, arrayFrIndex, commonInt --> first desired position in list

        operation = REPLACE_WITH_LCM_FRACTION;
        showLcmFractionList = true;
        animationEnd = false;
        removeAllListeners();

        isSide_Right = setSideToRight;

        // reuse
        arrayFrIndexes = new int[howManyIncludingStart - 1];
        // copy values not just get refs
        temp.x = endPt.x;
        temp.y = endPt.y;

        //<editor-fold desc="......... for loop to add corresponding extraFractions positions to array ...........">

        // for loops - add extra fraction on same side to arrayIndex
        int currentIndexArray = 0;
        int i;
        int alreadyMixed = 0;
        int frOnOppositeSide = 0;
        for (i = 0; (i - alreadyMixed - frOnOppositeSide < howManyIncludingStart) && (i < extraFractions.size()); i++) {


            ExtraFraction f = extraFractions.get(i);


            if (currentIndexArray < arrayFrIndexes.length) {


                // if side matches
                if (f.isSideRight == isSide_Right) {

                    // if already mixed skip it
                    if (f.isVisibility_GONE) {
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


        //<editor-fold desc=".......... get first desired element position (= commonInt) .................">

        // NOTE THAT FS FRACTION IS FIRST ELEMENT IN lcmFractionList List

        int i1 = 0;
        for (LcmFraction lf : lcmFractionList) {

            if (lf.uniqueID == unique_ID) {

                commonInt = i1; // record position
                break;

            }

            i1++;
        }

        //</editor-fold>

        // start adding lcm of numbers, first start with
        int LCM = 1;

        //<editor-fold desc="......... for loop to get LCM ...........">
        for (int j = commonInt; j < (commonInt + howManyIncludingStart) && j < lcmFractionList.size(); j++) {

            LcmFraction lf = lcmFractionList.get(j);

            // LCM
            // note oldD is used so that there is no back click problem
            LCM = getLCM(LCM, Integer.parseInt(lf.oldD));
        }
        //</editor-fold>

        strngLCM = Integer.toString(LCM);



        AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {


                animStart_common();

                // replace with old data
                if (!fbc) {
                    animStart_bbc();

                }


            }

            private void animStart_common() {
                if (!isSide_Right) {  // left

                    fs.showFractionLeft(false);


                } else {

                    fs.showFractionRight(false);
                }

                // hide fractions from extra Fraction List
                for (int i = 0; i < arrayFrIndexes.length; i++) {

                    ExtraFraction ef = extraFractions.get(arrayFrIndexes[i]);

                    ef.showFraction = false;
                    ef.showSign = false;


                }
            }

            private void animStart_bbc() {

                //<editor-fold desc="......... for loop to replace ef list values with old data with gap adjust ment too ...........">

                float gapDecrease = 0;
                for (int j = commonInt; j < (commonInt + howManyIncludingStart) && j < lcmFractionList.size(); j++) {

                    LcmFraction lf = lcmFractionList.get(j);

                    if (j == commonInt) {

                        if (!isSide_Right) {

                            fs.n1 = lf.oldN;
                            fs.d1 = lf.oldD;
                        } else {

                            fs.n2 = lf.oldN;
                            fs.d2 = lf.oldD;
                        }


                    } else {

                        // gap decrease should be noted only in extraFractions not in FS fr
                        // as that will be adjusted in measure pass

                        ExtraFraction ef = extraFractions.get(arrayFrIndexes[j - commonInt - 1]);


                        float divL_Longer = txtFS.getFractionDividerLength(ef.n, ef.d);

                        ef.n = lf.oldN;
                        ef.d = lf.oldD;


                        float divL_Shorter = txtFS.getFractionDividerLength(ef.n, ef.d);

                        gapDecrease = gapDecrease + divL_Longer - divL_Shorter;

                    }

                }
                //</editor-fold>

                if (!isSide_Right) {
                    fs.gapL = fs.gapL - gapDecrease;
                } else {

                    fs.gapR = fs.gapR - gapDecrease;
                }

                txtFS.measure(p00, fs);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationEnd = true;

                // common in both fbc and bbc
                //<editor-fold desc="....... show original fractions ..........">
                if (!isSide_Right) {  // left

                    fs.showN1 = true;
                    fs.showD1 = true;
                    fs.showDividerL = true;


                } else {
                    fs.showN2 = true;
                    fs.showD2 = true;
                    fs.showDividerR = true;
                }

                // hide fractions from extra Fraction List
                for (int i = 0; i < arrayFrIndexes.length; i++) {

                    ExtraFraction ef = extraFractions.get(arrayFrIndexes[i]);

                    ef.showFraction = true;
                    ef.showSign = true;


                }
                //</editor-fold>


                // replace with new data
                if (fbc) {
                    animEnd_fbc();
                }


            }

            private void animEnd_fbc() {

                //<editor-fold desc=".......... for loop to replace fr values (in FS and extraFrList) with new data ..........">
                int lj = 0;
                float gapIncrease = 0;
                for (LcmFraction lf : lcmFractionList) {
                    // new numerator & fraction
                    int newN = lf.lcmQuotient * Integer.parseInt(lf.n);
                    String strN = Integer.toString(newN);

                    if (lj == 0) {

                        if (!isSide_Right) {

                            fs.n1 = strN;
                            fs.d1 = strngLCM;
                        } else {

                            fs.n2 = strN;
                            fs.d2 = strngLCM;
                        }


                    } else {

                        ExtraFraction ef = extraFractions.get(arrayFrIndexes[lj - 1]);

                        float divLnOld = txtFS.getFractionDividerLength(ef.n, ef.d);


                        ef.n = strN;
                        ef.d = strngLCM;

                        // keep track of new gap
                        float divLnNew = txtFS.getFractionDividerLength(ef.n, ef.d);

                        gapIncrease = gapIncrease + divLnNew - divLnOld;

                    }
                    lj++;
                }
                //</editor-fold>


                if (!isSide_Right) {
                    fs.gapL = fs.gapL + gapIncrease;
                } else {

                    fs.gapR = fs.gapR + gapIncrease;
                }

                txtFS.measure(p00, fs);

            }
        };
        animFloat.addListener(adapter);

        animFloat.setStartDelay(4);
        animFloat.start();
    }


    //.......................................................................................


    // mix fractions from start (ie starts from a fraction of FractionSet, not from ExtraFraction)
    // NOTE: BEFORE MIXING ALL FRACTIONS SHOULD HAVE SAME DENOMINATOR
    public void mixFromStart(int mixHowManyIncludingStart, final boolean setSideToRight) {

        operation = MIX_FROM_START;
        isSide_Right = setSideToRight;

        // reset values
        correctionFS = 0;
        correction_lastF = 0;
        FS_Fr_Hit_animValue = 0;
        originalGapFS = -1;
        original_x_Denominator = -1;

        arrayFrIndexes = new int[mixHowManyIncludingStart - 1];

        // reset flags
        animationEnd = false;
        onHitAnimValueCalculated = false;
        commonBoolean = false; // used @ drawing when a4 == 1, one time call


        //<editor-fold desc=".......... get desired ef positions and mixedNumerator ............">
        StringBuilder sb = new StringBuilder();


        int alreadyMixed = getArrayIndexes(mixHowManyIncludingStart, sb);

        //</editor-fold>


        // position(in extrafractionsList) of 'last fraction to be mixed'
        int lastFractionPosition = arrayFrIndexes[arrayFrIndexes.length - 1];
        lastFractionToBeMixed = extraFractions.get(lastFractionPosition);


        // new mixed fraction data
        mixedNumerator = sb.toString();
        String mixedDenominator = (isSide_Right) ? fs.d2 : fs.d1;
        mixedFrDividerLength = txtFS.getFractionDividerLength(mixedNumerator, mixedDenominator);


        // record initial (or original) data
        original_x_Denominator = (isSide_Right) ? fs.p4.x : fs.p2.x;
        originalGapFS = (isSide_Right) ? fs.gapR : fs.gapL;
        lastF_DivLength = txtFS.getFractionDividerLength(lastFractionToBeMixed.n, lastFractionToBeMixed.d);

        // last fraction correction
        if (txt.getTextLength(lastFractionToBeMixed.n) < txt.getTextLength(lastFractionToBeMixed.d)) {

            correction_lastF = (txt.getTextLength(lastFractionToBeMixed.d)
                    - txt.getTextLength(lastFractionToBeMixed.n)) / 2f;

        }


        removeAllListeners();
        AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {

                if (!fbc) {

                    animStart_bbc();
                }

            }

            private void animStart_bbc() {

                if (isSide_Right) { // right

                    fs.showN2 = false;


                } else {

                    fs.showN1 = false;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                animationEnd = true;

                if (!fbc) {

                    animEnd_bbc();

                }

            }

            private void animEnd_bbc() {

                // fraction sign moving upward with a4
                for (int position : arrayFrIndexes) {

                    ExtraFraction f = extraFractions.get(position);

                    f.showSign = true;

                }

            }

        };

        animFloat.addListener(adapter);

        animFloat.setInterpolator(new AccelerateInterpolator());
        animFloat.setDuration(2000);
        animFloat.start();

    }


    // fill arrayIndexes for mixFromStart method
    private int getArrayIndexes(int mixHowManyIncludingStart, StringBuilder sb) {

        // returns number of already mixed fractions ON SAME SIDE
        int alreadyMixed = 0;

        if (fbc) { // fbc

            // first element of mixedNumerator
            // append fraction set numerator
            String s0 = (isSide_Right) ? fs.n2 : fs.n1;
            sb.append(s0);

            //<editor-fold desc=".......... fill array and hide sign ............">
            int currentIndexArray = 0;
            int i;
            int frOnOppositeSide = 0;

            // for loops - add extra fraction on same side to arrayIndex or hide it
            for (i = 0; (i - alreadyMixed - frOnOppositeSide < mixHowManyIncludingStart) && (i < extraFractions.size()); i++) {


                ExtraFraction f = extraFractions.get(i);


                if (currentIndexArray < arrayFrIndexes.length) {


                    // if side matches
                    if (f.isSideRight == isSide_Right) {

                        // if already mixed skip it
                        if (f.isVisibility_GONE) {
                            alreadyMixed++;


                        } else { // otherwise record position

                            // otherwise save its position to array
                            arrayFrIndexes[currentIndexArray] = i;
                            currentIndexArray++;

                            // hide sign
                            f.showSign = false;


                            // append number with sign
                            sb.append(f.sign);
                            sb.append(f.n);
                        }

                    } else {

                        frOnOppositeSide++;
                    }
                }
            }
            //</editor-fold>


            //<editor-fold desc="........... FractionSet fraction Correction ..............">


            // fractionSet correction
            // NOTE: correctionFS = totalCorrection/2
            // as correction is distributed equally on both sides but
            // numerator only has to move one side during anim
            if (!isSide_Right) { // operation happening in left side
                if ((txt.getTextLength(fs.n1) < txt.getTextLength(fs.d1))) {

                    correctionFS = (txt.getTextLength(fs.d1) - txt.getTextLength(fs.n1)) / 2f;

                }
            } else { // right

                if ((txt.getTextLength(fs.n2) < txt.getTextLength(fs.d2))) {


                    correctionFS = (txt.getTextLength(fs.d2) - txt.getTextLength(fs.n2)) / 2f;

                }

            }
            //</editor-fold>

        } else { // bbc


            //<editor-fold desc=".......... get last hidden extraFraction on this side ............">
            int lastHiddenEf = -1; // will throw exception if error
            int jj = 0;
            for (ExtraFraction extraFraction : extraFractions) {

                if (extraFraction.isSideRight == isSide_Right) {

                    if (extraFraction.isVisibility_GONE) {
                        lastHiddenEf = jj;
                    }
                }
                jj++;
            }
            //</editor-fold>


            //<editor-fold desc=".......... fill arrayIndex ............">

            // NOTE: REVERSED FOR LOOP TO GET PROPERLY FORMATED DATA

            int currentIndexArray = arrayFrIndexes.length - 1;
            int i;
            for (i = lastHiddenEf; i >= 0; i--) {


                ExtraFraction f = extraFractions.get(i);


                if (currentIndexArray >= 0) {

                    // if side matches
                    if (f.isSideRight == isSide_Right) {

                        if (f.isVisibility_GONE) {



                            // save its position to array
                            arrayFrIndexes[currentIndexArray] = i;
                            currentIndexArray--;


                            // append number with sign
                            sb.insert(0, f.n);
                            sb.insert(0, f.sign);

                        }
                    }
                } else { // after array is filled get already mixed

                    // if side matches
                    if (f.isSideRight == isSide_Right) {

                        // if already mixed
                        if (f.isVisibility_GONE) {
                            alreadyMixed++;
                        }
                    }
                }
            }
            //</editor-fold>


            // access hacked ef denominator but do not reset now
            ExtraFraction lastFr = extraFractions.get(lastHiddenEf);
            sb.insert(0, lastFr.d);

            // record initial (or original) data
            original_x_Denominator = (isSide_Right) ? fs.p4.x : fs.p2.x; // of no use
            originalGapFS = (isSide_Right) ? fs.gapR : fs.gapL;



            //<editor-fold desc="...... for loop to set VisibilityGONE for remaining fractions on same side ....">

            // fully hide remaining fractions on SAME SIDE
            // we will draw these fractions with this operation's code block instead
            // while animation is running

            for (int j = arrayFrIndexes[arrayFrIndexes.length - 1] + 1; j < (extraFractions.size()); j++) {


                ExtraFraction f = extraFractions.get(j);
                // if side matches
                if (f.isSideRight == isSide_Right) {

                    f.isVisibility_GONE = true;
                }
            }
            //</editor-fold>


            //<editor-fold desc="........... FractionSet fraction Correction  ..............">


            // fractionSet correction
            // NOTE: correctionFS = totalCorrection/2
            // as correction is distributed equally on both sides but
            // numerator only has to move one side during anim
            if (!isSide_Right) { // operation happening in left side
                if ((txt.getTextLength(lastFr.d) < txt.getTextLength(fs.d1))) {

                    correctionFS = (txt.getTextLength(fs.d1) - txt.getTextLength(lastFr.d)) / 2f;

                }
            } else { // right

                if ((txt.getTextLength(lastFr.d) < txt.getTextLength(fs.d2))) {


                    correctionFS = (txt.getTextLength(fs.d2) - txt.getTextLength(lastFr.d)) / 2f;

                }

            }
            //</editor-fold>

        }
        return alreadyMixed;
    }


    private int getLCM(int a, int b) {

        return ((a / gcd(a, b)) * b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {


    }

    @Override
    protected void onDraw(Canvas canvas) {


        // MIXING METHODS
        // NOTE: keep it above of txtFS.draw() method
        if (operation == MIX_FROM_START) {

            if (fbc) {
                draw_MixFromStart_fbc(canvas);
            } else {

                draw_MixFromStart_bbc(canvas);

            }
        }


        if ((operation == CREATE_LCM_FRACTION_FROM_START) || showLcmFractionList) {


            draw_CreateLcmFrFromStart_fbc(canvas);
        }


        if (operation == REPLACE_WITH_LCM_FRACTION) {

            draw_ReplaceWithLcmFraction(canvas);
        }




        // NOTE: keep all operations above super call
        // txtFS.draw() is in super class's onDraw method
        super.onDraw(canvas);


        //<editor-fold desc=".......... Drawing Extra Fractions ..........">
        if (extraFractions != null) {

            tempL.x = fs.rectF.left + fs.fDividerL;
            tempL.y = fs.fDividerY;

            tempR.x = fs.p4.x + fs.fDividerR / 2f;
            tempR.y = fs.fDividerY;

            for (ExtraFraction f : extraFractions) {

                if (!f.isSideRight) { // Left

                    if (!f.isVisibility_GONE) {

                        // here old tempx should be equal to
                        // far right xValue of previous fraction
                        tempL.x = tempL.x + f.getRequiredGap();

                        float dividerLength = txtFS.getFractionDividerLength(f.n, f.d);


                        // draw fraction
                        if (f.showFraction) {
                            tempL.y = fs.fDividerY;

                            // NOTE ref point for fraction is at right
                            txtFS.drawFraction(canvas, f.n, f.d, tempL, 2);

                            // NOTE: UPDATE FRACTION VARIABLES DATA XONLY
                            if (f.p1.x != tempL.x) {

                                tempL.x = tempL.x - dividerLength / 2f;
                                f.p1.x = tempL.x;
                                f.p2.x = tempL.x;

                                // reset tempx changes made just above
                                tempL.x = tempL.x + dividerLength / 2f;

                            }


                        }

                        // draw sign
                        if (f.showSign) {

                            tempL.y = fs.txtCenterY;
                            txt.draw(canvas, f.sign, tempL, dividerLength, 180, 2);


                            float sLength = txt.getTextLength(f.sign);

                            // NOTE: UPDATE pSign Data
                            f.pSign.x = (tempL.x - dividerLength - sLength / 2f);
                        }
                    }


                } else { // Right

                    if (!f.isVisibility_GONE) {

                        tempR.x = tempR.x + f.getRequiredGap();

                        float dividerLength = txtFS.getFractionDividerLength(f.n, f.d);

                        // draw fraction
                        if (f.showFraction) {

                            tempR.y = fs.fDividerY;
                            txtFS.drawFraction(canvas, f.n, f.d, tempR, 2);

                            // NOTE: UPDATE FRACTION VARIABLES DATA XONLY
                            if (f.p1.x != tempR.x) {

                                tempR.x = tempR.x - dividerLength / 2f;
                                f.p1.x = tempR.x;
                                f.p2.x = tempR.x;

                                // reset tempx changes made just above
                                tempR.x = tempR.x + dividerLength / 2f;

                            }
                        }

                        // draw sign
                        if (f.showSign) {


                            // for right side use this instead pSign
                            // now update pSign too
                            tempR.y = fs.txtCenterY;
                            txt.draw(canvas, f.sign, tempR, dividerLength, 180, 2);


                         /*   // Update this fraction as a fraction on left side
                            // may have been added previously
                            f.pSign.x = tempR.x - dividerLength - txt.getTextLength(f.sign) / 2f;
                            f.p1.x = tempR.x - dividerLength / 2f;
                            f.p2.x = tempR.x - dividerLength / 2f;*/

                            float sLength = txt.getTextLength(f.sign);

                            // NOTE: UPDATE pSign Data
                            f.pSign.x = (tempR.x - dividerLength - sLength / 2f);

                        }
                    }

                }

            }

        }
        //</editor-fold>

    }

    private void draw_CreateLcmFrFromStart_fbc(Canvas c) {

        // anim values
        float avf = (operation == CREATE_LCM_FRACTION_FROM_START) ? animatedFraction : 1;

        float a1 = (avf < 0.5f) ? 2 * avf : 1;

        // endPt is temp
        // animation is quadratic x = y^2;
        float dx = (!isSide_Right) ? (temp.x - fs.rectF.left) * a1 * a1 :
                (temp.x - (fs.p3.x - fs.fDividerR / 2f)) * a1 * a1;
        float dy = (temp.y - fs.rectF.top) * a1;

        // for fr
        tempL.y = fs.fDividerY + dy;

        // for sign
        tempR.y = fs.txtCenterY + dy;

        float gapX = 0;

        // x is far right x value for last LcmQ Fr
        float x = 0;

        for (int jj = commonInt; jj < lcmFractionList.size(); jj++) {

            LcmFraction lf = lcmFractionList.get(jj);

            // draw sign tempR used
            tempR.x = lf.pSign.x + dx + gapX * a1;
            txt.draw(c, lf.sign, tempR, 0, 0, 1);

            // draw fr tempL used
            tempL.x = lf.p1.x + dx + gapX * a1;
            txtFS.drawFraction(c, lf.n, lf.d, tempL, 1);

            // update gapX
            gapX = gapX + lf.getAdditionalGaprequired();


            //<editor-fold desc="........ Draw LcmQ Fr ...........">
            if (avf > 0.68f) {

                // draw multiple sign
                float divLength = txtFS.getFractionDividerLength(lf.n, lf.d);
                tempL.x = tempL.x + divLength / 2f;
                tempL.y = fs.txtCenterY + dy;
                txt.draw(c, FractionSet.multiSign, tempL, 0, 0, 0);

                // reset tempL.y
                tempL.y = fs.fDividerY + dy;


                // draw lcmQ fr
                String lcmQ = Integer.toString(lf.lcmQuotient);
                tempL.x = tempL.x + LcmFraction.multiSignLength;
                float divL_LcmFr = txtFS.drawFraction(c, lcmQ, lcmQ, tempL, 0);


                // update xValues
                // x is far right x value for this LcmQ Fr
                x = tempL.x + divL_LcmFr;
            }
            //</editor-fold>
        }

        if (avf > 0.9999f) {

            // draw equal sign
            tempR.x = x;
            tempR.y = fs.txtCenterY + dy;
            txt.draw(c, " = ", tempR, 0, 0, 0);

            x = x + txt.getTextLength(" = ");


            // finding LCM
            LcmFraction lf0 = lcmFractionList.get(0);
            int LCM = lf0.lcmQuotient * Integer.parseInt(lf0.d);
            String strLCM = Integer.toString(LCM);

            //<editor-fold desc="...... for loop to create new 'equal denominator' Fractions ......">
            for (LcmFraction lf : lcmFractionList) {

                // NOTE SAVE NEW X VALUES TO Y COORDINATE OF LcmFraction lf

                // draw sign
                tempR.x = x;
                tempR.y = fs.txtCenterY + dy;
                txt.draw(c, lf.sign, tempR, 0, 0, 0);

                float signLength = TextUtils.isEmpty(lf.sign) ? 0 : txt.getTextLength(lf.sign);

                // NEW X @ Y // hack: pSign.y is hacked for new X of sign
                lf.pSign.y = x + signLength / 2f;

                x = x + signLength;

                // new numerator & fraction
                int newN = lf.lcmQuotient * Integer.parseInt(lf.n);
                String strN = Integer.toString(newN);

                tempR.x = x;
                tempR.y = fs.fDividerY + dy;
                float divLengthNew = txtFS.drawFraction(c, strN, strLCM, tempR, 0);

                // NEW X @ fN.y // hack: p1.y is hacked for new X of lcmFr
                lf.p1.y = x + divLengthNew / 2f;

                x = x + divLengthNew;

            }
            //</editor-fold>


        }

    }

    private void draw_ReplaceWithLcmFraction(Canvas c) {

        float a1 = animatedFraction;

        //<editor-fold desc="........... set alpha --> for old fractions ..............">

        String fsN = (!isSide_Right) ? fs.n1 : fs.n2;
        String fsD = (!isSide_Right) ? fs.d1 : fs.d2;
        float px = (!isSide_Right) ? fs.p1.x : fs.p3.x;
        float divL_FS = (!isSide_Right) ? fs.fDividerL : fs.fDividerR;

        float pyFr = fs.fDividerY;
        float pySign = fs.txtCenterY;

        txt.setAlpha((int) (255 * (1 - a1)));

        // draw FS fraction
        tempL.x = px;
        tempL.y = pyFr;
        txtFS.drawFraction(c, fsN, fsD, tempL, 1);

        tempL.x = px + divL_FS / 2f;

        // draw ExtraFractions (only those which will be replaced )
        for (int j = 0; j < arrayFrIndexes.length; j++) {

            ExtraFraction ef = extraFractions.get(arrayFrIndexes[j]);

            // draw sign
            tempR.x = tempL.x;
            tempR.y = pySign;
            txt.draw(c, ef.sign, tempR, 0, 0, 0);

            tempL.x = tempL.x + txt.getTextLength(ef.sign);

            float divL = txtFS.drawFraction(c, ef.n, ef.d, tempL, 0);

            tempL.x = tempL.x + divL;

        }
        txt.resetAlpha();
        //</editor-fold>


        if (!animationEnd) {

            //<editor-fold desc="...... for loop to create new 'equal denominator' Fractions ......">

            for (int j = commonInt; j < (commonInt + (arrayFrIndexes.length + 1)) && j < lcmFractionList.size(); j++) {

                // bug alert for loop type changed
                LcmFraction lf = lcmFractionList.get(j);

                // NOTE: NEW X VALUES ARE SAVED IN Y COORDINATE (pSign.y or p1.y) OF LcmFraction lf

                // new numerator & fraction
                int newN = lf.lcmQuotient * Integer.parseInt(lf.n);
                String strN = Integer.toString(newN);

                if (j == commonInt) { // i.e. this lf is actually of FS fraction

                    float dx = lf.p1.y - px;  // lf.p1.y has new x so used it

                    float newDivY = temp.y + fs.rectF.height() / 2f;
                    float dy = newDivY - fs.fDividerY;

                    tempL.x = lf.p1.y - dx * a1 * a1;
                    tempL.y = newDivY - dy * a1;

                    txtFS.drawFraction(c, strN, strngLCM, tempL, 1);


                } else { // i.e. this lf is of ExtraFraction fraction

                    ExtraFraction ef = extraFractions.get(arrayFrIndexes[j - commonInt - 1]);

                    // lf.p1.y has new x so used it
                    float dx = lf.p1.y - ef.p1.x;

                    float newDivY = temp.y + fs.rectF.height() / 2f;
                    float dy = newDivY - fs.fDividerY;

                    tempL.x = lf.p1.y - dx * a1 * a1;
                    tempL.y = newDivY - dy * a1;

                    txtFS.drawFraction(c, strN, strngLCM, tempL, 1);

                    // draw sign
                    float newSignY = temp.y + (fs.txtCenterY - fs.rectF.top);

                    dx = lf.pSign.y - ef.pSign.x;
                    dy = newSignY - fs.txtCenterY;

                    tempL.x = lf.pSign.y - dx * a1 * a1;
                    tempL.y = newSignY - dy * a1;
                    txt.draw(c, lf.sign, tempL, 0, 0, 1);
                }

            }
            //</editor-fold>
        }

    }

    private void draw_MixFromStart_fbc(Canvas c) {

        // todo animate denominator from R2L not L2R


        float a4 = (animatedFraction <= 0.25f) ? 4 * animatedFraction : 1;
        float a5 = (animatedFraction <= 0.25f) ? 0 : 1.333f * (animatedFraction - 0.25f);
        // used only for denominator anim
        float a6 = (animatedFraction <= 0.75f) ? 0 : 4 * (animatedFraction - 0.75f);


        float gapReduced; // stores cumulative gap to be reduced between fractions

        gapReduced = correctionFS * 2 + txtFS.Gap;

        if (a4 < 1) {


            //<editor-fold desc=".......... Num sign upward Anim ............">
            for (int i = 0; i < arrayFrIndexes.length; i++) {

                ExtraFraction f = extraFractions.get(arrayFrIndexes[i]);


                if (a4 < 1) {
                    // fraction sign moving upward with a4
                    f.pSign.y = fs.txtCenterY * (1 - a4) + (fs.rectF.top + fs.maxTxtHeight) * a4;
                    txt.draw(c, f.sign, f.pSign, 0, 0, 1);
                }
            }
            //</editor-fold>


        } else {

            //<editor-fold desc=".......... hide toBeMixed ef's (One time call) ............">
            if (!commonBoolean) {

                for (int i = 0; i < arrayFrIndexes.length; i++) {

                    ExtraFraction f = extraFractions.get(arrayFrIndexes[i]);

                    f.showFraction = false;

                }

                //<editor-fold desc="...... for loop to set VisibilityGONE for remaining fractions on same side ....">

                // fully hide remaining fractions on SAME SIDE
                // we will draw these fractions with this operation's code block instead
                // while animation is running

                for (int j = arrayFrIndexes[arrayFrIndexes.length - 1] + 1; j < (extraFractions.size()); j++) {


                    ExtraFraction f = extraFractions.get(j);
                    // if side matches
                    if (f.isSideRight == isSide_Right) {

                        f.isVisibility_GONE = true;
                    }
                }
                //</editor-fold>


                commonBoolean = true;
            }
            //</editor-fold>

            //<editor-fold desc=".......... for loop to reduce gap bw fractions ............">
            for (int i = 0; i < arrayFrIndexes.length; i++) {

                ExtraFraction f = extraFractions.get(arrayFrIndexes[i]);


                // CORRECTIONS CALCULATIONS
                // needed when f.N and f.D are of different text lengths
                // correction is zero if TextLength(f.n) > TextLength(f.d)
                float correctionEF = 0; // correction due to extra fraction

                // fraction correction
                if ((txt.getTextLength(f.n) < txt.getTextLength(f.d))) {
                    correctionEF = (txt.getTextLength(f.d) - txt.getTextLength(f.n)) / 2f;
                }


                // DRAW fraction sign
                temp.x = f.pSign.x - a5 * gapReduced;
                temp.y = fs.rectF.top + fs.maxTxtHeight;
                txt.draw(c, f.sign, temp, 0, 0, 1);


                // pre increase in cumulative gap
                gapReduced = gapReduced + txtFS.Gap + correctionEF;

                // DRAW extra fraction
                temp.x = f.p1.x - a5 * gapReduced;
                txt.draw(c, f.n, temp, 0, 0, 1);

                // post increase in cumulative gap
                gapReduced = gapReduced + correctionEF + txtFS.Gap;

            }
            //</editor-fold>


            //<editor-fold desc="............ draw divider line new ...........">
            // DRAW divider line new
            if ((a4 == 1) && !animationEnd) {
                if (!isSide_Right) { // left

                    float x = (lastFractionToBeMixed.p1.x + lastF_DivLength / 2f) * (1 - a5) +
                            (fs.rectF.left + mixedFrDividerLength) * a5;
                    c.drawLine(fs.rectF.left, fs.fDividerY, x, fs.fDividerY, txt.paint);
                } else {  // right

                    float x = (lastFractionToBeMixed.p1.x + lastF_DivLength / 2f) * (1 - a5) +
                            (fs.p3.x - fs.fDividerR / 2f + mixedFrDividerLength) * a5;

                    c.drawLine(fs.p3.x - fs.fDividerR / 2f, fs.fDividerY, x, fs.fDividerY, txt.paint);

                }
            }
            //</editor-fold>

            // NOTE: gapReduced for remaining fractions and gap
            // = total gapReduced - lastGap - lastCorrection
            gapReduced = gapReduced - txtFS.Gap - correction_lastF;


            //<editor-fold desc="...... for loop to draw remaining fractions on SAME SIDE ........">
            for (int j = arrayFrIndexes[arrayFrIndexes.length - 1] + 1; j < extraFractions.size(); j++) {

                ExtraFraction ef = extraFractions.get(j);


                // check if both operation side and fraction side are same
                // otherwise do nothing as other side will take care of themselves
                // with default drawing implementation
                if (((isSide_Right) && (ef.isSideRight)) ||
                        ((!isSide_Right) && (!ef.isSideRight))) { // both on same side

                    // draw fraction
                    temp.y = fs.fDividerY;
                    temp.x = ef.p1.x * (1 - a5) + (ef.p1.x - gapReduced) * a5;
                    txtFS.drawFraction(c, ef.n, ef.d, temp, 1);

                    // draw sign
                    temp.y = fs.txtCenterY;
                    temp.x = ef.pSign.x * (1 - a5) + (ef.pSign.x - gapReduced) * a5;

                    txt.draw(c, ef.sign, temp, 0, 0, 1);
                }


            }
            //</editor-fold>

            // ADJUST GAP IN FRACTION SET
            if (!isSide_Right) { // left
                fs.gapL = originalGapFS - (gapReduced) * a5;
            } else { // right
                fs.gapR = originalGapFS - (gapReduced) * a5;

            }


            // adjustments after animation is ended
            if (animationEnd) {

                //<editor-fold desc=".......... Hack last of mixed ef for fs unmixed numerato  ............">
                // NOTE: Hack before value of Fs.Num is changed
                int last = arrayFrIndexes.length - 1;
                ExtraFraction lastEf = extraFractions.get(arrayFrIndexes[last]);

                // hack: last mixed ef is hacked for fs unmixed numerator
                lastEf.d = isSide_Right ? fs.n2 : fs.n1;
                //</editor-fold>


                //<editor-fold desc=".......... Readjust gap in FS ............">
                // READJUST GAP IN FRACION SET
                // gap should be so that it can just fill remaining fractions
                if (!isSide_Right) { // Left
                    fs.gapL = originalGapFS - (mixedFrDividerLength + gapReduced
                            - fs.fDividerL);
                    fs.showN1 = true;

                    fs.n1 = mixedNumerator;

                } else {  // right
                    fs.gapR = originalGapFS - (mixedFrDividerLength + gapReduced
                            - fs.fDividerR);
                    fs.showN2 = true;

                    fs.n2 = mixedNumerator;

                }
                //</editor-fold>


                //<editor-fold desc="............ for loop to hide already mixed fractions .............">
                for (int j = 0; j < arrayFrIndexes.length; j++) {

                    ExtraFraction ef = extraFractions.get(arrayFrIndexes[j]);

                    if (ef.isSideRight == isSide_Right) {

                        ef.isVisibility_GONE = true;

                        ef.showFraction = true;
                        ef.showSign = true;

                    }

                }
                //</editor-fold>


                //<editor-fold desc="...... for loop to show remaining fractions on SAME SIDE ........">

                // NOTE that we do not need to worry about fractions on other side
                // as we left them untouched through out the whole animation
                // so their default drawing implementation will take care of them
                for (int j = arrayFrIndexes[arrayFrIndexes.length - 1] + 1; j < extraFractions.size(); j++) {

                    ExtraFraction ef = extraFractions.get(j);

                    if (ef.isSideRight == isSide_Right) {
                        ef.isVisibility_GONE = false;
                    }

                }
                //</editor-fold>


            }


        }

        txtFS.measure(p00, fs);


        // todo animate fs numerator with a5 not a9

        //<editor-fold desc=".......... animate fs numerator based on correctionFS ............">
        // animation in FS
        if ((correctionFS > 0f) && !animationEnd) {

            // total distance moved by
            // first sign right next to FS fraction on this side
            float dTotal = correctionFS * 2 + txtFS.Gap;

            // distance moved by
            // first sign right next to FS fraction on this side
            // when it hits the FS numerator
            float dHit = correctionFS + txtFS.Gap;

            if (dTotal * a5 >= dHit) {

                if (!onHitAnimValueCalculated) {
                    FS_Fr_Hit_animValue = a5;
                    onHitAnimValueCalculated = true;
                }

                float a9 = (a5 - FS_Fr_Hit_animValue) / (1 - FS_Fr_Hit_animValue);

                float dx = (correctionFS) * a9;


                if (!isSide_Right) { // left
                    fs.showN1 = false;
                    temp.x = fs.p1.x - dx;
                    temp.y = fs.rectF.top + fs.maxTxtHeight;
                    txt.draw(c, fs.n1, temp, 0, 0, 1);
                } else {  // right

                    fs.showN2 = false;
                    temp.x = fs.p3.x - dx;
                    temp.y = fs.rectF.top + fs.maxTxtHeight;
                    txt.draw(c, fs.n2, temp, 0, 0, 1);


                }

            }

        }
        //</editor-fold>

        //<editor-fold desc=".......... ADJUST DENOMINATOR ............">
        // ADJUST DENOMINATOR with a6 anim value
        // NOTE: don't forget to do it after txtFS.measure()
        if (!isSide_Right) { // left

            fs.p2.x = original_x_Denominator * (1 - a6) + (fs.rectF.left + mixedFrDividerLength / 2f) * a6;
        } else {   // right

            fs.p4.x = original_x_Denominator * (1 - a6) +
                    (fs.rectF.right - fs.gapR - fs.fDividerR + mixedFrDividerLength / 2f) * a6;


        }
        //</editor-fold>
    }

    private void draw_MixFromStart_bbc(Canvas c) {

        // reset anim value (0 to 1)
        float av = 1 - animatedFraction;

        float a5 = (av <= 0.75f) ? 1.333f * av : 1;
        float a4 = (av > 0.9f) ? 10 * (av - 0.9f) : 0;

        float gapIncreased = 0; // stores cumulative gap to be reduced between fractions


        if (a5 < 1) {


            //<editor-fold desc=".......... fs Num anim ............">
            // get last fr to get value of FS num
            ExtraFraction lastFr = extraFractions.get(arrayFrIndexes[arrayFrIndexes.length - 1]);

            String fsNum = lastFr.d; // access hacked

            float xNum = isSide_Right ? fs.p4.x - fs.fDividerR / 2f : fs.rectF.left;
            xNum += txtFS.Gap;

            tempL.x = xNum + correctionFS * a5;
            tempL.y = fs.rectF.top + fs.maxTxtHeight;
            txt.draw(c, fsNum, tempL, 0, 0, 0);

            xNum += txt.getTextLength(fsNum);

            //</editor-fold>

            float x = xNum;
            gapIncreased = correctionFS * 2 + txtFS.Gap;

            //<editor-fold desc="......... for loop to increase gap bw mixFraction numerators ...........">
            int i;
            for (i = 0; i < arrayFrIndexes.length; i++) {

                ExtraFraction f = extraFractions.get(arrayFrIndexes[i]);


                // CORRECTIONS CALCULATIONS
                // needed when f.N and f.D are of different text lengths
                // correction is zero if TextLength(f.n) > TextLength(f.d)
                float correctionEF = 0; // correction due to extra fraction

                // fraction correction
                if ((txt.getTextLength(f.n) < txt.getTextLength(f.d))) {
                    correctionEF = (txt.getTextLength(f.d) - txt.getTextLength(f.n)) / 2f;
                }

                // DRAW fraction sign
                temp.x = x + a5 * gapIncreased;
                temp.y = fs.rectF.top + fs.maxTxtHeight;
                txt.draw(c, f.sign, temp, 0, 0, 0);

                x += txt.getTextLength(f.sign); // increase x

                // pre increase in cumulative gap
                gapIncreased = gapIncreased + txtFS.Gap + correctionEF;

                // draw extra fraction
                temp.x = x + a5 * gapIncreased;
                txt.draw(c, f.n, temp, 0, 0, 0);

                x += txt.getTextLength(f.n); // increase x

                // post increase in cumulative gap
                gapIncreased = gapIncreased + correctionEF + txtFS.Gap;


            }
            //</editor-fold>


            //<editor-fold desc="............ draw divider line new ...........">

            if (!isSide_Right) { // left

                float startX = fs.rectF.left;
                float endX = startX + fs.fDividerL + gapIncreased * a5;
                c.drawLine(startX, fs.fDividerY, endX, fs.fDividerY, txt.paint);

            } else {  // right

                float startX = fs.p3.x - fs.fDividerR / 2f;
                float endX = startX + fs.fDividerR + gapIncreased * a5;
                c.drawLine(startX, fs.fDividerY, endX, fs.fDividerY, txt.paint);

            }
            //</editor-fold>

            // NOTE: gapReduced for remaining fractions and gap
            // = total gapReduced - lastGap - lastCorrection
            gapIncreased = gapIncreased - txtFS.Gap - correction_lastF;

            //<editor-fold desc="...... for loop to draw remaining fractions on SAME SIDE ........">
            for (int j = arrayFrIndexes[arrayFrIndexes.length - 1] + 1; j < extraFractions.size(); j++) {

                ExtraFraction ef = extraFractions.get(j);


                // check if both operation side and fraction side are same
                // otherwise do nothing as other side will take care of themselves
                // with default drawing implementation
                if (((isSide_Right) && (ef.isSideRight)) ||
                        ((!isSide_Right) && (!ef.isSideRight))) { // both on same side

                    // draw fraction
                    temp.y = fs.fDividerY;
                    temp.x = ef.p1.x * (1 - a5) + (ef.p1.x + gapIncreased) * a5;
                    txtFS.drawFraction(c, ef.n, ef.d, temp, 1);

                    // draw sign
                    temp.y = fs.txtCenterY;
                    temp.x = ef.pSign.x * (1 - a5) + (ef.pSign.x + gapIncreased) * a5;

                    txt.draw(c, ef.sign, temp, 0, 0, 1);
                }


            }
            //</editor-fold>

            //<editor-fold desc=".......... increase gap in Fs ............">
            // ADJUST GAP IN FRACTION SET
            if (!isSide_Right) { // left
                fs.gapL = originalGapFS + (gapIncreased) * a5;
            } else { // right
                fs.gapR = originalGapFS + (gapIncreased) * a5;

            }
            // NOTE measure pass
            txtFS.measure(p00, fs);

            //</editor-fold>


        } else { // when a5 ==1

            //<editor-fold desc=".......... access hacked value of fs Num and readjust FS gap (Only One time call) ..">
            if (!onHitAnimValueCalculated) {


                //<editor-fold desc="......... for loop to calculate increased gap ...........">

                gapIncreased = correctionFS * 2 + txtFS.Gap;

                int i;
                for (i = 0; i < arrayFrIndexes.length; i++) {

                    ExtraFraction f = extraFractions.get(arrayFrIndexes[i]);


                    // CORRECTIONS CALCULATIONS
                    // needed when f.N and f.D are of different text lengths
                    // correction is zero if TextLength(f.n) > TextLength(f.d)
                    float correctionEF = 0; // correction due to extra fraction

                    // fraction correction
                    if ((txt.getTextLength(f.n) < txt.getTextLength(f.d))) {
                        correctionEF = (txt.getTextLength(f.d) - txt.getTextLength(f.n)) / 2f;
                    }

                    // pre increase in cumulative gap
                    gapIncreased = gapIncreased + txtFS.Gap + correctionEF;


                    // post increase in cumulative gap
                    gapIncreased = gapIncreased + correctionEF + txtFS.Gap;

                }


                // NOTE: gapReduced for remaining fractions and gap
                // = total gapReduced - lastGap - lastCorrection
                gapIncreased = gapIncreased - txtFS.Gap - correction_lastF;
                //</editor-fold>


                //<editor-fold desc=".......... access hacked FS num and readjust gap ............">
                // get last fr to get value of FS num
                ExtraFraction lastFr = extraFractions.get(arrayFrIndexes[arrayFrIndexes.length - 1]);
                String fsNum = lastFr.d; // access hacked

                if (isSide_Right) { // right

                    fs.n2 = fsNum;
                    lastFr.d = fs.d2; // reset
                    fs.showN2 = true;

                    float unmixedDivL = txtFS.getFractionDividerLength(fsNum, fs.d2);

                    // fs.fDividerR_unmixed + length(extraFractions) = mixedDivLength + gapIncreased + originalGAp
                    fs.gapR = originalGapFS + (mixedFrDividerLength + gapIncreased
                            - unmixedDivL); // reset gap

                } else {

                    fs.n1 = fsNum;
                    lastFr.d = fs.d1;
                    fs.showN1 = true;

                    float unmixedDivL = txtFS.getFractionDividerLength(fsNum, fs.d1);

                    // reset gap
                    // gap should be so that it can just fill remaining fractions
                    fs.gapL = originalGapFS + (mixedFrDividerLength + gapIncreased
                            - unmixedDivL);
                }
                //</editor-fold>


                txtFS.measure(p00, fs);

                //<editor-fold desc=".......... set unmixed efs to visible ............">

                for (int position : arrayFrIndexes) {

                    ExtraFraction ef = extraFractions.get(position);

                    ef.isVisibility_GONE = false;
                    ef.showFraction = true;
                    ef.showSign = false;
                }
                //</editor-fold>


                //<editor-fold desc="...... for loop to show remaining fractions on SAME SIDE ........">

                // NOTE that we do not need to worry about fractions on other side
                // as we left them untouched through out the whole animation
                // so their default drawing implementation will take care of them
                for (int j = arrayFrIndexes[arrayFrIndexes.length - 1] + 1; j < extraFractions.size(); j++) {

                    ExtraFraction ef = extraFractions.get(j);

                    if (ef.isSideRight == isSide_Right) {
                        ef.isVisibility_GONE = false; // back problem:
                    }

                }
                //</editor-fold>

                onHitAnimValueCalculated = true; // calculate only once


            }
            //</editor-fold>


            if (!animationEnd) {

                // fraction sign moving upward with a4
                for (int position : arrayFrIndexes) {

                    ExtraFraction f = extraFractions.get(position);

                    f.pSign.y = fs.txtCenterY * a4 + (fs.rectF.top + fs.maxTxtHeight) * (1 - a4);
                    txt.draw(c, f.sign, f.pSign, 0, 0, 1);

                }
            }


        }


    }
}
