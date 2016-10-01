package com.visualfiber.apps.visualmaths.ac3_list_tools;

import com.visualfiber.apps.visualmaths.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramprasaddeora on 28/05/16.
 */
public class ToolPresenter extends BasePresenter<ToolMvpView> {



    public ToolPresenter() {

    }

    @Override
    public void attachView(ToolMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void loadToolList() {
        checkViewAttached();


        getMvpView().showToolList(getTools());



    }

    // test
    List<String> tools = new ArrayList<>();

    private List<String> getTools() {


        tools.add("Tool 1");
        tools.add("Tool 2");


        return tools;

    }


}