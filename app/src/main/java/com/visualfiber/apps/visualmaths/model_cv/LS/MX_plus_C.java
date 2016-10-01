package com.visualfiber.apps.visualmaths.model_cv.LS;


import com.visualfiber.apps.visualmaths.utils.ss;
import com.visualfiber.apps.visualmaths.utils.txt.txt;

// mx + c
public class MX_plus_C {


    public String x = "x"; // unknown variable

    public String m;
    public String c;

    public boolean isOrderReverse; // mx + c --> c + mx

    public boolean ipm; // is m positive
    public boolean ipc;


    public float xm; // x coordinate of m
    public float xc;

    public float dxm; // displacement in m
    public float dxc;

    public float dxx; // displacement in x from m



    public MX_plus_C(String m_with_sign, String c_with_sign) {

        int mm = Integer.parseInt(m_with_sign);

        ipm = (mm >= 0);
        mm = Math.abs(mm); // get absolute value

        m = Integer.toString(mm);

        //.......................................

        int cc = Integer.parseInt(c_with_sign);

        ipc = (cc >= 0);
        cc = Math.abs(cc); // get absolute value

        c = Integer.toString(cc);

    }

    // also updates ipm
    public void setM(String m_with_sign) {

        int mm = Integer.parseInt(m_with_sign);

        ipm = (mm >= 0);
        mm = Math.abs(mm); // get absolute value

        m = Integer.toString(mm);

    }

    // updates ipc too
    public void setC(String c_with_sign) {

        int cc = Integer.parseInt(c_with_sign);

        ipc = (cc >= 0);
        cc = Math.abs(cc); // get absolute value

        c = Integer.toString(cc);

    }


    // used in add fraction of Equation View
    // set coordinates for mx + c with xi as leftmost x (ie x just before leading sign)
    // NOTE: ADD SPACES ONLY WHEN denominator = 1 AND IT IS NOT FIRST FRACTION ON THIS SIDE
    public float set_x_Values(float xi, boolean addSpacesBeforeLeadingNumber){

        String sign = addSpacesBeforeLeadingNumber? ss.Minus_S: ss.Minus;


        //<editor-fold desc=".......... set xm ............">
        // update xi if m -ve & non zero
        if (!ipm && !m.equals("0")) {

            xi += gtl(sign);
        }


        // assign value to xm
        xm = xi;
        float length = m.equals("0")?0 : gtl(m);

        // assign dxx
        dxx = length;

        // update xi
        xi += length;

        // account for unknown variable also
        if (!m.equals("0")) {
            xi += gtl(x);
        }


        //</editor-fold>


        // update xi if
        // CASE A: m = 0, c != 0 & -ve
        if (!ipc && !c.equals("0") && m.equals("0")) {
            xi += gtl(sign); // conditional space
        }

        // CASE B: m != 0, c != 0
        if (!c.equals("0") && !m.equals("0")) {
            xi += gtl(" + "); // with space when m & c both != 0
        }

        // assign value to xc
        xc = xi;

//        Log.d("MX_plus_C", "set_x_Values: xm --> " + xm + "  xc --> " + xc);

        // reupdate xi
        float lengthC = gtl(c);
        xi += lengthC;

        return xi; // now xi is updated final xi
    }



    // NOTE: USE THIS TO_STRING VALUE IF denominator != 1
    @Override
    public String toString() {

        // NOTE: if negative sign is at left most of mixed number
        // it will be with out any space

        String mixedNo = "No mixed number";

        //<editor-fold desc=".......... m == 0 OR c == 0 ............">
        if (m.equals("0")) { // m == 0

            if (ipc) { // c +ve

                mixedNo = c;

            } else { // c -ve

                mixedNo = ss.Minus + c;

            }
        } else if (c.equals("0")) { // c == 0

            if (ipm) { // m +ve

                mixedNo = m + x;

            } else { // m -ve

                mixedNo = ss.Minus + m + x;

            }

        }
        //</editor-fold>

        else if (isOrderReverse) { // do not remove else if from here

            //<editor-fold desc=".......... order reversed ............">

            if (ipm && ipc) { // both m & c +ve

                mixedNo = c + ss.Plus_S + m + x;

            } else if (ipm && !ipc) { // m +ve,  c -ve

                mixedNo = ss.Minus + c + ss.Plus_S + m + x;

            } else if (!ipm && ipc) {  // m -ve,  c +ve

                mixedNo = c + ss.Minus_S + m + x;


            } else {  // both -ve

                mixedNo = ss.Minus + c + ss.Minus_S + m + x;


            }
            //</editor-fold>


        } else {

            //<editor-fold desc=".......... order is not reversed ............">

            if (ipm && ipc) { // both m & c +ve

                mixedNo = m + x + ss.Plus_S + c;

            } else if (ipm && !ipc) { // m +ve,  c -ve

                mixedNo = m + x + ss.Minus_S + c;

            } else if (!ipm && ipc) {  // m -ve,  c +ve

                mixedNo = ss.Minus + m + x + ss.Plus_S + c;


            } else {  // both -ve

                mixedNo = ss.Minus + m + x + ss.Minus_S + c;


            }
            //</editor-fold>

        }


        return mixedNo;
    }




    //................... private methods ............
    // get Text Length
    private float gtl(String str) {
        return txt.getTextLength(str);
    }

}
