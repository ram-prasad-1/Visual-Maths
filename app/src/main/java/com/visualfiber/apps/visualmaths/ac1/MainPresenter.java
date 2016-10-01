package com.visualfiber.apps.visualmaths.ac1;

import android.app.Activity;

import com.visualfiber.apps.visualmaths.base.BasePresenter;
import com.visualfiber.apps.visualmaths.model.Topic;
import com.visualfiber.apps.visualmaths.persistance.DB_Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramprasaddeora on 28/05/16.
 */
public class MainPresenter extends BasePresenter<MainMvpView> {



    public MainPresenter() {

    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void loadTitles() {
        checkViewAttached();

        List<Topic> topicList = DB_Helper.getTopicList(((Activity) (getMvpView())), true);

        getMvpView().showTitles(topicList);


    }

    // test
    List<Topic> topicTitles = new ArrayList<>();

    private List<Topic> getTopicTitles() {
        topicTitles.add(new Topic("Circle", ""));
        topicTitles.add(new Topic("Triangle", ""));
        topicTitles.add(new Topic("Fraction", ""));

        return topicTitles;

    }


}