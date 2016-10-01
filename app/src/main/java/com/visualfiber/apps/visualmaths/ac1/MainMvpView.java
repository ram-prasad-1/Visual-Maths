package com.visualfiber.apps.visualmaths.ac1;



import com.visualfiber.apps.visualmaths.base.BaseMvpView;
import com.visualfiber.apps.visualmaths.model.Topic;

import java.util.List;

/**
 * Created by ramprasaddeora on 28/05/16.
 */
public interface MainMvpView extends BaseMvpView {

    void showTitles(List<Topic> topicTitles);



}