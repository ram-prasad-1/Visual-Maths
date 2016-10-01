

package com.visualfiber.apps.visualmaths.views.question;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.helper.ApiLevelHelper;
import com.visualfiber.apps.visualmaths.model.question.MultipleChoiceQuestion;

// Multiple Choice Question View
@SuppressLint("ViewConstructor")
public class MCQView extends AbsQuestionView<MultipleChoiceQuestion> {

    private static final String KEY_ANSWER = "ANSWER";
    private int mAnswered = -1;
    private GridView mAnswerView;

    public MCQView(Context context, View customView, MultipleChoiceQuestion quiz) {
        super(context, customView, quiz);

    }

    @Override
    protected View createQuizContentView() {
        mAnswerView = new GridView(getContext());
        mAnswerView.setSelector(R.drawable.selector_button);
        mAnswerView.setNumColumns(2);
        mAnswerView.setAdapter(new OptionsQuizAdapter(getQuiz().getOptions(),
                R.layout.item_answer));
        mAnswerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                allowAnswer();
                mAnswered = position;

                // inform activity
                activityHandle.onOptionSelectInQuestionView();
            }
        });
        return mAnswerView;
    }

    @Override
    public Bundle getUserInput() {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ANSWER, mAnswered);
        return bundle;
    }

    @Override
    @SuppressLint("NewApi")
    public void setUserInput(Bundle savedInput) {
        if (savedInput == null) {
            return;
        }
        mAnswered = savedInput.getInt(KEY_ANSWER);
        if (mAnswered != -1) {
            if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.KITKAT) && isLaidOut()) {
                setUpUserInput();
            } else {
                addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top,
                                               int right, int bottom,
                                               int oldLeft, int oldTop,
                                               int oldRight, int oldBottom) {
                        v.removeOnLayoutChangeListener(this);
                        setUpUserInput();
                    }
                });
            }
        }
    }

    private void setUpUserInput() {
        mAnswerView.performItemClick(mAnswerView.getChildAt(mAnswered), mAnswered,
                mAnswerView.getAdapter().getItemId(mAnswered));
        mAnswerView.getChildAt(mAnswered).setSelected(true);
        mAnswerView.setSelection(mAnswered);
    }

    @Override
    protected boolean isAnswerCorrect() {
        return getQuiz().isAnswerCorrect(mAnswered);
    }
}
