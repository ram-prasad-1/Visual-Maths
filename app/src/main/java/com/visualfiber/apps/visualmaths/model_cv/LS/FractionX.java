package com.visualfiber.apps.visualmaths.model_cv.LS;

import com.visualfiber.apps.visualmaths.utils.ss;

// n(mx+c)/ d(mx+c)
public class FractionX {

    // every fraction must have a unique id number
    public int uniqueID;

    public String sign; // fraction sign

    public MX_plus_C num;
    public MX_plus_C denm;

    public boolean visibility_GONE; // show by default

    public boolean isSideRight = true; // is fraction on right side
    public boolean isFirstOnThisSide = true; // is this first fraction on this side , default true

    public boolean isDenominator_1; // if d == 1 (to draw FrNum at center)


    // start coordinate of fraction
    public float xStart;

    // extra gap is accountable even if fraction is invisible
    public float gapExtra;

    // num c
    public FractionX(String num_c_with_sign, int unique_ID, boolean isSide_Right) {
        uniqueID = unique_ID;
        isSideRight = isSide_Right;

        num = new MX_plus_C("0", num_c_with_sign);
        denm = new MX_plus_C("0", "1");

        isDenominator_1 = true;

        sign = ss.Plus_S;
    }

    // num m & num c
    public FractionX(String num_m, String num_c, int unique_ID, boolean isSide_Right) {
        uniqueID = unique_ID;
        isSideRight = isSide_Right;

        num = new MX_plus_C(num_m, num_c);
        denm = new MX_plus_C("0", "1");

        isDenominator_1 = true;

        sign = ss.Plus_S;

    }

    // num & denm
    public FractionX(String sign, MX_plus_C num, MX_plus_C denm, int unique_ID, boolean isSide_Right) {
        uniqueID = unique_ID;
        isSideRight = isSide_Right;

        this.num = num;
        this.denm = denm;

        isDenominator_1 = denm.m.equals("0") && denm.c.equals("1") && denm.ipc;

        this.sign = sign;
    }


    protected FractionX (){}

    public void copyValues(FractionX copyFrom){

        xStart = copyFrom.xStart;
        gapExtra = copyFrom.gapExtra;
        visibility_GONE = copyFrom.visibility_GONE;
        isSideRight = copyFrom.isSideRight;
        isFirstOnThisSide = copyFrom.isFirstOnThisSide;
        isDenominator_1 = copyFrom.isDenominator_1;

    }

    // d == 1 && (Numerator m or c == 0)
    public boolean isSingle() {

        int m_n = Integer.parseInt(num.m);
        int c_n = Integer.parseInt(num.c);

        return isDenominator_1 && (m_n == 0 || c_n == 0);

    }

}