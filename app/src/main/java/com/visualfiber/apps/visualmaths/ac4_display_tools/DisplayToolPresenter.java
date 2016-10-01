package com.visualfiber.apps.visualmaths.ac4_display_tools;

import com.visualfiber.apps.visualmaths.base.BasePresenter;
import com.visualfiber.apps.visualmaths.model.Topic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramprasaddeora on 28/05/16.
 */
public class DisplayToolPresenter extends BasePresenter<DisplayToolMvpView> {



    public DisplayToolPresenter() {
    }

    @Override
    public void attachView(DisplayToolMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void loadTool(Topic topic, int toolNo) {
        checkViewAttached();

        getMvpView().showTool(getQuestionsList());



    }

    // test
    private List<String> getQuestionsList() {
        List<String> questions = new ArrayList<>();





        return questions;

    }


}