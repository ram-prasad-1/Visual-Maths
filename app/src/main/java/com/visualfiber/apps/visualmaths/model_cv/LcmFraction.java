package com.visualfiber.apps.visualmaths.model_cv;

import android.graphics.PointF;

import com.visualfiber.apps.visualmaths.utils.txt.txtFS;

public class LcmFraction extends ExtraFraction {


    // = LCM / frNumerator
    public int lcmQuotient;

    // holds corresponding extraFraction values
    // values before LCM
    public String oldN;
    public String oldD;


    public static float multiSignLength;

    public LcmFraction(String sign, String N, String D, int LcmQuotient, int uniqueID) {
        super(sign, N, D, uniqueID); // any unique id will do
        this.lcmQuotient = LcmQuotient;


        multiSignLength = txtFS.getTextLength(FractionSet.multiSign);
    }

    // copy values not just pointing
    public void copyRefPts(PointF pSign, PointF pN, PointF pD){

        p1.x = pN.x;
        p1.y = pN.y;

        p2.x = pD.x;
        p2.y = pD.y;

        this.pSign.x = pSign.x;
        this.pSign.y = pSign.y;


    }



    // gap required to draw lcmQuotient fraction
    public float getAdditionalGaprequired(){

        String lcmQ = Integer.toString(lcmQuotient);

        float gap = txtFS.getFractionDividerLength(lcmQ, lcmQ) +
                multiSignLength;


        return  gap;
    }
}
