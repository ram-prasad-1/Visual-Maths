package com.visualfiber.apps.visualmaths.fraction.tools;/*
        
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.utils.txt.txt;
import com.visualfiber.apps.visualmaths.utils.txt.txtFS;


public class FractionTool extends ViewGroup {


    private int radius;

    private int N = 1;
    private int D=1;

    private int i; // used in for loop

    private float angle;

    private PointF pt = new PointF();
    private RectF arcRect = new RectF();

    private SeekBar seekBarN;
    private SeekBar seekBarD;

    private SparseIntArray colors;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public FractionTool(Context context) {
        this(context, null);
    }

    public FractionTool(Context context, AttributeSet attrs) {
        super(context, attrs);

        // very important for all custom Views extending ViewGroup
        this.setWillNotDraw(false);


        seekBarD = new SeekBar(context);
        seekBarD.setMax(19);
        seekBarD.setProgress(2);
        addView(seekBarD);

        seekBarN = new SeekBar(context);
        seekBarN.setMax(19);
        seekBarN.setProgress(2);
        addView(seekBarN);

        D = seekBarD.getProgress()+1;
        N = seekBarN.getProgress()+1;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(context, R.color.red));

        setListenerSeekBars();

        colors = new SparseIntArray();

        colors.append(0, R.color.cyan_600);
        colors.append(1, R.color.amber_600);
        colors.append(2, R.color.light_green_800);
        colors.append(3, R.color.orange_900);
        colors.append(4, R.color.pink_800);
        colors.append(5, R.color.brown_500);


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        int space = 20;

        int hh = h - 100 - space;
        int ww = w - 20;


        int min = (ww < hh) ? ww : hh;

        radius = min / 3;

        arcRect.left = w / 2 - radius;
        arcRect.top = w / 2 - radius;

        arcRect.right = w / 2 + radius;
        arcRect.bottom = w / 2 + radius;


        seekBarD.layout(10, h-50-space, w - 10, h-space);
        seekBarN.layout(10, h-100-space, w - 10, h-50-space);


        // point for displaying fraction

        pt.x = arcRect.centerX();
        pt.y = arcRect.bottom + txt.getTextHeight(""+N)+ 50 ;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        angle = 360.0f / D;

        for (i = 1; i <= D; i++) {
            paint.setColor(ContextCompat.getColor(getContext(), colors.get(i%6)));
            if (i > N) {
                paint.setAlpha(25);
            }
            canvas.drawArc(arcRect, -90 + (i - 1) * angle, angle, true, paint);

        }


        txtFS.drawFraction(canvas, ""+N, ""+D, pt, 1);

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


    private void setListenerSeekBars() {

        seekBarD.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                D = progress + 1;

                seekBarN.setMax(progress);
                invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        seekBarN.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                N = progress + 1;
                invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });





    }
}

