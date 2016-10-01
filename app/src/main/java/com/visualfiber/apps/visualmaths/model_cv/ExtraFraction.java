package com.visualfiber.apps.visualmaths.model_cv;

import android.graphics.PointF;

import com.visualfiber.apps.visualmaths.utils.txt.txt;
import com.visualfiber.apps.visualmaths.utils.txt.txtFS;

public class ExtraFraction {


    // flag for draw or not to draw for entire fraction with sign
    // if false no pre drawing measurements will take place as if fraction did not existed
    public boolean isVisibility_GONE = false;


    // individual flags
    public boolean showSign = true;
    public boolean showFraction = true;


    public boolean isSideRight = true;   // by default it will be added to right


    // every extra fraction must have a unique id number
    public int uniqueID;

    public String n;
    public String d;

    public String sign = ""; // NOTE: Add sign with space in both isSideLeft


    public PointF p1; // numerator coordinate
    public PointF p2;  // denominator coordinate
    public PointF pSign;


    public ExtraFraction(String signWithSpace, String N, String D, int unique_ID_Number) {

        n = N;
        d = D;
        sign = signWithSpace;
        uniqueID = unique_ID_Number;

        p1 = new PointF();
        p2 = new PointF();
        pSign = new PointF();
    }

    // use this if you want to add to left side
    public ExtraFraction(String signWithSpace, String N, String D, int unique_ID_Number, boolean isSide_Right) {

        n = N;
        d = D;
        sign = signWithSpace;
        uniqueID = unique_ID_Number;
        isSideRight = isSide_Right;

        p1 = new PointF();
        p2 = new PointF();
        pSign = new PointF();
    }


    // get required gap to draw this fraction properly
    public float getRequiredGap() {

        return txt.getTextLength(sign) + txtFS.getFractionDividerLength(n, d);
    }

}
