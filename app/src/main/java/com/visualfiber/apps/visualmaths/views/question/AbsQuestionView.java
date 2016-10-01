/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.visualfiber.apps.visualmaths.views.question;

import android.animation.ArgbEvaluator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.helper.ViewUtils;
import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model.question.Question;

/**
 * This is the base class for displaying a {@link Question}.
 * <p>
 * Subclasses need to implement {@link AbsQuestionView#createQuizContentView()}
 * in order to allow solution of a quiz.
 * </p>
 * <p>
 * Also {@link AbsQuestionView#allowAnswer(boolean)} needs to be called with
 * <code>true</code> in order to mark the quiz solved.
 * </p>
 *
 * @param <Q> The type of {@link Question} you want to
 *            display.
 */
public abstract class AbsQuestionView<Q extends Question> extends FrameLayout {


    private static final int ANSWER_HIDE_DELAY = 500;
    private static final int FOREGROUND_COLOR_CHANGE_DELAY = 750;
    private final int mSpacingDouble;
    private final LayoutInflater mLayoutInflater;

    private final Q mQuestion;
    private final Interpolator mLinearOutSlowInInterpolator;
    private final Handler mHandler;
    private final InputMethodManager mInputMethodManager;
    private boolean mAnswered;
    private TextView mQuestionView;

    private Runnable mHideFabRunnable;
    private Runnable mMoveOffScreenRunnable;


    // CustomView
    private View customView;



    /**
     * Enables creation of views for quizzes.
     *
     * @param context    The context for this view.
     * @param customView The {Category} this view is running in.
     * @param question       The actual {@link Question} that is going to be displayed.
     */
    public AbsQuestionView(Context context, View customView, Q question) {
        super(context);
        mQuestion = question;


        this.customView = customView;

        mSpacingDouble = getResources().getDimensionPixelSize(R.dimen.spacing_double);
        mLayoutInflater = LayoutInflater.from(context);

        mLinearOutSlowInInterpolator = new LinearOutSlowInInterpolator();
        mHandler = new Handler();
        mInputMethodManager = (InputMethodManager) context.getSystemService
                (Context.INPUT_METHOD_SERVICE);

        setId(question.getId());

        setUpQuestionView();
        LinearLayout container = createContainerLayout(context);

        View quizOptionsView = getInitializedOptionsView();
        addContentView(container, quizOptionsView);
    }

    /**
     * Sets the behaviour for all question views.
     */
    private void setUpQuestionView() {
        mQuestionView = (TextView) mLayoutInflater.inflate(R.layout.question, this, false);

        mQuestionView.setText(getQuiz().getQuestion());
    }

    private LinearLayout createContainerLayout(Context context) {
        LinearLayout container = new LinearLayout(context);
        container.setId(R.id.qn_view_container_linear);
        container.setOrientation(LinearLayout.VERTICAL);

        //Animate Layout changes with default animation
        LayoutTransition lt = new LayoutTransition();
        container.setLayoutTransition(lt);
        return container;
    }

    private View getInitializedOptionsView() {
        View quizOptionsView = createQuizContentView();
        quizOptionsView.setId(R.id.options_view);
        quizOptionsView.setSaveEnabled(true);
        setDefaultPadding(quizOptionsView);
        if (quizOptionsView instanceof ViewGroup) {
            ((ViewGroup) quizOptionsView).setClipToPadding(false);
        }
        setMinHeightInternal(quizOptionsView);
        return quizOptionsView;
    }

    private void addContentView(LinearLayout container, View quizOptionsView) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        container.addView(mQuestionView, layoutParams);

        // Set Id to Custom View & Add it below the question
        customView.setId(R.id.custom_view);
        container.addView(customView, layoutParams);

        // set Id to options view and add it at bottom
        container.addView(quizOptionsView, layoutParams);

        // add linearLayout to parent Framelayout (ie this view)
        addView(container, layoutParams);
    }


    private void setDefaultPadding(View view) {
        view.setPadding(mSpacingDouble, mSpacingDouble, mSpacingDouble, mSpacingDouble);
    }

    protected LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    /**
     * Implementations should create the content view for the type of
     * {@link Question} they want to display.
     *
     * @return the created view to solve the quiz.
     */
    protected abstract View createQuizContentView();

    /**
     * Implementations must make sure that the answer provided is evaluated and correctly rated.
     *
     * @return <code>true</code> if the question has been correctly answered, else
     * <code>false</code>.
     */
    protected abstract boolean isAnswerCorrect();

    /**
     * Save the user input to a bundle for orientation changes.
     *
     * @return The bundle containing the user's input.
     */
    public abstract Bundle getUserInput();

    /**
     * Restore the user's input.
     *
     * @param savedInput The input that the user made in a prior instance of this view.
     */
    public abstract void setUserInput(Bundle savedInput);

    public Q getQuiz() {
        return mQuestion;
    }

    protected boolean isAnswered() {
        return mAnswered;
    }

    /**
     * Sets the quiz to answered or unanswered.
     *
     * @param answered <code>true</code> if an answer was selected, else <code>false</code>.
     */
    protected void allowAnswer(final boolean answered) {
        /*if (null != mSubmitAnswer) {
            if (answered) {
                mSubmitAnswer.show();
            } else {
                mSubmitAnswer.hide();
            }
            mAnswered = answered;
        }*/
    }

    /**
     * Sets the quiz to answered if it not already has been answered.
     * Otherwise does nothing.
     */
    protected void allowAnswer() {
        if (!isAnswered()) {
            allowAnswer(true);
        }
    }

    /**
     * Allows children to submit an answer via code.
     */
    protected void submitAnswer() {
        submitAnswer(findViewById(R.id.submitAnswer));
    }

    @SuppressWarnings("UnusedParameters")
    private void submitAnswer(final View v) {
        final boolean answerCorrect = isAnswerCorrect();
        mQuestion.setSolved(true);
        performScoreAnimation(answerCorrect);
    }

    /**
     * Animates the view nicely when the answer has been submitted.
     *
     * @param answerCorrect <code>true</code> if the answer was correct, else <code>false</code>.
     */
    private void performScoreAnimation(final boolean answerCorrect) {
        // Decide which background color to use.
        final int backgroundColor = ContextCompat.getColor(getContext(),
                answerCorrect ? R.color.green : R.color.red);
        resizeView();
        moveViewOffScreen(answerCorrect);
        // Animate the foreground color to match the background color.
        // This overlays all content within the current view.
        animateForegroundColor(backgroundColor);
    }


    private void resizeView() {
        final float widthHeightRatio = (float) getHeight() / (float) getWidth();
        // Animate X and Y scaling separately to allow different start delays.
        // object animators for x and y with different durations and then run them independently
        resizeViewProperty(View.SCALE_X, .5f, 200);
        resizeViewProperty(View.SCALE_Y, .5f / widthHeightRatio, 300);
    }

    private void resizeViewProperty(Property<View, Float> property,
                                    float targetScale, int durationOffset) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, property,
                1f, targetScale);
        animator.setInterpolator(mLinearOutSlowInInterpolator);
        animator.setStartDelay(FOREGROUND_COLOR_CHANGE_DELAY + durationOffset);
        animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mHideFabRunnable != null) {
            mHandler.removeCallbacks(mHideFabRunnable);
        }
        if (mMoveOffScreenRunnable != null) {
            mHandler.removeCallbacks(mMoveOffScreenRunnable);
        }
        super.onDetachedFromWindow();
    }

    private void animateForegroundColor(@ColorInt final int targetColor) {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, ViewUtils.FOREGROUND_COLOR,
                Color.TRANSPARENT, targetColor);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setStartDelay(FOREGROUND_COLOR_CHANGE_DELAY);
        animator.start();
    }

    private void moveViewOffScreen(final boolean answerCorrect) {
        // Move the current view off the screen.
        mMoveOffScreenRunnable = new Runnable() {
            @Override
            public void run() {

               /* mCategory.setScore(getQuiz(), answerCorrect);
                if (getContext() instanceof QuizActivity) {
                    ((QuizActivity) getContext()).proceed();
                }*/
            }
        };
        mHandler.postDelayed(mMoveOffScreenRunnable,
                FOREGROUND_COLOR_CHANGE_DELAY * 2);
    }

    private void setMinHeightInternal(View view) {
        view.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.min_height_question));
    }

    // Interface handle to notify Activity
    protected RequestActivityPipe activityHandle ;

   public void registerActivityToQuestionView(RequestActivityPipe activityHandle){

       this.activityHandle = activityHandle;

   }



}
