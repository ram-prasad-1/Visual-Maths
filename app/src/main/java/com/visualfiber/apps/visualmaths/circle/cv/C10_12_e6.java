package com.visualfiber.apps.visualmaths.circle.cv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.visualfiber.apps.visualmaths.views.base_views.BaseViewWithAnimation;
import com.visualfiber.apps.visualmaths.views.cvHelpers.Arrow;



public class C10_12_e6 extends BaseViewWithAnimation {


    final int totalSteps = 3;
    int outerwidth = 60;
    private int k;


    private RectF square;

    private Path arcPath;
    private Path arcPath2;


    public C10_12_e6(Context context) {
        super(context);

        arcPath = new Path();
        arcPath2 = new Path();
        square = new RectF();
    }


    //<editor-fold desc="Drawing">
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        // draw background rect
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        canvas.drawRect(square, paint);

        if ((step == 2)|| (step == 3) ) {

            // draw two half circles left and right
            paint.setColor(Color.GREEN);

            if (step == 2) {
                paint.setAlpha((int) (a1 * 255));
            }

            if (step == 3) {
                paint.setAlpha((int) ((1-a1) * 255));  // fade out circle halfs
            }

            paint.setStyle(Paint.Style.FILL);
            arcPath.setFillType(Path.FillType.EVEN_ODD);
            canvas.drawPath(arcPath2, paint);

            paint.setAlpha(255);

        }


        if ((step == 0) ||(step == 1)|| (step >= 3)) {

            // draw the flowers path
            paint.setColor(Color.GREEN);

            if (step == 1) {
                paint.setAlpha((int) ((1-a1) * 255));  // fade out flowers
            }

            if (step == 3) {
                paint.setAlpha((int) (a1 * 255));  // fade in flowers
            }
            paint.setStyle(Paint.Style.FILL);
            arcPath.setFillType(Path.FillType.EVEN_ODD);
            canvas.drawPath(arcPath, paint);

            paint.setAlpha(255);

        }


        Arrow.draw(canvas, p1, p2, -30, " 10 cm ", 44);
        Arrow.draw(canvas, p2, p3, -33, " 10 cm ", 44);

        // Text paint
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(44);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(" A ", p1.x, p1.y, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(" B ", p2.x, p2.y - 8, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(" D ", p3.x - 5, p3.y + 30, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(" C ", p1.x, p3.y + 30, paint);


//        canvas.drawText("step= "+ step+  "   as=  "+actualStep, p1.x + 400, p1.y + 200, paint);


    }


    @Override
    protected int getSuggestedMinimumWidth() {
        return 480;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 480;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 0);


        // no need for this same as minw so delete after completion
        int minh = getPaddingBottom() + getPaddingTop() + getSuggestedMinimumHeight();
        int h = resolveSizeAndState(minh, heightMeasureSpec, 0);

        setMeasuredDimension(widthMeasureSpec, h);


    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        // Set dimensions for text, custom view here


        // Account for padding and outer text
        float xpad = (float) (getPaddingLeft() + getPaddingRight()) + 2 * outerwidth;
        float ypad = (float) (getPaddingTop() + getPaddingBottom()) + 2 * outerwidth;


        // actual area available for drawing the rect
        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        // Figure out how big we can make the rect.
        float squareSide = Math.min(ww, hh);

        square.left = 0;
        square.top = 0;
        square.right = squareSide;
        square.bottom = squareSide;

        square.offset(outerwidth, outerwidth);
        square.offset(getPaddingLeft(), getPaddingTop());


        setupPath();


    }

    private void setupPath() {


        // ref point
        p1.x = square.left;
        p1.y = square.top;

        k = (int) (square.width() / 10.0f);

        // upper half @ arcPath
        square.left = p1.x;
        square.top = p1.y - 5 * k;
        square.right = p1.x + 10 * k;
        square.bottom = p1.y + 5 * k;
        arcPath.addArc(square, 0, 180);

        // lower half @arcPath
        square.left = p1.x;
        square.top = p1.y + 5 * k;
        square.right = p1.x + 10 * k;
        square.bottom = p1.y + 15 * k;
        arcPath.addArc(square, 180, 180);

        // right half @arcPath2
        square.left = p1.x + 5 * k;
        square.top = p1.y;
        square.right = p1.x + 15 * k;
        square.bottom = p1.y + 10 * k;
        arcPath2.addArc(square, 90, 180);


        // left half @arcPath2
        square.left = p1.x - 5 * k;
        square.top = p1.y;
        square.right = p1.x + 5 * k;
        square.bottom = p1.y + 10 * k;
        arcPath2.addArc(square, 270, 180);

        // intersection paths
        arcPath.op(arcPath2, Path.Op.INTERSECT);


        // Draw Square
        square.top = p1.y;
        square.left = p1.x - 1;
        square.bottom = p1.y + 10 * k;
        square.right = p1.x + 10 * k + 1;


        // Points for arrow
        p1.x = square.left;
        p1.y = square.top;

        p2.x = square.right;
        p2.y = square.top;

        p3.x = square.right;
        p3.y = square.bottom;


    }
    //</editor-fold>



    @Override
    protected void playAnimationInternal() {



            if (step == 2){ anim1.start();}
            else {anim2.start();}

    }

    @Override
    public int getTotalSteps() {
        return totalSteps;
    }

    @Override
    public boolean showBothScrollViewAndCustomView() {
        return false;
    }


}
