package com.visualfiber.apps.visualmaths.ac4_display_problem_set;

import com.visualfiber.apps.visualmaths.base.BasePresenter;
import com.visualfiber.apps.visualmaths.model.ProblemSet;
import com.visualfiber.apps.visualmaths.model.Topic;
import com.visualfiber.apps.visualmaths.model.question.PickerQuestion;
import com.visualfiber.apps.visualmaths.model.question.Question;
import com.visualfiber.apps.visualmaths.persistance.DB_Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramprasaddeora on 28/05/16.
 */
public class DisplayQuestionPresenter extends BasePresenter<DisplayQuestionMvpView> {



    public DisplayQuestionPresenter() {
    }

    @Override
    public void attachView(DisplayQuestionMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void loadProblemSet(Topic topic, int pSetNo) {
        checkViewAttached();

        List<Question> questionList = DB_Helper.getQuestionList(context,topic, pSetNo);

        ProblemSet ps = new ProblemSet(topic.topicId, questionList, pSetNo);

        getMvpView().showProblemSet(ps);


    }

    // test
    private List<Question> getQuestionsList() {
        List<Question> questions = new ArrayList<>();

        questions.add(new PickerQuestion("Me Question 3", 6, 2, 20, 1, false));



        return questions;

    }


}