package com.visualfiber.apps.visualmaths.views.cvHelpers;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;



// Draw circular angle Sign
// This class uses a RectF to draw, not a path
public class SignAngleCircular extends BaseStaticView {

    public static boolean isDir21Smaller; // angle comparison

    private static RectF arcRect = new RectF();

    private static double dirxn21;  // in radian
    private static double dirxn23;


    // draw circular angle sign
    // point p2 is vertex of the angle
    public static void draw(Canvas c, PointF p1, PointF p2, PointF p3, int radius) {


        setupPaint();

        arcRect.left = p2.x - radius;
        arcRect.top = p2.y - radius;

        arcRect.right = p2.x + radius;
        arcRect.bottom = p2.y + radius;


        dirxn21 = getDirection(p1, p2);
        dirxn23 = getDirection(p3, p2);



        // convert atan2-theta to clockwise theta from x axis
        // if theta -ve --> make it +ve
        // if theta +ve --> theta = 360 - theta
        if (dirxn21 <= 0) {
            dirxn21 = -1 * dirxn21;
        } else {

            dirxn21 = 2 * Math.PI - dirxn21;

        }


        if (dirxn23 <= 0) {
            dirxn23 = -1 * dirxn23;
        } else {

            dirxn23 = 2 * Math.PI - dirxn23;

        }

        isDir21Smaller = (dirxn21 - dirxn23) < 0;



        // to degrees
        dirxn21 = Math.toDegrees(dirxn21);
        dirxn23 = Math.toDegrees(dirxn23);

        Log.d("TAG", "          ");
        Log.d("TAG", "draw: dir21:  "+ dirxn21 + "   dir23:  "+ dirxn23);
        Log.d("TAG", "          ");



        if (isDir21Smaller) {



            if (dirxn21 >= 180) {

                // sweepAngle = dirxn23 - dirxn21
//                c.drawArc(arcRect, (float) dirxn21, (float) (dirxn23 - dirxn21), false, paint);

                c.drawArc(arcRect, (float) (360 - dirxn21), (float) ( - (dirxn23 - dirxn21)), false, paint);


            } else {

                if (dirxn23 < (dirxn21 + 180)) {

                    // sweepAngle = dirxn23 - dirxn21
//                    c.drawArc(arcRect, (float) (360 - dirxn21), (float) (dirxn21 - dirxn23), false, paint);

                    c.drawArc(arcRect, (float) (360 - dirxn21), (float) ( - (dirxn23 - dirxn21)), false, paint);


//                    Log.d("TAG", "  23 <  21 + 180  ");


                } else {

                    // sweepAngle = ((360.0 - dirxn23) + dirxn21)  && startAngle = dirxn23
                    c.drawArc(arcRect, (float) (360 - dirxn23), (float) ( -((360.0 - dirxn23) + dirxn21)), false, paint);


                }


            }

        } else {

            // same as above with dirxn21 <--> dirxn23 (exchanged)

            if (dirxn23 >= 180) {

                c.drawArc(arcRect, (float) (360 - dirxn23), (float) (-(dirxn21 - dirxn23)), false, paint);


            } else {

                if (dirxn21 < (dirxn23 + 180)) {

                    c.drawArc(arcRect, (float) (360 -dirxn23), (float) (-(dirxn21 - dirxn23)), false, paint);

                } else {

                    c.drawArc(arcRect, (float) (360 - dirxn21), (float) ( -((360.0 - dirxn21)) + dirxn23), false, paint);

                }


            }


        }


    }


}

