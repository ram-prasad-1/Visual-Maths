package com.visualfiber.apps.visualmaths.model;


// Through this interface other classes can request cv to do something
// so all custom views must implement this interface to receive the request

import android.view.ViewGroup;

// in android design its name would be like CvGetDataListener interface
public interface RequestCvPipe {

    void setCvData(String[] cvData);

    // forward or backward button is clicked
    void setForwardButtonClicked(boolean forwardButtonClicked);

    // activity handle through which cv's will
    // be able to enable disable jumper buttons
    void setActivityHandle(RequestActivityPipe activityHandle);

    // use this method to choreograph the animation internally
    void playAnimationForStep(int stepNo);

    int getTotalSteps();



    // hoorray it is automatically implemented by all view classes
    // as they already have this method


    // weather to show only custom view or with scroll view
    // when showing solution
    boolean showBothScrollViewAndCustomView(); // by default false


    // hoorray it is automatically implemented by all view classes
    // as they already have this method
    void setLayoutParams(ViewGroup.LayoutParams lp);

    int getBottom();
    int getTop();


}
