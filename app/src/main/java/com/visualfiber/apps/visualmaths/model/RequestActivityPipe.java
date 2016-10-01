package com.visualfiber.apps.visualmaths.model;

// activity needs to implement this if said activity wants to get notified
// when any of the quiz option is clicked

public interface RequestActivityPipe {



    // called when any of the quiz option is clicked
    // called by questions views (MCQ and picker views)
    void onOptionSelectInQuestionView();

    // called from custom views when a animation starts or ends
    void setEnabledJumperButtons(boolean enableButtons);
}
