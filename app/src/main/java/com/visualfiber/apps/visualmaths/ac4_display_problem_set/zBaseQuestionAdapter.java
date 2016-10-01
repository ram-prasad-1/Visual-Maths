
package com.visualfiber.apps.visualmaths.ac4_display_problem_set;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.visualfiber.apps.visualmaths.model.JsonAttributes;
import com.visualfiber.apps.visualmaths.model.ProblemSet;
import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model.RequestCvPipe;
import com.visualfiber.apps.visualmaths.model.question.MultipleChoiceQuestion;
import com.visualfiber.apps.visualmaths.model.question.PickerQuestion;
import com.visualfiber.apps.visualmaths.model.question.Question;
import com.visualfiber.apps.visualmaths.views.question.AbsQuestionView;
import com.visualfiber.apps.visualmaths.views.question.MCQView;
import com.visualfiber.apps.visualmaths.views.question.PickerQuestionView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adapter to display quizzes.
 */
public abstract class zBaseQuestionAdapter extends BaseAdapter {

    protected final Context context;
    private final List<Question> questions;
    private final ProblemSet problemSet;

    // INTERFACE COMMUNICATION INTERMEDIATE LINK
    private RequestActivityPipe activityHandle;

    private AbsQuestionView currentQuestionView;

    // adapter compulsary
    private final int mViewTypeCount;
    private List<String> mQuizTypes;


    public zBaseQuestionAdapter(Context context, ProblemSet problemSet) {
        this.context = context;
        this.problemSet = problemSet;
        questions = problemSet.getQuizzes();
        mViewTypeCount = calculateViewTypeCount();
    }

    // Call this from activity to get handle of this
    public AbsQuestionView getCurrentQnView() {
        return currentQuestionView;
    }

    // MUST USE
    // THIS WILL USE THIS HANDLE TO REGISTER ACTIVITY TO AbsQuestionView
    public void registerActivityHandleToQuestionView(RequestActivityPipe activityHandle) {
        this.activityHandle = activityHandle;
    }

    // get the types of QuizViews used in this category
    // and store them in mQuizTypes
    // types bole to picker, mcq etc
    private int calculateViewTypeCount() {
        Set<String> tmpTypes = new HashSet<>();
        for (int i = 0; i < questions.size(); i++) {
            tmpTypes.add(questions.get(i).getType().getJsonName());
        }
        mQuizTypes = new ArrayList<>(tmpTypes);
        return mQuizTypes.size();
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Question getItem(int position) {
        return questions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return questions.get(position).getId();
    }

    @Override
    public int getViewTypeCount() {
        return mViewTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        return mQuizTypes.indexOf(getItem(position).getType().getJsonName());
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Question question = getItem(position);

        // TODO reuse later
        // reuse older view if possible
       /* if (convertView instanceof AbsQuizView) {
            if (((AbsQuizView) convertView).getQuiz().equals(quiz)) {
                return convertView;
            }
        }         */

        // otherwise create new view
        // AND REGISTER ACTIVITY TO THIS VIEW
        currentQuestionView = getViewInternal(position);
        currentQuestionView.registerActivityToQuestionView(activityHandle);

        return currentQuestionView;
    }

    private AbsQuestionView getViewInternal(int position) {
        // get question for this position
        final Question question = getItem(position);

        if (null == question) {
            throw new IllegalArgumentException("Quiz must not be null");
        }

        // also, get cv for this position, note that qNo = position +1
        RequestCvPipe customView = getCustomViewFromTopic(position + 1);


        if (null == customView) {
            throw new IllegalArgumentException(" CUSTOM VIEW FOR THIS QUESTION IS NULL --> " + question.getQuestion());
        }

        // ADD DATA TO CUSTOM VIEW HERE
        customView.setCvData(question.getCvData());

        // PASS ACTIVITY HANDLE TO CUSTOM VIEW
        customView.setActivityHandle(activityHandle);

        // cast custom view to View Object
        return createViewFor(question, (View) customView);

    }

    // TOPIC filter method
    private RequestCvPipe getCustomViewFromTopic(int questionNo) {

        switch (problemSet.getTopicId()) {

            case JsonAttributes.Topic_IDs.Circle:
                return getCircleCV(problemSet.getProblemSetNo(), questionNo);


            case JsonAttributes.Topic_IDs.Rectangle:
                return getRectangleCV(problemSet.getProblemSetNo(), questionNo);

            case JsonAttributes.Topic_IDs.Triangle:
                return getTriangleCV(problemSet.getProblemSetNo(), questionNo);

            case JsonAttributes.Topic_IDs.Percent:
                return getPercentCV(problemSet.getProblemSetNo(), questionNo);


            default:
                throw new IllegalArgumentException("THIS TOPIC ID IS NOT VALID --> " + problemSet.getTopicId());

        }
    }



    // main view with data
    private AbsQuestionView createViewFor(Question question, View customView) {
        switch (question.getType()) {

            case FOUR_QUARTER:
                return new MCQView(context, customView, (MultipleChoiceQuestion) question);

            case PICKER:
                return new PickerQuestionView(context, customView, (PickerQuestion) question);

        }
        throw new UnsupportedOperationException(
                "Quiz of type " + question.getType() + " can not be displayed.");

    }


    protected abstract RequestCvPipe getCircleCV(int pSetNo, int qNo);

    protected abstract RequestCvPipe getRectangleCV(int pSetNo, int qNo);

    protected abstract RequestCvPipe getTriangleCV(int problemSetNo, int questionNo);

    protected abstract RequestCvPipe getPercentCV(int problemSetNo, int questionNo);


}
