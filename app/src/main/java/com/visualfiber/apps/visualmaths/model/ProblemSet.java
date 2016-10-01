
package com.visualfiber.apps.visualmaths.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.visualfiber.apps.visualmaths.model.question.Question;

import java.util.ArrayList;
import java.util.List;

// parcel to be used in DisplayQuestion Activity
// code from Category Class of Topeka app
public class ProblemSet implements Parcelable {

    public static final String TAG = "Topic";



    // topic id
    private final String topicId;

    // current instance data to be send to QuestionAdapter
    private int problemSetNo;
    private List<Question> mQuizzes;  // questions in current problem set



    // pass topic instance to adapter
    // to add corresponding views
    public ProblemSet(@NonNull String topicId, @NonNull List<Question> quizzes, int problemSetNo) {
        this.topicId = topicId;
        mQuizzes = quizzes;

        this.problemSetNo = problemSetNo;
    }



    protected ProblemSet(Parcel in) {
        topicId = in.readString();
        mQuizzes = new ArrayList<>();
        in.readTypedList(mQuizzes, Question.CREATOR);
        problemSetNo = in.readInt();
    }



    public String getTopicId() {
        return topicId;
    }


    public int getProblemSetNo() {
        return problemSetNo;
    }


    @NonNull
    public List<Question> getQuizzes() {
        return mQuizzes;
    }









    /**
     * Checks which quiz is the first unsolved within this category.
     *
     * @return The position of the first unsolved quiz.
     */
    public int getFirstUnsolvedQuizPosition() {
        if (mQuizzes == null) {
            return -1;
        }
        for (int i = 0; i < mQuizzes.size(); i++) {
            if (!mQuizzes.get(i).isSolved()) {
                return i;
            }
        }
        return mQuizzes.size();
    }





    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(topicId);
        dest.writeTypedList(getQuizzes());
        dest.writeInt(problemSetNo);
    }


    @Override
    public String toString() {
        return "ProblemSet{" +
                " id='" + topicId + '\'' +
                ", mQuizzes=" + mQuizzes +
                ", problemSetNo='" + problemSetNo +
                '}';
    }



    @Override
    public int describeContents() {
        return 0;
    }

    // CREATOR field
    public static final Creator<ProblemSet> CREATOR = new Creator<ProblemSet>() {
        @Override
        public ProblemSet createFromParcel(Parcel in) {
            return new ProblemSet(in);
        }

        @Override
        public ProblemSet[] newArray(int size) {
            return new ProblemSet[size];
        }
    };
}
