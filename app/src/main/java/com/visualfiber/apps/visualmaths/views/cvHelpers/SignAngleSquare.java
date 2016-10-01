package com.visualfiber.apps.visualmaths.views.cvHelpers;

import android.graphics.Canvas;
import android.graphics.PointF;

import static java.lang.Math.cos;
import static java.lang.Math.sin;



public class SignAngleSquare extends BaseStaticView {

    public static float side = 20f;   // rect side



    // draw square angle sign
    public static void draw(Canvas c, PointF p1, PointF p2, PointF p3){

        path.rewind();
        setupPaint();


        // Directions from p2
        double aSlope1 =  getDirection(p1, p2);
        double aSlope3 = getDirection(p3, p2);

        p.x = p2.x + side*(float) cos(aSlope1);
        p.y = p2.y + side*(float) sin(aSlope1);

        path.moveTo(p.x, p.y);

        p.x = p.x + side*(float) cos(aSlope3);
        p.y = p.y + side*(float) sin(aSlope3);

        path.lineTo(p.x, p.y);

        p.x = p.x - side*(float) cos(aSlope1);
        p.y = p.y - side*(float) sin(aSlope1);

        path.lineTo(p.x, p.y);


        c.drawPath(path,paint);


    }


}
