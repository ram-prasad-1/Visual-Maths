package com.visualfiber.apps.visualmaths.ac2;/*
        
 */

import com.visualfiber.apps.visualmaths.base.BasePresenter;

public class ContentTypePresenter extends BasePresenter<ContentTypeMvpView> {

    @Override
    public void attachView(ContentTypeMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void loadCategories() {

        getMvpView().showCategories(getCategories());
    }



    // test
    private String[] getCategories() {

        String[] categories = {"Concepts", "Examples", "Problems", "Special", "Tools"};

                return categories;
    }

}
