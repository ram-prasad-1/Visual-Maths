package com.visualfiber.apps.visualmaths.ac4_display_problem_set;

import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewAnimator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.ac2.ContentTypeActivity;
import com.visualfiber.apps.visualmaths.ac3_list_problem_set.ProblemListActivity;
import com.visualfiber.apps.visualmaths.base.BaseDisplayActivity;
import com.visualfiber.apps.visualmaths.model.ProblemSet;
import com.visualfiber.apps.visualmaths.model.RequestActivityPipe;
import com.visualfiber.apps.visualmaths.model.RequestCvPipe;
import com.visualfiber.apps.visualmaths.model.Topic;
import com.visualfiber.apps.visualmaths.model.question.Question;

public class DisplayQuestionActivity extends BaseDisplayActivity implements DisplayQuestionMvpView, RequestActivityPipe, View.OnClickListener {


    private Topic topic = null;
    private int problemSetNo;

    private int step = 0;

    private DisplayQuestionPresenter presenter;

    private Handler handler;

    // question view manager
    private AdapterViewAnimator questionView;   // AdapterView with Animator
    private QuestionAdapter adapter = null;

    private Question question;

    // custom view handle through RequestCvPipe
    private RequestCvPipe cv;

    private ImageButton buttonNext;
    private ImageButton forward;
    private ImageButton backward;
    private Space space_view;

    private Button solution;
//    private Button check_Answer;

    // scroll View to show solution
    private ScrollView scrollView;

    // direct container of question, cv, options etc
    private LinearLayout container;

    // txtViews container and direct child of scroll view
    private LinearLayout txtContainer;

    private int txtSolutionIndexCurrent;
    private StringBuilder txtBuilder;

    private Toolbar bottomToolbar;


    private AnimatorListenerAdapter jumperButtonListener;


    // start this activity
    public static void start(Context context, Topic topic, int problemSetNo) {
        Intent intent = new Intent(context, DisplayQuestionActivity.class);

        intent.putExtra(ContentTypeActivity.KEY_TOPIC, topic);
        intent.putExtra(ProblemListActivity.KEY_PROBLEM_SET, problemSetNo);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_question_relative);


        // get Intent
        Intent intent = getIntent();
        topic = intent.getParcelableExtra(ContentTypeActivity.KEY_TOPIC);
        problemSetNo = intent.getIntExtra(ProblemListActivity.KEY_PROBLEM_SET, 1);

        // notify presenter
        presenter = new DisplayQuestionPresenter();
        presenter.attachView(this);
        presenter.loadProblemSet(topic, problemSetNo);

        questionView = (AdapterViewAnimator) findViewById(R.id.question_view);
        bottomToolbar = (Toolbar) findViewById(R.id.progress_toolbar);


        space_view = (Space) findViewById(R.id.space);
        buttonNext = (ImageButton) findViewById(R.id.button_next);
        forward = (ImageButton) findViewById(R.id.forward_button);
        backward = (ImageButton) findViewById(R.id.backward_button);


        solution = (Button) findViewById(R.id.button_solution);
        if (solution != null) {
            solution.setOnClickListener(this);
        }
        buttonNext.setOnClickListener(this);
        forward.setOnClickListener(this);
        backward.setOnClickListener(this);

        if (questionView != null) {
            questionView.setAdapter(adapter);
            setupCvOnDisplay();
        }

        //
        txtBuilder = new StringBuilder();

    }


    //.........   ON CLICK METHODS  ..................
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.button_solution:
                onSolutionButtonClick();
                break;


            case R.id.backward_button:
                onBackwardButtonClick();
                break;

            case R.id.forward_button:
                onForwardButtonClick();
                break;

            case R.id.button_next:
                onNextButtonClick();
                break;

        }


    }

    private void onForwardButtonClick() {
        cv.setForwardButtonClicked(true);

        step++;

        cv.playAnimationForStep(step);

        toggleJumperButtons();

        updateSolutionData();
    }

    private void onBackwardButtonClick() {
        cv.setForwardButtonClicked(false);


        cv.playAnimationForStep(step);

        // NOTE: IN BACK CLICK STEP MUST BE UPDATED LATER
        step--;


        toggleJumperButtons();


    }

    private void onNextButtonClick() {

        solution.setVisibility(View.VISIBLE);

        space_view.setVisibility(View.GONE);
        forward.setVisibility(View.GONE);
        backward.setVisibility(View.GONE);

        questionView.showNext();
        setupCvOnDisplay();


        // if it is last item then hide next button
        if (questionView.getDisplayedChild() == questionView.getCount() - 1) {
            buttonNext.setVisibility(View.INVISIBLE);
        }

    }

    private void onSolutionButtonClick() {

        solution.setVisibility(View.GONE);

        space_view.setVisibility(View.VISIBLE);
        forward.setVisibility(View.VISIBLE);
        backward.setVisibility(View.VISIBLE);


        // remove options view
        container.removeView(container.findViewById(R.id.options_view));

        addScrollView();
    }


    //.................................................


    // ........... LOCAL METHODS ...........

    // call this method immediately whenever a new view is displayed
    private void setupCvOnDisplay() {

        // get current question
        question = adapter.getItem(questionView.getDisplayedChild());

        // question view container
        container = (LinearLayout) (questionView.getCurrentView().findViewById(R.id.qn_view_container_linear));

        // get handle to the cv of current view
        cv = (RequestCvPipe) (questionView.getCurrentView().findViewById(R.id.custom_view));


        // reset index
        txtSolutionIndexCurrent = 0;


    }


    // toggle jumperbuttons on forward backward click
    protected void toggleJumperButtons() {

        int totalSteps = cv.getTotalSteps();


        //Backward button
        if (step >= 1) {
            backward.setVisibility(View.VISIBLE);
        }

        if (step < 1) {
            backward.setVisibility(View.INVISIBLE);
        }

        // forward
        if (step >= totalSteps) {
            forward.setVisibility(View.INVISIBLE);
        }

        if (step < totalSteps) {
            forward.setVisibility(View.VISIBLE);
        }


    }

    private void addScrollView() {

        if (cv.showBothScrollViewAndCustomView()) { // ie show scroll view too
            scrollView = new ScrollView(this);

            int width = container.getWidth();
            int height = bottomToolbar.getTop() - cv.getBottom();
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(width, height);
            scrollView.setLayoutParams(lp);

            scrollView.setBackgroundColor(Color.GREEN);
            scrollView.setFillViewport(true);

            container.addView(scrollView);


            // add single child to scroll view
            txtContainer = new LinearLayout(this);
            txtContainer.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);


            txtContainer.setLayoutParams(lp2);

            //Animate Layout changes with default animation
            LayoutTransition lt = new LayoutTransition();
            txtContainer.setLayoutTransition(lt);

            scrollView.addView(txtContainer);

        } else {  // resize custom view


            int width = container.getWidth();
            int height = container.getHeight() - cv.getTop();
            Log.d("DisplayQuestionActivity", "addScrollView: cvtop  " + cv.getTop());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
            cv.setLayoutParams(lp);


        }

    }


    // upate question solution data
    private void updateSolutionData() {

        if (cv.showBothScrollViewAndCustomView()) { // show scroll view too
            // reuse
            txtBuilder.delete(0, txtBuilder.length());


            String[] solution = question.getSolution();


            for (int i = txtSolutionIndexCurrent; i < 150; i++) {

                if (i >= solution.length) {
                    txtSolutionIndexCurrent = i;
                    break;

                }


                // NOTE THE CHECKPOINT SYMBOL
                // currently empty string
                if (TextUtils.isEmpty(solution[i])) {
                    txtSolutionIndexCurrent = i + 1;
                    break;
                }

                txtBuilder.append(solution[i]).append("\n");
            }


            if (!TextUtils.isEmpty(txtBuilder)) {
                TextView textView = new TextView(this);


                FrameLayout.LayoutParams lp3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                // set Margin
                lp3.setMarginStart(50);
                txtContainer.setLayoutParams(lp3);

                textView.setLayoutParams(lp3);


                textView.setTextSize(20);
                textView.setTextColor(Color.BLACK);

                textView.setText(txtBuilder.toString());

                txtContainer.addView(textView);

                // change focus of container to this view
                txtContainer.requestChildFocus(textView, textView);
            }
        }


    }


    //...........................................


    //............... Other Interfaces methods ........

    // will be called from AbsQuestionView
    @Override
    public void onOptionSelectInQuestionView() {


        if (solution.getVisibility() != View.VISIBLE) {
            solution.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void setEnabledJumperButtons(final boolean enableButtons) {


        if (enableButtons) { // enable

            if (handler == null) {
                handler = new Handler();
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    forward.setEnabled(true);
                    backward.setEnabled(true);

                }
            }, 15);


        } else { // disable
            forward.setEnabled(false);
            backward.setEnabled(false);
        }
    }

    // will be called from presenter
    @Override
    public void showProblemSet(ProblemSet problemSet) {

        adapter = new QuestionAdapter(this, problemSet);

        // do not forget to register activity for receving requests
        adapter.registerActivityHandleToQuestionView(this);

    }

    //......................................................
}
