package com.visualfiber.apps.visualmaths.ac3_list_tools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.ac2.ContentTypeActivity;
import com.visualfiber.apps.visualmaths.base.BaseListActivity;
import com.visualfiber.apps.visualmaths.model.Topic;

import java.util.List;

public class ToolListActivity extends BaseListActivity implements ToolMvpView{

    public static final String KEY_TOOL_LIST = "tool_list";

    private ToolRvAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private TextView tvTitle;

    private ToolPresenter presenter;


    private Topic topic = null;



    // start this activity
    public static void start (Context context, Topic topic) {
        Intent intent = new Intent(context, ToolListActivity.class );
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

        adapter = new ToolRvAdapter(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        presenter = new ToolPresenter();
        presenter.attachView(this);
        presenter.loadToolList();





    }


    @Override
    public void showToolList(List<String> toolList) {

        adapter.setToolList(topic ,toolList);


    }
}
