package com.visualfiber.apps.visualmaths.views.base_views;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model.RequestCvPipe;


//   2 PointF free
public abstract class BaseViewGroup extends ViewGroup implements RequestCvPipe {


    public PointF p1;
    public PointF p2;

    // Data for View
    protected String[] cvData;

    protected int step;

    protected boolean fbc; // forward Button Clicked
    protected RequestActivityPipe activityHandle;

    // should remain private
    // just a workaround for back click
    private int  actualStep;


    public BaseViewGroup(Context context) {
        super(context);
        // very important for all custom Views extending ViewGroup
        this.setWillNotDraw(false);


        p1 = new PointF();
        p2 = new PointF();


    }


    // default implementation, override again if necessary
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int h = resolveSizeAndState(getSuggestedMinimumHeight(), heightMeasureSpec, 0);

        setMeasuredDimension(widthMeasureSpec, h);


    }

    @Override
    public void setCvData(String[] cvData) {
        this.cvData = cvData;
    }

    @Override
    public void setForwardButtonClicked(boolean forwardButtonClicked) {

        fbc = forwardButtonClicked;

        setForwardButtonClickedInternal(forwardButtonClicked);
    }


    @Override
    public void setActivityHandle(RequestActivityPipe activityHandle) {

        this.activityHandle = activityHandle;

        setActivityHandleInternal(activityHandle);
    }



    private void updateStepInfo(int stepNo) {
        // First update local step with actual step
        step = actualStep;

        // check which button is clicked - forward or backward
        fbc = (stepNo > step);


        // only update local step if forwardButtonClicked
        if (fbc) {
            step = stepNo;
        }


        // record step in actual step
        actualStep = stepNo;
    }

    //<editor-fold desc="........ margin functionality addition .........">

    /**
     * Validates if a set of layout parameters is valid for a child this ViewGroup.
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    /**
     * @return A set of default layout parameters when given a child with no layout parameters.
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * @return A set of layout parameters created from attributes passed in XML.
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * Called when {@link #checkLayoutParams(LayoutParams)} fails.
     *
     * @return A set of valid layout parameters for this ViewGroup that copies appropriate/valid
     * attributes from the supplied, not-so-good-parameters.
     */
    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return generateDefaultLayoutParams();
    }


    // only for null check not compulsory for margin
    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        if (child == null) {
            return;
        }

        super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if (child == null) {
            return;
        }
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }
    //</editor-fold>


    //.............. TextView Helpers ..................

    protected TextView getTextView(String text) {

        TextView tv = (TextView) LayoutInflater.from(getContext())
                .inflate(R.layout.text_view_solution, this, false);

       /* TextView tv = new TextView(getContext());
        MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setTextSize(20);*/


        tv.setText(text);

//        tv.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.amber_600));


        return tv;
    }

    // empty text view
    protected TextView getEmptyTextView() {

        TextView tv = getTextView("   ");
        return tv;
    }


    protected void showTextView(TextView textView, String text) {

        if (textView != null) {

            textView.setText(text);

            addView(textView);
        }

    }

    protected TextView getAndShowTV(String text) {

        TextView textView = getTextView(text);
        addView(textView);
        return textView;
    }


    protected void layoutView(View view, int left, int top, int width, int height) {
        if (view != null) {
            MarginLayoutParams margins = (MarginLayoutParams) view.getLayoutParams();
            final int leftWithMargins = left + margins.leftMargin;
            final int topWithMargins = top + margins.topMargin;

            view.layout(leftWithMargins, topWithMargins,
                    leftWithMargins + width, topWithMargins + height);
        }
    }

    // get Width With Margins
    protected int getW(View child) {

        if (child == null) {
            return 0;
        }

        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getWidth() + lp.leftMargin + lp.rightMargin;
    }

    // get Height With Margins
    protected int getH(View child) {

        if (child == null) {
            return 0;
        }

        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getHeight() + lp.topMargin + lp.bottomMargin;
    }

    // get Measured Width With Margins
    protected int getW_M(View child) {
        if (child == null) {
            return 0;
        }

        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
    }

    // get Measured Height With Margins
    protected int getH_M(View child) {
        if (child == null) {
            return 0;
        }

        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
    }






    //............................. Abstract methods ..............................................


    // THESE BOTH METHODS CAN REMAIN EMPTY (if subclass has no cv with anim)
    // pass activity handle to any internal custom views with animations
    // otherwise do nothing.
    protected abstract void setActivityHandleInternal(RequestActivityPipe activityHandle);
    protected abstract void setForwardButtonClickedInternal(boolean forwardButtonClicked);



}
