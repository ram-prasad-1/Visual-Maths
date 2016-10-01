package com.visualfiber.apps.visualmaths.views.cvHelpers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;



// draw arrow in one direction OR
// draw both sided arrow with text in between
public class Arrow  extends BaseStaticView{


    private static int tip_side = 15; // length of side lines of tip of arrow

    public static int alphaText = 255;


    private static double aSlope;

    private static PointF aTip  = new PointF();
    private static PointF aEnd = new PointF();

    private static PointF temp1 ;

    private Arrow(){} // prevent default initiation



    // draw arrow in both direction with text in between
    public static final void draw(Canvas c, PointF pt1, PointF pt2, float d, String text, float textSize) {


        // paint for Text
        paint.setColor(Color.BLACK);
        paint.setAlpha(alphaText);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);


        // for text height
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        float txtLength = paint.measureText(text);

        float txtHeight = bounds.height();

        float totalLength = (float) hypot((pt2.y - pt1.y), (pt2.x - pt1.x));

        float arrowLength = (totalLength - txtLength) / 2.0f;  // length of one arrow



        // Direction of pt2 from pt1
        aSlope = getDirection(pt2, pt1);



        // First arrow
        temp1 = pt1;

        p.x = pt1.x + arrowLength * (float) cos(aSlope);
        p.y = pt1.y + arrowLength * (float) sin(aSlope);



        //  Path for positioning text
        path.rewind();
        path.moveTo(p.x, p.y);
        path.lineTo(pt2.x,pt2.y);



        c.drawTextOnPath(text, path,0f,d + txtHeight /2.0f, paint);



        draw(c, p, temp1 , -d); // NOTE sign of d changed here as slope has changed



        // Second Arrow
        temp1 = pt2;
        p.x = pt1.x + (arrowLength + txtLength) * (float) cos(aSlope);
        p.y = pt1.y + (arrowLength + txtLength) * (float) sin(aSlope);



        draw(c, p, temp1 , d); // NOTE sign of d changed here as slope will be changed







    }



    // draw single arrow
    // d = distance (in perpendicular dirxn) by which the should be displaced. (d can be both +ve and -ve)
    public static void draw(Canvas c,PointF end, PointF tip , float d){

        path.rewind();
        setupPaint();
        aSlope= getDirection(end,tip);


        float stX;  // effect of strokeWidth on x coordinate of tip
        float stY;  // effect of strokeWidth on y coordinate of tip

        stX = (strokeWidth * (float) cos(aSlope)) * (float) cos(aSlope) ;
        stY = (strokeWidth * (float) cos(aSlope)) * (float) sin(aSlope) ;



        aTip.x = tip.x + d* (float) sin(aSlope);
        aTip.y = tip.y - d* (float) cos(aSlope);

        aEnd.x = end.x + d* (float) sin(aSlope);
        aEnd.y = end.y - d* (float) cos(aSlope);






        path.moveTo(aTip.x-stX + tip_side *(float) Math.cos(aSlope - Math.toRadians(30)),
                aTip.y-stY + tip_side *(float) Math.sin(aSlope - Math.toRadians(30)));

        path.lineTo(aTip.x-stX, aTip.y-stY);

        path.lineTo(aTip.x-stX + tip_side *(float) Math.cos(aSlope + Math.toRadians(30)),
                aTip.y-stY + tip_side *(float) Math.sin(aSlope + Math.toRadians(30)));

        path.moveTo(aTip.x-stX, aTip.y-stY);

        path.lineTo(aEnd.x, aEnd.y);


        c.drawPath(path, paint);

    }








    //...........  Animation Helpers  .........

    // for fadeIn-- animatedValue= a1,   for fadeOut-- animatedValue= 1- a1
    public static void drawAndFade(Canvas c,PointF end, PointF tip , float d,float animatedValue){

        Arrow.alpha = (int) (255*animatedValue);
        draw(c,end,tip,d);
        Arrow.alpha= 255;



    }

    public static void drawAndFade(Canvas c, PointF pt1, PointF pt2, float d, String text, float textSize,float animatedValue){

        Arrow.alpha = (int) (255*animatedValue);
        Arrow.alphaText = (int) (255*animatedValue);
        draw(c, pt1, pt2, d, text, textSize);
        Arrow.alpha= 255;
        Arrow.alphaText = 255;


    }


}
