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

package com.visualfiber.apps.visualmaths.model.question;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.visualfiber.apps.visualmaths.helper.ParcelableHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This abstract class provides general structure for quizzes.
 *
 * @see QuestionType
 *  com.visualfiber.apps.visualmaths.widget.quiz.AbsQuizView
 */
public abstract class Question implements Parcelable {

    private static final String TAG = "Quiz";



    private final String mQuestion;
    private final String mQuizType;
    private int mAnswer;

    // for custom View
    private String[] cvData;
    private String[] solution;



    /**
     * Flag indicating whether this quiz has already been solved.
     * It does not give information whether the solution was correct or not.
     */
    private boolean mSolved;

    protected Question(String question, int answer, boolean solved) {
        mQuestion = question;
        mAnswer = answer;
        mQuizType = getType().getJsonName();
        mSolved = solved;
    }

    protected Question(Parcel in) {
        mQuestion = in.readString();
        mQuizType = getType().getJsonName();
        mSolved = ParcelableHelper.readBoolean(in);
    }

    /**
     * @return The {@link QuestionType} that represents this quiz.
     */
    public abstract QuestionType getType();

    /**
     * Implementations need to return a human readable version of the given answer.
     */
    public abstract String getStringAnswer();

    public String getQuestion() {
        return mQuestion;
    }

    public int getAnswer() {
        return mAnswer;
    }

    protected void setAnswer(int answer) {
        mAnswer = answer;
    }

    public boolean isAnswerCorrect(int answer) {
        return mAnswer == answer;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }


    // data for custom View
    public void setCvData(String[] cvData) {
        this.cvData = cvData;
    }

    public String[] getCvData() {
        return cvData;
    }

    public void setSolution(String[] solution) {
        this.solution = solution;
    }

    public String[] getSolution() {
        return solution;
    }

    /**
     * @return The id of this quiz.
     */
    public int getId() {
        return getQuestion().hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableHelper.writeEnumValue(dest, getType());
        dest.writeString(mQuestion);
        ParcelableHelper.writeBoolean(dest, mSolved);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }

        Question question = (Question) o;

        if (mSolved != question.mSolved) {
            return false;
        }

        if (!mQuestion.equals(question.mQuestion)) {
            return false;
        }
        if (!mQuizType.equals(question.mQuizType)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = mQuestion.hashCode();
        result = 31 * result + mQuizType.hashCode();
        result = 31 * result + (mSolved ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return getType() + ": \"" + getQuestion() + "\"";
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @SuppressWarnings("TryWithIdenticalCatches")
        @Override
        public Question createFromParcel(Parcel in) {
            int ordinal = in.readInt();
            QuestionType type = QuestionType.values()[ordinal];
            try {

                // important @ for CV also
                Constructor<? extends Question> constructor = type.getType()
                        .getConstructor(Parcel.class);
                return constructor.newInstance(in);


            } catch (InstantiationException e) {
                performLegacyCatch(e);
            } catch (IllegalAccessException e) {
                performLegacyCatch(e);
            } catch (InvocationTargetException e) {
                performLegacyCatch(e);
            } catch (NoSuchMethodException e) {
                performLegacyCatch(e);
            }
            throw new UnsupportedOperationException("Could not create Quiz");
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    private static void performLegacyCatch(Exception e) {
        Log.e(TAG, "createFromParcel ", e);
    }

}
