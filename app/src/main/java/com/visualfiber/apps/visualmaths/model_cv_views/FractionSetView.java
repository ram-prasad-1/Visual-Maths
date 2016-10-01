package com.visualfiber.apps.visualmaths.model_cv_views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model_cv.FractionSet;
import com.visualfiber.apps.visualmaths.model_cv.MixedNumber;
import com.visualfiber.apps.visualmaths.model_cv.ThreeStrings;
import com.visualfiber.apps.visualmaths.utils.pathanimation.AnimatorPath;
import com.visualfiber.apps.visualmaths.utils.pathanimation.PathEvaluator2;
import com.visualfiber.apps.visualmaths.utils.pathanimation.PathPoint;
import com.visualfiber.apps.visualmaths.utils.txt.txt;
import com.visualfiber.apps.visualmaths.utils.txt.txtFS;

import java.util.ArrayList;
import java.util.List;


// this view includes support just for 2 fractions of the fraction set
public class FractionSetView extends View {

    // if you want to hide LcmFractionList set it to false again
    protected boolean showSimplifiedFraction = false;


    //<editor-fold desc=".......... Available operations constants ............">
    private static final int MOVE_D1_TO_RIGHT = 1;
    private static final int MOVE_D2_TO_LEFT = 2;

    private static final int INCREASE_GAP_L = 3;

    private static final int INVERT_FRACTION_SET = 4;
    private static final int CHANGE_SIDES_FS_FRACTIONS = 5;

    private static final int DRAW_SUM_MIXED_NUMBERS = 6;
    private static final int REPLACE_MIXED_NUMBERS_WITH_SUM = 7;

    private static final int CREATE_SIMPLIFY_FRACTION = 8;
    private static final int REPLACE_WITH_SIMPLIFIED_FRACTION = 9;
    private static final int DRAW_FINAL_ANSWER_01 = 10;
    //</editor-fold>

    protected int operation;

    protected boolean isSide_Right = false; // by default side right

    // ref point for FractionSet
    protected PointF p00 = new PointF(30, 100);

    public FractionSet fs;

    protected Handler handler;

    //<editor-fold desc=".......... Anim ............">
    protected float animatedFraction;

    public ValueAnimator animObject;
    public ValueAnimator animFloat;

    protected ValueAnimator.AnimatorUpdateListener updatelistener;
    protected ValueAnimator.AnimatorUpdateListener updatelistenerFloat;

    private PathPoint avPathPt;
    private PathEvaluator2 pathEvaluator;
    //</editor-fold>


    // ref points for extra fraction added
    protected PointF tempL; // reusable per frame only
    protected PointF tempR; // reusable per frame only

    protected PointF temp = new PointF(); // reusable per method, must not be hacked


    protected String mixedNumerator;

    // used in simplification operations
    private ThreeStrings threeStrings;

    // save mixed numbers to list
    private List<MixedNumber> mixedNumberList;

    protected boolean animationEnd;
    protected boolean shrinkEnabled;

    // activity handle for enabling jumper buttons
    public RequestActivityPipe activityHandle;
    protected AnimatorListenerAdapter enableJumperButtonsAdapter;


    public boolean fbc; // forward Button Clicked

    // reusable per method, must not be hacked
    protected boolean commonBoolean;
    protected int commonInt;

    // for testing
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //...........................................................


    public FractionSetView(Context context) {
        super(context);

        fs = new FractionSet();
        txt.setTextSize(25);

        setupAnimation();
    }


    // MOSTLY USE THIS
    public FractionSetView(Context context, FractionSet fs, PointF leftTopPoint) {
        super(context);

        txt.setTextSize(25);

        p00.x = leftTopPoint.x;
        p00.y = leftTopPoint.y;


        this.fs = fs;
        txtFS.measure(p00, fs);

        setupAnimation();


    }


    //..............  Public Methods...................................................

    // add fraction set through method
    public void setFractionSet(FractionSet fs) {
        this.fs = fs;

        // reset operation
        operation = -989;


        txtFS.measure(p00, fs);

        invalidate();
        requestLayout();

    }

    public void setReferencePoint(PointF pointF) {

        p00.x = pointF.x;
        p00.y = pointF.y;
        txtFS.measure(p00, fs);
    }


    // inverts FractionSet
    // NOTE: Only invert when no extra fraction is displayed
    // as in that case inversion is mathematically wrong
    public void invertFractionSet() {

        operation = INVERT_FRACTION_SET;

        removeAllListeners();

        AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {

                fs.invertFraction = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                fs.updateAfterInvert();
            }
        };
        animFloat.addListener(adapter);

        animFloat.setStartDelay(4);
        animFloat.start();
    }

    public void changeSidesFsFractions() {

        operation = CHANGE_SIDES_FS_FRACTIONS;


        removeAllListeners();

        animationEnd = false;

        if (tempL == null) {
            tempR = new PointF();
            tempL = new PointF();
        }


        AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {

                fs.showFractionRight(false);
                fs.showFractionLeft(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                animationEnd = true;

                fs.showFractionRight(true);
                fs.showFractionLeft(true);

                fs.gapL = 0;

                //<editor-fold desc="          FrL <---> FrR  ">
                String ts = fs.n1;
                fs.n1 = fs.n2;
                fs.n2 = ts;

                ts = fs.d1;
                fs.d1 = fs.d2;
                fs.d2 = ts;
                //</editor-fold>

                txtFS.measure(p00, fs);

                invalidate();
            }

        };
        animFloat.addListener(adapter);

        animFloat.setStartDelay(4);

        animFloat.start();
    }

    // increase GapL by a amount
    public void increaseGapL(int gap) {

        operation = INCREASE_GAP_L;

//        this.gap = gap;


        animFloat.start();
    }

    public void moveD1toRight() {

        operation = MOVE_D1_TO_RIGHT;

        if (handler == null) {
            handler = new Handler();
        }

        // reset path evaluator flag
        // adds backward functionality to anim object
        pathEvaluator.reverseTravel = !fbc;

        //NOTE: DO NOT FORGET TO SET EVALUATOR AFTER SETTING OBJECT VALUES
        // get path points and pass it to animator
        AnimatorPath path = fs.getPathD1toRight();
        animObject.setObjectValues(path.getPoints().toArray());
        animObject.setEvaluator(pathEvaluator);

        removeAllListeners();


        AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {


            @Override
            public void onAnimationStart(Animator animation) {


                fs.showDividerL = false;
                fs.moveToCenterNumberIndex = 1;

                if (fbc) {

                    // hide D1 & instead display Temp1 there
                    // ( Temp1 will add multiply sign to the number )
                    // Hide dividerL & move N1 to center

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            fs.hideNumber(2);
                            fs.showTemp1 = true;

                        }
                    }, 500);

                } else {

                    //
                    fs.temp1 = FractionSet.multiSign + fs.d1;
                    fs.showTemp1 = true;

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            fs.showTemp1 = false;
                            fs.showD1 = true;
                        }
                    }, 500);


                }


            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pathEvaluator.reverseTravel = false;


                if (!fbc) {

                    fs.showDividerL = true;
                    fs.showN1 = true;
                    fs.moveToCenterNumberIndex = -999;
                }
            }

        };
        animObject.addListener(adapter);


        animObject.start();
    }

    // Do not to forget to increase the GapL before calling this method
    public void moveD2toLeft() {

        operation = MOVE_D2_TO_LEFT;

        if (handler == null) {
            handler = new Handler();
        }

        // part-1 anim for increasing gapL
        // part-2 anim for moving d2


        // reset path evaluator flag
        // adds backward functionality to anim object
        pathEvaluator.reverseTravel = !fbc;


        //NOTE: DO NOT FORGET TO SET EVALUATOR AFTER SETTING OBJECT VALUES
        // get path points and pass it to animator
        AnimatorPath path = fs.getPathD2toLeft();
        animObject.setObjectValues(path.getPoints().toArray());
        animObject.setEvaluator(pathEvaluator);


        // adapter for part-1
        AnimatorListenerAdapter adapterForFloatAnim = new AnimatorListenerAdapter() {


            @Override
            public void onAnimationStart(Animator animation) {


            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if (fbc) {
                    //NOTE: DO NOT FORGET TO SET EVALUATOR AFTER SETTING OBJECT VALUES
                    // part-2 anim for moving D2 to left
                    // get path points and pass it to animator
                    AnimatorPath path = fs.getPathD2toLeft();
                    animObject.setObjectValues(path.getPoints().toArray());
                    animObject.setEvaluator(pathEvaluator);

                    // delay for starting anim part-2
                    animObject.setStartDelay(300);

                    // start part 2
                    animObject.start();
                } else {

                    // reset delay
                    animFloat.setStartDelay(2);
                }

            }

        };

        // adapter for part-2
        AnimatorListenerAdapter adapterForObjectAnim = new AnimatorListenerAdapter() {


            @Override
            public void onAnimationStart(Animator animation) {

                fs.showDividerR = false;
                fs.moveToCenterNumberIndex = 3;

                if (fbc) {

                    // hide D2 & instead display Temp1 there
                    // ( Temp1 will add multiply sign to the number )
                    // Hide dividerR & move N2 to center

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            fs.hideNumber(4);
                            fs.showTemp1 = true;

                        }
                    }, 500);

                } else {

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            fs.showTemp1 = false;
                            fs.showD2 = true;

                        }
                    }, 500);
                }


            }

            @Override
            public void onAnimationEnd(Animator animation) {

                pathEvaluator.reverseTravel = false;

                if (!fbc) {

                    // start part 1
                    animFloat.setStartDelay(160);
                    animFloat.start();


                    fs.showDividerR = true;
                    fs.showN2 = true;
                    fs.moveToCenterNumberIndex = -999;
                }


            }

        };

        removeAllListeners();

        animFloat.addListener(adapterForFloatAnim);
        animObject.addListener(adapterForObjectAnim);


        if (fbc) {
            animFloat.start();
        } else {
            animObject.start();
        }
    }

    // create Simplify Fraction
    public void createSimplifyFraction(PointF endPt, boolean setSideToRight) {

        operation = CREATE_SIMPLIFY_FRACTION;

        isSide_Right = setSideToRight;
        showSimplifiedFraction = true;

        removeAllListeners();

        // copy values not just get refs
        temp.x = endPt.x;
        temp.y = endPt.y;

        // tempL/R also used when drawing so do null check
        if (tempL == null) {
            tempR = new PointF();
            tempL = new PointF();
        }

        if (threeStrings == null) {
            threeStrings = new ThreeStrings();
        }

        getNewValuesAfterSimplification(threeStrings);

        AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {


            @Override
            public void onAnimationStart(Animator animation) {


                if (!fbc) {
                    showSimplifiedFraction = false;
//                    operation = -99;


                    // show temp
                    fs.temp1 = FractionSet.multiSign + fs.d1;
                    fs.showTemp1 = true;

                    if (!isSide_Right) {


                    } else { // right

                        fs.showFractionRight(true);
                        fs.moveToCenterNumberIndex = 1;
                        fs.animatedFraction = 1;

                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if (!fbc) {
                    showSimplifiedFraction = false;
                    operation = MOVE_D1_TO_RIGHT;
                    animatedFraction = 1;


                    // show temp
                    fs.temp1 = FractionSet.multiSign + fs.d1;
                    fs.showTemp1 = true;

                }

            }

        };
        animFloat.addListener(adapter);

        animFloat.start();
    }

    // replace with simplified fraction
    public void replaceWithSimplifiedFr(PointF endPt, final boolean setSideToRight) {

        operation = REPLACE_WITH_SIMPLIFIED_FRACTION;
        isSide_Right = setSideToRight;

        removeAllListeners();

        // copy values not just get refs
        temp.x = endPt.x;
        temp.y = endPt.y;

        AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {


                fs.showTemp1 = false;
                fs.showFractionLeft(false);
                fs.showFractionRight(false);

                // reset move to center value
                fs.moveToCenterNumberIndex = -99;


            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if (!fbc) {

                    showSimplifiedFraction = true;


                }

            }
        };
        animFloat.addListener(adapter);

        animFloat.setStartDelay(4);

        animFloat.start();
    }

    // show or hide simplified fraction
    public void hideSimplifiedFraction() {

        operation = REPLACE_WITH_SIMPLIFIED_FRACTION;

        if (fbc) {
            showSimplifiedFraction = false;
        } else {

            showSimplifiedFraction = true;
        }


        invalidate();
    }


    // draw the sum of mixed number numerator over it
    // DO NOT FORGET TO CALL replaceMixedNumbersWithSum() JUST AFTER THIS METHOD CALL
    public void drawSumOverMixedNo(String sum, int uniqueID, final boolean setSideToRight) {

        // Variables reused: sum --> mixedNumerator,  pt(sum) --> temp

        isSide_Right = setSideToRight;


        // reuse
        mixedNumerator = sum;

        boolean isAlreadyAdded = false;

        if (fbc) {

            operation = DRAW_SUM_MIXED_NUMBERS;

            //<editor-fold desc=".......... check if already added ............">
            if (mixedNumberList == null) {
                mixedNumberList = new ArrayList<>(2);

            } else {

                for (MixedNumber mn : mixedNumberList) {

                    if (mn.uniqueID == uniqueID) {
                        isAlreadyAdded = true;
                        break;
                    }

                }
            }
            //</editor-fold>


            //<editor-fold desc=".......... add if not already added ............">
            if (!isAlreadyAdded) {

                if (isSide_Right) { // right

                    MixedNumber newMn = new MixedNumber(fs.n2, uniqueID);

                    mixedNumberList.add(newMn);


                } else { // left

                    MixedNumber newMn = new MixedNumber(fs.n1, uniqueID);

                    mixedNumberList.add(newMn);

                }
            }
            //</editor-fold>


        } else {

            operation = -348348;
        }


        requestLayout();
        invalidate();


    }


    public void replaceMixedNumbersWithSum(final String sum, final int uniqueID, final boolean setSideToRight) {
        operation = REPLACE_MIXED_NUMBERS_WITH_SUM;

        mixedNumerator = sum;
        isSide_Right = setSideToRight;

        commonInt = uniqueID; // save to commonInt

        // Variables reused: sum --> mixedNumerator,  pt(sum) --> temp, commonInt --> uniqueID


        // assuming txtLength(sum) < txtLength(numerator) always
        // i.e. no expansion of dividerLength

        // sum --> mixedNumerator,  pt(sum) --> temp, side --> side_L0R1
        // fractionDividerOriginalLength --> originalGapFS


      /*  // reuse variables
        // original Div Length
        originalGapFS = (side_L0R1 == 0) ? fs.fDividerL : fs.fDividerR;*/

        // reset flag
        // fraction shrink due to N > D but Sum < D (in length)
        shrinkEnabled = false;

        String fsN = (!isSide_Right) ? fs.n1 : fs.n2;
        String fsD = (!isSide_Right) ? fs.d1 : fs.d2;

        boolean c1 = txt.getTextLength(fsN) > txt.getTextLength(fsD);
        boolean c2 = txt.getTextLength(mixedNumerator) < txt.getTextLength(fsD);

        // if length --> N > D but S < D
        if (c1 && c2) {
            shrinkEnabled = true;
        }

        removeAllListeners();

        AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {

                //<editor-fold desc=".......... hide FS num ............">
                if (!isSide_Right) {

                    fs.showN1 = false;
                } else {

                    fs.showN2 = false;
                }
                //</editor-fold>

                if (!fbc) {

                    String olnMn = "Abe nhi mila be";

                    //<editor-fold desc=".......... get old mixed number ............">


                    for (MixedNumber mn : mixedNumberList) {

                        if (mn.uniqueID == uniqueID) {

                            olnMn = mn.str;

                        }
                    }
                    //</editor-fold>

                    if (!isSide_Right) {
                        fs.n1 = olnMn;

                    } else {
                        fs.n2 = olnMn;

                    }

                    if (handler == null) {
                        handler = new Handler();
                    }


                    txtFS.measure(p00, fs);


                }

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                //<editor-fold desc=".......... show FS num ............">
                if (!isSide_Right) {

                    fs.showN1 = true;
                } else {

                    fs.showN2 = true;
                }
                //</editor-fold>


                if (fbc) {
                    if (!isSide_Right) { // mixedNumerator = sum
                        fs.n1 = mixedNumerator;
                    } else {
                        fs.n2 = mixedNumerator;
                    }


                    txtFS.measure(p00, fs);


                } else { // bbc

                    // good trick: you can change fbc internally and call other methods

                    // for drawing lights
                    fbc = true; // do not forget to change it
                    drawSumOverMixedNo(sum, uniqueID, setSideToRight);

                }

               /* // todo add code to shrink FS with animation
                if (shrinkEnabled) {
                    txtFS.measure(p00, fs);
                }*/

                invalidate();
            }
        };
        animFloat.addListener(adapter);

        animFloat.setStartDelay(4);
        animFloat.setDuration(1000);

        animFloat.start();
    }


    // final answer
    public void drawFinalAnswer() {

        if (fbc) {
            operation = DRAW_FINAL_ANSWER_01;
        } else {

            // just show replaced fraction
            operation = REPLACE_WITH_SIMPLIFIED_FRACTION;
        }

        invalidate();


    }


    //.......................................................................................

    private void setupAnimation() {


        // custom evaluator for object animation
        pathEvaluator = new PathEvaluator2();


        // animate along a path
        AnimatorPath ap = new AnimatorPath();
        animObject = ValueAnimator.ofObject(pathEvaluator, ap.getPoints().toArray());
        animObject.setInterpolator(new LinearInterpolator());
        animObject.setDuration(2000);
        animObject.setStartDelay(2);

        // floating point anim
        animFloat = ValueAnimator.ofFloat(0, 1);
        animFloat.setInterpolator(new LinearInterpolator());
        animFloat.setDuration(2000);
        animFloat.setStartDelay(2);

        //<editor-fold desc="......... updatelisteners.........">
        updatelistener = new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                if (fbc) {
                    avPathPt = (PathPoint) animObject.getAnimatedValue();
                    animatedFraction = animObject.getAnimatedFraction();

                } else {  // i.e. backward button clicked

                    animatedFraction = 1 - animObject.getAnimatedFraction();
                    avPathPt = (PathPoint) animObject.getAnimatedValue();


                }


                invalidate();


            }


        };


        // updatelistener for float Animator
        updatelistenerFloat = new ValueAnimator.AnimatorUpdateListener() {
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
        animObject.addUpdateListener(updatelistener);
        animFloat.addUpdateListener(updatelistenerFloat);

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
        animObject.addListener(enableJumperButtonsAdapter);
    }

    // this disables jumper buttons also
    protected void removeAllListeners() {


        if (animFloat != null) {
            animFloat.removeAllListeners();
        }

        if (animObject != null) {
            animObject.removeAllListeners();

        }

        // disable jumper buttons
        activityHandle.setEnabledJumperButtons(false);

        // add listener to enable jumper buttons
        if (operation == MOVE_D1_TO_RIGHT || operation == MOVE_D2_TO_LEFT) {
            animObject.addListener(enableJumperButtonsAdapter);
        } else {
            animFloat.addListener(enableJumperButtonsAdapter);
        }

    }


    protected int gcd(int a, int b) {
        if (a == 0 || b == 0) return a + b; // base case
        return gcd(b, a % b);
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
        super.onDraw(canvas);

        paint.setTextSize(35);
        paint.setStyle(Paint.Style.STROKE);

//        canvas.drawRect(fs.rectF, paint);
//        canvas.drawText("" + step, 200, 500, txt.paint);


        //<editor-fold desc="....... L2R or R2L Denominator Movement Operations .......">
        if (operation == MOVE_D1_TO_RIGHT) {

            fs.animatedFraction = animatedFraction;
            // avPathPt is PathPoint object received from updateListener
            if (avPathPt != null) {

                fs.p2.x = avPathPt.mX;
                fs.p2.y = avPathPt.mY;
            }


        }

        if (operation == MOVE_D2_TO_LEFT) {

            if (animFloat.isRunning()) {
                // As gap is changing we need to call txt.measure() on each frame
                // to find latest coordinates of fraction numbers
                fs.gapL = (animatedFraction * txtFS.getTextLength(FractionSet.multiSign + fs.d2));
                txtFS.measure(p00, fs);
            }

            if (animObject.isRunning()) {


                fs.animatedFraction = animatedFraction;
                // avPathPt is PathPoint object received from updateListener
                if (avPathPt != null) {

                    fs.p4.x = avPathPt.mX;
                    fs.p4.y = avPathPt.mY;
                }
            }


        }

        //</editor-fold>

        if (operation == INVERT_FRACTION_SET) {
            // this will animate fraction set
            fs.animatedFraction = animatedFraction;
        }

        if (operation == DRAW_SUM_MIXED_NUMBERS) {

            draw_DrawSumMixedNos(canvas);
        }


        if (operation == REPLACE_MIXED_NUMBERS_WITH_SUM) {

            draw_ReplaceMixedNumbersWithSum(canvas);
        }


        if ((operation == CREATE_SIMPLIFY_FRACTION) || showSimplifiedFraction) {

            draw_CreateSimplifyFraction(canvas);
        }

        if (operation == REPLACE_WITH_SIMPLIFIED_FRACTION || operation == DRAW_FINAL_ANSWER_01
                /*|| (operation == HIDE_SIMPLIFIED_FRACTION)*/) {

            draw_ReplaceWithSimplifiedFraction(canvas);
        }

        if ((operation == CHANGE_SIDES_FS_FRACTIONS) && !animationEnd) {

            draw_ChangeSidesFsFractions(canvas);
        }

        if (operation == DRAW_FINAL_ANSWER_01) {

            draw_DrawFinalAnswer_01(canvas);

        }


        // draw fraction set
        // NOTE: should be at last
        txtFS.draw(canvas, fs);

    }

    private void draw_DrawFinalAnswer_01(Canvas c) {

        boolean drawFraction = !(threeStrings.newD.equals("1"));

        int ansNum = Integer.parseInt(threeStrings.newN) * Integer.parseInt(threeStrings.newTemp1);
        String ansNumStr = Integer.toString(ansNum);

        tempL.y = drawFraction ? fs.rectF.bottom + 80 : fs.rectF.bottom + 32;

        // NOTE: side is opposite of replaceWithSimplifiedFr method
        if (isSide_Right) { // x or unknown variable is on left

            float numLength = txt.getTextLength(fs.n1); // fs.n1 = unknown


            // draw answer sign
            tempR.x = fs.p1.x - numLength / 2f - 12;
            tempR.y = tempL.y - 3;
            txt.draw(c, "⇒", tempR, 0, 0, 2);


            tempL.x = fs.p1.x;
            // draw left numerator
            txt.draw(c, fs.n1, tempL, 0, 0, 1);

            // equal sign
            tempL.x += numLength / 2f;
            txt.draw(c, fs.equalSign, tempL, 0, 0, 0);

            tempL.x += fs.equalSignLength;
            if (drawFraction) {

                tempR.x = tempL.x;
                tempR.y = tempL.y - (fs.txtCenterY - fs.fDividerY);
                txtFS.drawFraction(c, ansNumStr, threeStrings.newD, tempR, 0);
            } else {

                txt.draw(c, ansNumStr, tempL, 0, 0, 0);
            }

        } else { // x or unknown variable is on right

            // todo add drawFinalAnswer code for right side
            throw new IllegalArgumentException("TODO");

        }
    }

    private void draw_ChangeSidesFsFractions(Canvas c) {

        float a1 = animatedFraction;

        float deltaGap = (fs.fDividerR - fs.fDividerL);

        fs.gapL = deltaGap * a1;


        //<editor-fold desc="........ draw fractions ...........">

        // draw left fraction
        // NOTE: when changing sides of left fraction don't forget to
        // consider the effect of gapL
        tempL.x = fs.p1.x * (1 - a1) + (fs.p3.x - deltaGap / 2f + deltaGap) * a1;
        tempL.y = fs.fDividerY;
        txtFS.drawFraction(c, fs.n1, fs.d1, tempL, 1);

        // draw right fraction
        tempR.x = fs.p3.x * (1 - a1) + (fs.p1.x + deltaGap / 2f) * a1;
        tempR.y = fs.fDividerY;
        txtFS.drawFraction(c, fs.n2, fs.d2, tempR, 1);
        //</editor-fold>


    }

    private void draw_ReplaceWithSimplifiedFraction(Canvas c) {

        // todo hide whole fraction set and redraw fractions with gap adjustment

        float a1 = (operation == REPLACE_WITH_SIMPLIFIED_FRACTION) ?
                animatedFraction : 1;
        float a2 = (a1 < 0.3f) ? 3.33f * a1 : 1;


        //<editor-fold desc="....... Hide existing Fraction with alpha --> 0 .........">
        String fsN = (!isSide_Right) ? fs.n1 : fs.n2;
        String fsD = (!isSide_Right) ? fs.d1 : fs.d2;
        float px = (!isSide_Right) ? fs.p1.x : fs.p3.x;


        txt.setAlpha((int) (255 * (1 - a2)));

        // draw FS fraction
        tempL.x = px;
        tempL.y = fs.fDividerY;
        txtFS.drawFraction(c, fsN, fsD, tempL, 1);

        // draw temp1
        tempL.x = fs.tempHandle.x;
        tempL.y = fs.tempHandle.y;
        txt.draw(c, fs.temp1, tempL, 0, 0, 1);

        txt.resetAlpha();
        //</editor-fold>

        //<editor-fold desc="....... draw simplified fraction ........">

        float dx0 = (!isSide_Right) ? (threeStrings.refX - fs.rectF.left) :
                (threeStrings.refX - (fs.rectF.left + fs.fDividerL
                        + fs.gapL + fs.equalSignLength));

        // endPt is temp
        // animation is quadratic x = y^2;
        float dx = dx0 * a1 * a1;
        float dy = (temp.y - fs.rectF.top) * a1;

        boolean isDnmIgnored = threeStrings.newD.equals("1");

        // temp_R_Y ( CRAZY MATHS )
        tempR.y = (isDnmIgnored) ? temp.y + (fs.txtCenterY - fs.rectF.top) - dy :
                temp.y + (fs.fDividerY - fs.rectF.top) - dy;


        // draw simplified fraction
        tempR.x = threeStrings.refX - dx;
        drawSimplifiedFractionFinal(c, tempR);
        //</editor-fold>


        if (!isSide_Right) {

            //<editor-fold desc="...... adjust gap on left .........">
            float oldGap = txt.getTextLength(fs.temp1);

            float oldSpace = fs.fDividerL + oldGap;

            float newSpace = threeStrings.requiredGap;

            float deltaGap = (newSpace - oldSpace) * a1;

            fs.gapL = oldGap + deltaGap;
            //</editor-fold>


            //<editor-fold desc="...... draw adjusted right numerator .........">

            float dR = (fs.p3.x - (fs.rectF.left + fs.fDividerL +
                    fs.gapL + fs.equalSignLength + txt.getTextLength(fs.n2) / 2f)) * a1;

            tempL.x = fs.p3.x - dR;
            tempL.y = fs.txtCenterY;

            // draw right numerator
            txt.draw(c, fs.n2, tempL, 0, 0, 1);

            //</editor-fold>

        } else {   // right

            float numLength = txt.getTextLength(fs.n1);

            //<editor-fold desc="........... old code if left num adjusts itself to left .........">


        /*

            // gap adjustment
            float deltaGap = (fs.fDividerL - numLength) * a1;
            fs.gapL = 0 - deltaGap;

            //  adjustment in left numerator
            tempL.x = fs.p1.x * (1 - a1) + (fs.rectF.left + numLength / 2f) * a1;
            tempL.y = fs.txtCenterY;*/


            //</editor-fold>


            // gap adjustment
            float deltaGap = (fs.fDividerL / 2f - numLength / 2f) * a1;
            fs.gapL = 0 - deltaGap;

            // No need to do any adjustment in left numerator
            tempL.x = fs.p1.x;
            tempL.y = fs.txtCenterY;


            // draw left numerator
            txt.draw(c, fs.n1, tempL, 0, 0, 1);



            /*//<editor-fold desc="...... adjust gap on right .........">

            // actually no need now

            float oldGap = txt.getTextLength(fs.temp1);

            float oldSpace = fs.fDividerR + oldGap;

            float newSpace = threeStrings.requiredGap;

            float deltaGap = (newSpace - oldSpace) * a1;

            fs.gapR = oldGap + deltaGap;
            //</editor-fold>*/


        }


    }

    private void draw_CreateSimplifyFraction(Canvas c) {

        // anim values
        float avf = (operation == CREATE_SIMPLIFY_FRACTION) ? animatedFraction : 1;

        float a1 = (avf < 0.33f) ? 3.03f * avf : 1;

        boolean drawFinalFraction = (avf > 0.999f);

        // endPt is temp
        // animation is quadratic x = y^2;
        float dx = (!isSide_Right) ? (temp.x - fs.rectF.left) * a1 * a1 :
                (temp.x - (fs.p3.x - fs.fDividerR / 2f)) * a1 * a1;
        float dy = (temp.y - fs.rectF.top) * a1;

        // for fr
        tempL.y = fs.fDividerY + dy;

        // for tempX
        tempR.y = fs.txtCenterY + dy;


        if (!isSide_Right) {

            // draw fr tempL used
            tempL.x = fs.p1.x + dx;
            txtFS.drawFraction(c, fs.n1, fs.d1, tempL, 1);
        } else {


            // draw fr tempL used
            tempL.x = fs.p3.x + dx;
            txtFS.drawFraction(c, fs.n2, fs.d2, tempL, 1);

        }

        // draw temp
        tempR.x = fs.tempHandle.x + dx;
        txt.draw(c, fs.temp1, tempR, 0, 0, 1);

        float tmpLength = txt.getTextLength(fs.temp1);

        // x is far right x value for temp1
        float x = tempR.x + tmpLength / 2f;

        boolean isDenomnAlreadyCut = false;

        // used in crossing lines
        int g = (int) (txtFS.Gap * 0.7f);


        //<editor-fold desc="....... cut temp1 and fr Denominator ..............">

        if (avf > 0.45f) {

            int temp1;
            int frD;
            int gcd_temp;

            // get temp & Fr denominator
            if (!isSide_Right) { // left
                frD = Integer.parseInt(fs.d1);
                temp1 = Integer.parseInt(fs.d2);
            } else {
                frD = Integer.parseInt(fs.d2);
                temp1 = Integer.parseInt(fs.d1);
            }

            gcd_temp = gcd(temp1, frD);


            if (gcd_temp > 1) {
                isDenomnAlreadyCut = true;


                //<editor-fold desc=".......... draw new numbers and lines ..........">
                if (!isSide_Right) {

                    // draw cross line over denominator
                    int wD = txt.getTextBound(fs.d1).width();
                    c.drawLine(fs.p1.x - wD / 2f + dx - g, fs.rectF.bottom + dy - g,
                            fs.p1.x + wD / 2f + dx + g, fs.rectF.bottom - fs.maxTxtHeight + dy + g,
                            txt.paint);


                    // draw new denomintor
                    String newDenm = Integer.toString(frD / gcd_temp);
                    tempL.x = fs.p1.x + dx;
                    tempL.y = fs.rectF.bottom + dy + txtFS.Gap + fs.maxTxtHeight;
                    txt.draw(c, newDenm, tempL, 0, 0, 1);

                } else { // right

                    // draw cross line over denominator
                    int wD = txt.getTextBound(fs.d2).width();
                    c.drawLine(fs.p3.x - wD / 2f + dx - g, fs.rectF.bottom + dy - g,
                            fs.p3.x + wD / 2f + dx + g, fs.rectF.bottom - fs.maxTxtHeight + dy + g,
                            txt.paint);


                    // draw new denomintor
                    String newDenm = Integer.toString(frD / gcd_temp);
                    tempL.x = fs.p3.x + dx;
                    tempL.y = fs.rectF.bottom + dy + txtFS.Gap + fs.maxTxtHeight;
                    txt.draw(c, newDenm, tempL, 0, 0, 1);

                }


                // draw cross line over temp1
                int wTemp = txt.getTextBound(Integer.toString(temp1)).width();
                c.drawLine(x - wTemp - g, fs.txtCenterY + dy - g,
                        x + g, fs.txtCenterY - fs.maxTxtHeight + dy + g, txt.paint);


                // draw new temp1
                String newDenm = Integer.toString(temp1 / gcd_temp);
                tempL.x = x - wTemp / 2f;
                tempL.y = fs.tempHandle.y - fs.maxTxtHeight + dy - txtFS.Gap;
                txt.draw(c, newDenm, tempL, 0, 0, 1);

                //</editor-fold>


            }
        }
        //</editor-fold>

        //<editor-fold desc="......... cut fr num & fr denomn ...............">
        if (avf > 0.75f) {

            int frN;
            int frD;
            int gcd_frN;

            String frD_latest;

            // get temp & Fr denominator
            if (!isSide_Right) { // left
                frN = Integer.parseInt(fs.n1);
                frD = Integer.parseInt(fs.d1);

                int gcd_old = gcd(frD, Integer.parseInt(fs.d2));
                frD_latest = Integer.toString(frD / gcd_old);
            } else {
                frN = Integer.parseInt(fs.n2);
                frD = Integer.parseInt(fs.d2);

                int gcd_old = gcd(frD, Integer.parseInt(fs.d1));
                frD_latest = Integer.toString(frD / gcd_old);
            }

            gcd_frN = gcd(frN, Integer.parseInt(frD_latest));

            if (gcd_frN > 1) {

                if (!isSide_Right) {

                    //<editor-fold desc="....... new numerator ............">
                    // draw cross line over numerator
                    int wN = txt.getTextBound(fs.n1).width();
                    c.drawLine(fs.p1.x - wN / 2f + dx - g, fs.rectF.top + fs.maxTxtHeight + dy - g,
                            fs.p1.x + wN / 2f + dx + g, fs.rectF.top + dy + g,
                            txt.paint);

                    // draw new numerator
                    String newNum = Integer.toString(frN / gcd_frN);
                    tempL.x = fs.p1.x + dx;
                    tempL.y = fs.rectF.top + dy - txtFS.Gap;
                    txt.draw(c, newNum, tempL, 0, 0, 1);
                    //</editor-fold>


                    //<editor-fold desc="........ new Denominator .........">

                    if (isDenomnAlreadyCut) { // if denominator is already cut

                        // draw cross line over recent denominator

                        int wD = txt.getTextBound(frD_latest).width();
                        float dy2 = txtFS.Gap + fs.maxTxtHeight;
                        c.drawLine(fs.p1.x - wD / 2f + dx - g, fs.rectF.bottom + dy + dy2 - g,
                                fs.p1.x + wD / 2f + dx + g,
                                fs.rectF.bottom - fs.maxTxtHeight + dy + dy2 + g,
                                txt.paint);

                        // draw new denomintor2
                        String newDenm = Integer.toString(Integer.parseInt(frD_latest) / gcd_frN);
                        tempL.x = fs.p1.x + dx + txt.getTextLength(frD_latest) / 2f + txtFS.Gap;
                        tempL.y = fs.rectF.bottom + dy + txtFS.Gap + 1.5f * fs.maxTxtHeight;
                        txt.draw(c, newDenm, tempL, 0, 0, 0);

                    } else { // denominator is not cut
                        // draw cross line over denominator
                        int wD = txt.getTextBound(fs.d1).width();
                        c.drawLine(fs.p1.x - wD / 2f + dx - g, fs.rectF.bottom + dy - g,
                                fs.p1.x + wD / 2f + dx + g, fs.rectF.bottom - fs.maxTxtHeight + dy + g,
                                txt.paint);


                        // draw new denomintor
                        String newDenm = Integer.toString(frD / gcd_frN);
                        tempL.x = fs.p1.x + dx;
                        tempL.y = fs.rectF.bottom + dy + txtFS.Gap + fs.maxTxtHeight;
                        txt.draw(c, newDenm, tempL, 0, 0, 1);

                    }
                    //</editor-fold>

                } else {

                    //<editor-fold desc="....... new numerator ............">
                    // draw cross line over numerator
                    int wN = txt.getTextBound(fs.n2).width();
                    c.drawLine(fs.p3.x - wN / 2f + dx - g, fs.rectF.top + fs.maxTxtHeight + dy - g,
                            fs.p3.x + wN / 2f + dx + g, fs.rectF.top + dy + g,
                            txt.paint);

                    // draw new numerator
                    String newNum = Integer.toString(frN / gcd_frN);
                    tempL.x = fs.p3.x + dx;
                    tempL.y = fs.rectF.top + dy - txtFS.Gap;
                    txt.draw(c, newNum, tempL, 0, 0, 1);
                    //</editor-fold>


                    //<editor-fold desc="........ new Denominator .........">

                    if (isDenomnAlreadyCut) { // if denominator is already cut

                        // draw cross line over recent denominator

                        int wD = txt.getTextBound(frD_latest).width();
                        float dy2 = txtFS.Gap + fs.maxTxtHeight;
                        c.drawLine(fs.p3.x - wD / 2f + dx - g, fs.rectF.bottom + dy + dy2 - g,
                                fs.p3.x + wD / 2f + dx + g,
                                fs.rectF.bottom - fs.maxTxtHeight + dy + dy2 + g,
                                txt.paint);

                        // draw new denomintor2
                        String newDenm = Integer.toString(Integer.parseInt(frD_latest) / gcd_frN);
                        tempL.x = fs.p3.x + dx + txt.getTextLength(frD_latest) / 2f + txtFS.Gap;
                        tempL.y = fs.rectF.bottom + dy + txtFS.Gap + 1.5f * fs.maxTxtHeight;
                        txt.draw(c, newDenm, tempL, 0, 0, 0);

                    } else { // denominator is not cut
                        // draw cross line over denominator
                        int wD = txt.getTextBound(fs.d2).width();
                        c.drawLine(fs.p3.x - wD / 2f + dx - g, fs.rectF.bottom + dy - g,
                                fs.p3.x + wD / 2f + dx + g, fs.rectF.bottom - fs.maxTxtHeight + dy + g,
                                txt.paint);


                        // draw new denomintor
                        String newDenm = Integer.toString(frD / gcd_frN);
                        tempL.x = fs.p3.x + dx;
                        tempL.y = fs.rectF.bottom + dy + txtFS.Gap + fs.maxTxtHeight;
                        txt.draw(c, newDenm, tempL, 0, 0, 1);

                    }
                    //</editor-fold>


                }
            }

        }
        //</editor-fold>


        if (drawFinalFraction) {

            // draw equal sign
            tempR.x = x;
            tempR.y = fs.txtCenterY + dy;

            // NOTE: equal sign should be same as in replaceWithSimplifiedFr() method
            // distribution --> | 3 spaces | equal sign | 2 spaces |
            String equalSign = "   =  ";
            txt.draw(c, equalSign, tempR, 0, 0, 0);

            x = x + txt.getTextLength(equalSign);

            // tempX
            tempR.x = x;
            // to be used in replace method
            threeStrings.refX = x;

            // tempY
            boolean isDnmIgnored = threeStrings.newD.equals("1");
            tempR.y = isDnmIgnored ? fs.txtCenterY + dy : fs.fDividerY + dy;


            // draw final fractions
            drawSimplifiedFractionFinal(c, tempR);


        }

    }

    private void draw_DrawSumMixedNos(Canvas c) {

        // todo you can use path for round effects

        txt.setColor(Color.RED);

        float topGap = txtFS.Gap;
        float grip = 0.6f * topGap;

        if (!isSide_Right) {


            // vertical left
            c.drawLine(fs.rectF.left, fs.rectF.top + grip,
                    fs.rectF.left, fs.rectF.top - topGap, txt.paint);

            // horizontal top
            c.drawLine(fs.rectF.left, fs.rectF.top - topGap,
                    fs.rectF.left + fs.fDividerL, fs.rectF.top - topGap, txt.paint);

            // vertical right
            c.drawLine(fs.rectF.left + fs.fDividerL, fs.rectF.top + grip,
                    fs.rectF.left + fs.fDividerL, fs.rectF.top - topGap, txt.paint);

            // sum
            // NOTE: sum coordinates are saved in temp

            temp.x = fs.rectF.left + fs.fDividerL / 2f;
            temp.y = fs.rectF.top - topGap - txtFS.Gap;

            txt.draw(c, mixedNumerator, temp, 0, 0, 1);


        } else { // right

            float left = fs.p3.x - fs.fDividerR / 2f;
            float right = fs.p3.x + fs.fDividerR / 2f;

            // vertical left
            c.drawLine(left, fs.rectF.top + grip,
                    left, fs.rectF.top - topGap, txt.paint);

            // horizontal top
            c.drawLine(left, fs.rectF.top - topGap,
                    right, fs.rectF.top - topGap, txt.paint);

            // vertical right
            c.drawLine(right, fs.rectF.top + grip,
                    right, fs.rectF.top - topGap, txt.paint);


            // sum
            // NOTE: sum coordinates are saved in temp
            temp.x = fs.p3.x;
            temp.y = fs.rectF.top - topGap - txtFS.Gap;

            txt.draw(c, mixedNumerator, temp, 0, 0, 1);

        }

        // do not forget to reset color
        txt.resetColor();

    }

    private void draw_ReplaceMixedNumbersWithSum(Canvas c) {

        // DO NOT DISTURB temp AS pt(mixedNumerator or sum ) == temp
        // also see variable used at start of method

        // anim values
        float a1;
        if (fbc) {
            a1 = (animatedFraction < 0.3f) ? 3.333f * animatedFraction : 1;
        } else {
            a1 = (animatedFraction < 0.7f) ? 1.4285f * animatedFraction : 1;
        }


        // use below code when shrinking smoothly
     /*   // for moving sum and hiding mixed num
        float a1 = shrinkEnabled? Math.min(2 * animatedFraction, 1): animatedFraction;
        // for shrinking divider
        float a2 = (animatedFraction > 0.5f) && shrinkEnabled? (animatedFraction - 0.5f) : 0;*/

        PointF pN = (!isSide_Right) ? fs.p1 : fs.p3;


        //<editor-fold desc="......... move down the sum .........">
        tempL.x = pN.x;
        tempL.y = temp.y * (1 - a1) + (fs.rectF.top + fs.maxTxtHeight) * a1;
        txt.draw(c, mixedNumerator, tempL, 0, 0, 1);
        //</editor-fold>

        //<editor-fold desc=".......... get mixed number ............">
        String mixedNum = "Bhai mixed number nhi mila";
        for (MixedNumber mn : mixedNumberList) {

            if (mn.uniqueID == commonInt) { // id is saved in commonInt
                mixedNum = mn.str;
            }
        }
        //</editor-fold>

        //<editor-fold desc="....... hide original mixed number .......">
        txt.setAlpha((int) (255 * (1 - a1)));
        txt.draw(c, mixedNum, pN, 0, 0, 1);
        txt.resetAlpha();

        //</editor-fold>


    }

    //<editor-fold desc=".......... methods which are common bw 2 or more methods  ............">


    // draw final simplified fraction
    private void drawSimplifiedFractionFinal(Canvas c, PointF pt) {
        // if new value is 1 ignore it
        boolean ignoreTemp = threeStrings.newTemp1.equals("1");
        boolean ignoreNum = threeStrings.newN.equals("1");
        boolean ignoreDnm = threeStrings.newD.equals("1");


        if (!ignoreDnm) { // if Denominator isn't ignored then draw fraction


            if (ignoreNum && !ignoreTemp) { // temp not ignored

                txtFS.drawFraction(c, threeStrings.newTemp1, threeStrings.newD, pt, 0);

                threeStrings.requiredGap = txtFS.getFractionDividerLength(
                        threeStrings.newTemp1, threeStrings.newD);

            } else if (!ignoreNum && ignoreTemp) { // num not ignored

                txtFS.drawFraction(c, threeStrings.newN, threeStrings.newD, pt, 0);


                threeStrings.requiredGap = txtFS.getFractionDividerLength(
                        threeStrings.newN, threeStrings.newD);

            } else if ((ignoreNum && ignoreTemp)) { // both ignored

                txtFS.drawFraction(c, "1", threeStrings.newD, pt, 0);

                threeStrings.requiredGap = txtFS.getFractionDividerLength(
                        "1", threeStrings.newD);
            } else { // nothing ignored

                txtFS.drawFraction(c, threeStrings.newN + " × " + threeStrings.newTemp1,
                        threeStrings.newD, pt, 0);

                threeStrings.requiredGap = txtFS.getFractionDividerLength(
                        threeStrings.newN + " × " + threeStrings.newTemp1, threeStrings.newD);
            }


        } else {  // else draw just the number

            if (ignoreNum && !ignoreTemp) { // temp not ignored

                txt.draw(c, threeStrings.newTemp1, pt, 0, 0, 0);

                threeStrings.requiredGap = txt.getTextLength(threeStrings.newTemp1);

            } else if (!ignoreNum && ignoreTemp) { // num not ignored

                txt.draw(c, threeStrings.newN, pt, 0, 0, 0);


                threeStrings.requiredGap = txt.getTextLength(threeStrings.newN);

            } else if ((ignoreNum && ignoreTemp)) { // both ignored

                txt.draw(c, "1", pt, 0, 0, 0);


                threeStrings.requiredGap = txt.getTextLength("1");

            } else { // nothing ignored

                txt.draw(c, threeStrings.newN + " × " + threeStrings.newTemp1, pt, 0, 0, 0);


                threeStrings.requiredGap = txt.getTextLength(
                        threeStrings.newN + " × " + threeStrings.newTemp1);
            }


        }
    }

    // get new fraction values after cancellation/ simplification
    private void getNewValuesAfterSimplification(ThreeStrings ts) {

        int gcd_temp1_d; // d --> denominator
        int gcd_num_d;

        int frD;
        int frN;
        int temp1;

        // get temp & Fr denominator
        if (!isSide_Right) { // left

            frD = Integer.parseInt(fs.d1);
            frN = Integer.parseInt(fs.n1);
            temp1 = Integer.parseInt(fs.d2);


        } else {  // right

            frD = Integer.parseInt(fs.d2);
            frN = Integer.parseInt(fs.n2);
            temp1 = Integer.parseInt(fs.d1);

        }


        gcd_temp1_d = gcd(frD, temp1);

        // new temp
        ts.newTemp1 = Integer.toString(temp1 / gcd_temp1_d);

        int frD2 = frD / gcd_temp1_d;

        gcd_num_d = gcd(frD2, frN);

        // new fraction
        ts.newN = Integer.toString(frN / gcd_num_d);
        ts.newD = Integer.toString(frD2 / gcd_num_d);
    }


    //</editor-fold>


}
