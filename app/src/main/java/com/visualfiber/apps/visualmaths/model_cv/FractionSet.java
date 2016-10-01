package com.visualfiber.apps.visualmaths.model_cv;/*
        
 */

import android.graphics.PointF;
import android.graphics.RectF;

import com.visualfiber.apps.visualmaths.utils.pathanimation.AnimatorPath;
import com.visualfiber.apps.visualmaths.utils.txt.txt;
import com.visualfiber.apps.visualmaths.utils.txt.txtFS;


// see bottom to see how to use this API
public class FractionSet {

    public static final String multiSign = " × ";

    public String equalSign = " = ";

    // for fine tuning; value must be bw 0 and 1
    public float animatedFraction;
    public int moveToCenterNumberIndex = -1; // index n1 =1; d1=2; n2=3; d2=4

    //....NOTE: USE GAPS TO ADD ADDITIONAL TEXTS TO FRACTION SET
    public float gapL = 0;  // gap to left of equal sign
    public float gapR = 0; // gap to right of right fraction

    public float fDividerL;  // divider lengths
    public float fDividerR;

    public float fDividerY;  // y Coordinate of dividers

    // txtCenterY > fDividerY (in mobile)
    // also act as equalSignY
    public float txtCenterY;  // y Coordinate for text to be at center

    public float equalSignLength;

    public float maxTxtHeight; // max height of a number n1 vs n2

    // TO MOVE A NUMBER GET HANDLE TO THESE POINTS
    public PointF p1 = new PointF(); // n1
    public PointF p2 = new PointF(); // d1
    public PointF p3 = new PointF(); // n2
    public PointF p4 = new PointF(); // d2

    public PointF tempHandle; // handle to draw temp string


    public String n1;
    public String d1;
    public String n2;
    public String d2;

    // temporary string holder
    public String temp1 = " × ";

    // Enclosing rect for fraction set
    public RectF rectF = new RectF();

    // various flags
    public boolean showEqualSign = true;

    public boolean showN1 = true;
    public boolean showD1 = true;
    public boolean showN2 = true;
    public boolean showD2 = true;

    public boolean showDividerL = true;
    public boolean showDividerR = true;

    public boolean showTemp1 = false;


    public boolean invertFraction = false;


    public FractionSet(){}

    // copy the values of fraction values only
    public FractionSet(FractionSet fs){
        n1 = fs.n1;
        d1 = fs.d1;
        n2 = fs.n2;
        d2 = fs.d2;
    }


    public FractionSet(String n1, String d1, String n2, String d2) {

        this.n1 = n1;
        this.d1 = d1;

        this.n2 = n2;
        this.d2 = d2;


    }

    // set fractionSet data
    public void setData(String n1, String d1, String n2, String d2) {

        this.n1 = n1;
        this.d1 = d1;

        this.n2 = n2;
        this.d2 = d2;

    }

    // hide/show right fraction
    public void showFractionRight(boolean setShow){

        showDividerR = setShow;
        showN2 = setShow;
        showD2 = setShow;


    }

    // hide/show left fraction
    public void showFractionLeft(boolean setShow){

        showDividerL = setShow;
        showN1 = setShow;
        showD1 = setShow;


    }

    // fill point pt with desired coordinates
    public void fillBottomPoint(PointF pt, int distanceFromRectF_Bottom){

        pt.x = rectF.left;
        pt.y = rectF.bottom + distanceFromRectF_Bottom;

    }




    // for number movement anim
    public void hideNumber(int N1_1__N2_3) {

        switch (N1_1__N2_3) {

            case 1: // n1
                showN1 = false;
                temp1 = multiSign + n1;
                tempHandle = p1;
                break;

            case 2: // d1
                showD1 = false;
                temp1 = multiSign + d1;
                tempHandle = p2;
                break;

            case 3: // n2
                showN2 = false;
                temp1 = multiSign + n2;
                tempHandle = p3;
                break;

            case 4: // d2
                showD2 = false;
                temp1 = multiSign + d2;
                tempHandle = p4;
                break;

            default:
                break;

        }
    }


    // update fraction after inversion
    // MUST CALL Method
    public void updateAfterInvert() {
        this.invertFraction = false;
        this.showD1 = true;
        this.showD2 = true;
        this.showN1 = true;
        this.showN2 = true;


        // During fraction invert process, p1, p2 etc will remain at their same place
        // but d1 will come on top and n1 will come to bottom
        // so reset these string references to update fractionSet correctly

        //<editor-fold desc="       n1 <---> d1">
        String tempNo;

        tempNo = this.d1;

        this.d1 = this.n1;

        this.n1 = tempNo;
        //</editor-fold>

        //<editor-fold desc="       n2 <---> d2">
        tempNo = this.d2;
        this.d2 = this.n2;
        this.n2 = tempNo;
        //</editor-fold>



    }

    // getAnimator Paths to move a number along this path
    public AnimatorPath getPathD2toLeft() {

        AnimatorPath path = new AnimatorPath();
        path.moveTo(p3.x, rectF.bottom);

        PointF throughPt = new PointF(p4.x + 20, rectF.bottom + 60);
        PointF controlPt2nd = new PointF(p4.x, rectF.bottom + 200);
        PointF endPt = new PointF(rectF.left + fDividerL + txt.getTextLength(multiSign + d2) / 2f,
                txtCenterY - txtFS.Gap+2); // bug alert


        path.curveThrough(throughPt, 0.3f, controlPt2nd, endPt);


        return path;
    }
    public AnimatorPath getPathD1toRight() {

        AnimatorPath path = new AnimatorPath();
        path.moveTo(p1.x, rectF.bottom);

        PointF throughPt = new PointF(rectF.right , rectF.bottom + 100);
        PointF controlPt2nd = new PointF(rectF.right + 60, rectF.bottom + 200);
        PointF endPt = new PointF(rectF.right + txt.getTextLength(multiSign + d1) / 2f,
                fDividerY + maxTxtHeight / 2f );

        path.curveThrough(throughPt, 0.5f, controlPt2nd, endPt);

       /* Log.d("TAG", "getPathD1toRight: "+ p2.toString() + "\n"+
                                         throughPt.toString() + "\n"+
                controlPt2nd.toString() + "\n"+
        endPt.toString());*/

        return path;
    }


}


/*

       HOW TO USE Fraction Set API

        Note: Measure FractionSet atleast once before executing any of the operation below
                      call  txt.measure(p1, fs);

        1. Invert Fraction

           - onAnimationStart:
                // set invert fraction to true before
                fs.invertFraction = true;

           - onAnimationEnd:
                // MUST CALL METHOD after fraction is inverted
                fs.updateAfterInvert();

           - onDraw
               fs.animatedFraction = AnimatedValueFromUpdateListener;



       2. Move D1 to right

           - Animation Object
                // Create Animator path here as
                // we want to use latest coordinates of fraction
                // NOTE FOR PATH POINTS OF fs Use fs.getPathD1toRight()
                pathEvaluator2a = new PathEvaluator2();
                animObject = ValueAnimator.ofObject(pathEvaluator2a, fs.getPathD1toRight().getPoints().toArray());


           - onAnimationStart: (i.e. ValueAnim.OfObject)
                  // hide D1 & instead display Temp1 there
                // ( Temp1 will add multiply sign to the number )
                // Hide dividerL & move N1 to center
                fs.showDividerL = false;
                fs.moveToCenterNumberIndex = 1;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        fs.hideNumber(2);
                        fs.showTemp1 = true;

                    }
                }, 500);


            - onDraw
               fs.animatedFraction = AnimatedFractionFromUpdateListener;
               // avPathPt is PathPoint object received from updateListener
                if (avPathPt != null) {

                fs.p2.x = avPathPt.mX;
                fs.p2.y = avPathPt.mY;
                }


       3. Increase GapL:

           - onDraw
            // As gap is changing we need to call txt.measure() on each frame
            // to find latest coordinates of fraction numbers
            fs.gapL = (avFloat * txt.getTextLength(FractionSet.multiSign + fs.d2));
            txt.measure(p1, fs);


       4. Move D2 to Left:

             - Animation Object
                // Create Animator path here as
                // we want to use latest coordinates of fraction
                // NOTE FOR PATH POINTS OF fs Use fs.getPathD1toRight()
                pathEvaluator2a = new PathEvaluator2();
                animObject = ValueAnimator.ofObject(pathEvaluator2a, fs.getPathD2toLeft().getPoints().toArray());

            - onAnimationStart:
                  // hide D2 & instead display Temp1 there
                // ( Temp1 will add multiply sign to the number )
                // Hide dividerR & move N2 to center
                fs.showDividerR = false;
                fs.moveToCenterNumberIndex = 3;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                       fs.hideNumber(4);
                       fs.showTemp1 = true;

                    }
                }, 500);


            - onDraw
               fs.animatedFraction = AnimatedFractionFromUpdateListener;
               // avPathPt is PathPoint object received from updateListener
                if (avPathPt != null) {

                fs.p4.x = avPathPt.mX;
                fs.p4.y = avPathPt.mY;
                }


      5. Reverse travel trick on Path Of D1 or D2:
          (like when back button is pressed)
            // No need to create another animator object or Animator path
            // just toggle below flag on pathEvaluator object
            - onAnimationStart:
                ...
                pathEvaluator2.reverseTravel = true;
                ...

           - onAnimationEnd:
                ...
                pathEvaluator2.reverseTravel = false;
                ...



 */