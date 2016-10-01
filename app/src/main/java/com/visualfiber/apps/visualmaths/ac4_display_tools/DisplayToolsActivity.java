package com.visualfiber.apps.visualmaths.ac4_display_tools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.ac2.ContentTypeActivity;
import com.visualfiber.apps.visualmaths.ac3_list_tools.ToolListActivity;
import com.visualfiber.apps.visualmaths.base.BaseDisplayActivity;
import com.visualfiber.apps.visualmaths.fraction.FractionSetViewTest;
import com.visualfiber.apps.visualmaths.model.Topic;

import java.util.List;

public class DisplayToolsActivity extends BaseDisplayActivity implements DisplayToolMvpView {
    private DisplayToolPresenter presenter;

    private Topic topic = null;
    private FrameLayout container;

    private FractionSetViewTest cv;



    // start this activity
    public static void start(Context context, Topic topic, int toolNo) {
        Intent intent = new Intent(context, DisplayToolsActivity.class);

        intent.putExtra(ContentTypeActivity.KEY_TOPIC, topic);
        intent.putExtra(ToolListActivity.KEY_TOOL_LIST, toolNo);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_tools);

        // get Intent
        Intent intent = getIntent();
        topic = intent.getParcelableExtra(ContentTypeActivity.KEY_TOPIC);


        container = (FrameLayout) findViewById(R.id.container);

        cv = new FractionSetViewTest(this);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        cv.setLayoutParams(lp);

        container.addView(cv);

        Log.d("TAG", "onCreate: View Added tool");



        presenter = new DisplayToolPresenter();
        presenter.attachView(this);



    }


    @Override
    public void showTool(List<String> toolTitle) {

    }
}
