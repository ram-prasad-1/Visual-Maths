package com.visualfiber.apps.visualmaths.model.question;

import com.visualfiber.apps.visualmaths.model.JsonAttributes;

/**
 * Available types of quizzes.
 * Maps {JsonAttributes.QuizType} to subclasses of {@link Question}.
 */
public enum QuestionType {

    FOUR_QUARTER(JsonAttributes.QuizType.FOUR_QUARTER, MultipleChoiceQuestion.class),

    PICKER(JsonAttributes.QuizType.PICKER, PickerQuestion.class);

    private final String mJsonName;
    private final Class<? extends Question> mType;

    QuestionType(final String jsonName, final Class<? extends Question> type) {
        mJsonName = jsonName;
        mType = type;
    }

    public String getJsonName() {
        return mJsonName;
    }

    public Class<? extends Question> getType() {
        return mType;
    }
}
