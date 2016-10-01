

package com.visualfiber.apps.visualmaths.model.question;

import android.os.Parcel;

import java.util.Arrays;

// Multiple Choice Question
public final class MultipleChoiceQuestion extends Question {

    String[] mOptions;



    public MultipleChoiceQuestion(String question, int answer, String[] options, boolean solved) {
        super(question, answer, solved);
        mOptions = options;

    }

    public MultipleChoiceQuestion(Parcel in) {
        super(in);
        String options[] = in.createStringArray();
        setOptions(options);
    }

    public String[] getOptions() {
        return mOptions;
    }

    protected void setOptions(String[] options) {
        mOptions = options;
    }

    @Override
    public QuestionType getType() {
        return QuestionType.FOUR_QUARTER;
    }

    @Override
    public String getStringAnswer() {
        return Integer.toString(getAnswer());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        String[] options = getOptions();
        dest.writeStringArray(options);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultipleChoiceQuestion)) {
            return false;
        }

        MultipleChoiceQuestion quiz = (MultipleChoiceQuestion) o;
        final String question = getQuestion();
        if (!question.equals(quiz.getQuestion())) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (!Arrays.equals(getOptions(), quiz.getOptions())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(getOptions());
        return result;
    }


    public static final Creator<MultipleChoiceQuestion> CREATOR
            = new Creator<MultipleChoiceQuestion>() {
        @Override
        public MultipleChoiceQuestion createFromParcel(Parcel in) {
            return new MultipleChoiceQuestion(in);
        }

        @Override
        public MultipleChoiceQuestion[] newArray(int size) {
            return new MultipleChoiceQuestion[size];
        }
    };

}
