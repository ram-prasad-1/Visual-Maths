package com.visualfiber.apps.visualmaths.model_cv.LS;

import com.visualfiber.apps.visualmaths.utils.ss;
import com.visualfiber.apps.visualmaths.utils.txt.txt;
import com.visualfiber.apps.visualmaths.utils.txt.txtFS;

public class LcmFractionX extends FractionX {

    // note that num and denm variables holds corresponding  extraFraction values, values before LCM


    // = LCM / frNumerator
    public int lcmQuotient;

    // holds values of lcm fraction
    public MX_plus_C lcmFrNum;
    public MX_plus_C lcmFrDenm;

    public LcmFractionX(String fSign, MX_plus_C N, MX_plus_C D, int LcmQuotient, int uniqueID) {

        lcmFrNum = N;
        lcmFrDenm = D;

        this.uniqueID = uniqueID;
        this.lcmQuotient = LcmQuotient;

        num = new MX_plus_C("4", "10");
        denm = new MX_plus_C("4", "10");
        sign = fSign;

    }



    // gap required to draw lcmQuotient fraction
    public float getAdditionalGaprequired(){

        String lcmQ = Integer.toString(lcmQuotient);

        float gap = txtFS.getFractionDividerLength(lcmQ, lcmQ) +
                txt.getTextLength(ss.MultiSign_S);

        return  gap;
    }
}
