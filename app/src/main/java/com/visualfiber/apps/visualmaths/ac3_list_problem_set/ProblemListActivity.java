package com.visualfiber.apps.visualmaths.ac3_list_problem_set;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.ac2.ContentTypeActivity;
import com.visualfiber.apps.visualmaths.model.Topic;

public class ProblemListActivity extends AppCompatActivity {

    public static final String KEY_PROBLEM_SET = "problem_set";

    private ProblemRvAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private TextView tvTitle;


    private Topic topic = null;



    // start this activity
    public static void start (Context context, Topic topic) {
        Intent intent = new Intent(context, ProblemListActivity.class );
        intent.putExtra(ContentTypeActivity.KEY_TOPIC, topic);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_list);

        // get Intent
        Intent intent = getIntent();
        topic = intent.getParcelableExtra(ContentTypeActivity.KEY_TOPIC);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        tvTitle = (TextView) findViewById(R.id.title);
        if (tvTitle != null) {
            tvTitle.setText(topic.title);
        }

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ProblemRvAdapter(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        // call this method only after adapter is initialized
        adapter.setProblemSetList(topic);




    }
}
