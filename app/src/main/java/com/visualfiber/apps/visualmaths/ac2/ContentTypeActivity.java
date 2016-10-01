package com.visualfiber.apps.visualmaths.ac2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.ac3_list_problem_set.ProblemListActivity;
import com.visualfiber.apps.visualmaths.ac3_list_tools.ToolListActivity;
import com.visualfiber.apps.visualmaths.model.Topic;

public class ContentTypeActivity extends AppCompatActivity implements ContentTypeMvpView, View.OnClickListener {

    public static final String TAG = "ContentTypeActivity";

    // key for cross activity communications


    public static final String KEY_TOPIC = "topic";

    private Topic topic = null;


    private ContentTypePresenter presenter;

    private TextView title;
    private TextView concepts;
    private TextView examples;
    private TextView problems;
    private TextView special;
    private TextView tools;


    private CardView cvConcepts;
    private CardView cvExamples;
    private CardView cvProblems;
    private CardView cvSpecial;
    private CardView cvTools;


    // start this activity
    public static void start(Context context, Topic topic) {
        Intent intent = new Intent(context, ContentTypeActivity.class);
        intent.putExtra(KEY_TOPIC, topic);
        context.startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_type);


        //<editor-fold desc=".... findViewsById ........">
        title = (TextView) findViewById(R.id.title2);
        concepts = (TextView) findViewById(R.id.concepts);
        examples = (TextView) findViewById(R.id.examples);
        problems = (TextView) findViewById(R.id.problems);
        special = (TextView) findViewById(R.id.special);
        tools = (TextView) findViewById(R.id.tools);


        cvConcepts = (CardView) findViewById(R.id.cvConcepts);
        cvExamples = (CardView) findViewById(R.id.cvExamples);
        cvProblems = (CardView) findViewById(R.id.cvProblems);
        cvSpecial = (CardView) findViewById(R.id.cvSpecial);
        cvTools = (CardView) findViewById(R.id.cvTools);
        //</editor-fold>


        // get Intent
        Intent intent = getIntent();

        topic = intent.getParcelableExtra(KEY_TOPIC);
        title.setText(topic.title);

        // set onClick listeners
        cvConcepts.setOnClickListener(this);
        cvExamples.setOnClickListener(this);
        cvProblems.setOnClickListener(this);
        cvSpecial.setOnClickListener(this);
        cvTools.setOnClickListener(this);


        presenter = new ContentTypePresenter();
        presenter.attachView(this);
        presenter.loadCategories();
    }

    // MVP View method implement
    @Override
    public void showCategories(String[] types) {

        concepts.setText(types[0]);
        examples.setText(types[1]);
        problems.setText(types[2]);
        special.setText(types[3]);
        tools.setText(types[4]);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        Context context = v.getContext();
        switch (id) {

            case R.id.cvConcepts:
                break;

            case R.id.cvExamples:
                break;

            case R.id.cvProblems:
                ProblemListActivity.start(context, topic);
                break;


            case R.id.cvTools:
                ToolListActivity.start(context, topic);
                break;

                default:
                    break;
        }



    }
}
