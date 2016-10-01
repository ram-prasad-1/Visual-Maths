package com.visualfiber.apps.visualmaths.base;



/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the BaseMvpView type that wants to be attached with.
 */
public interface Presenter<V extends BaseMvpView> {

    void attachView(V mvpView);

    void detachView();
}