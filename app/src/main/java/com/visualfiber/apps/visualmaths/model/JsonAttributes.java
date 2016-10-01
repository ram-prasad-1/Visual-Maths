
package com.visualfiber.apps.visualmaths.model;

public interface JsonAttributes {

    // level 1 - topic
    String TOPIC_ID = "id";  // topic id
    String NAME = "name";
    String SCORES = "scores";
    String TOTALPS = "totalPS";  // total number of problem sets
    String PSARRAY = "psArray";  // problem set


    // level 2 - problem set
    String PSNO = "ps_no";  // problem set no
    String QNARRAY = "qnArray";

    // level 3 - question
    String TYPE = "type";
    String QUESTION = "question";
    String OPTIONS = "options";
    String ANSWER = "answer";
    String CVDATA = "cvData";
    String SOLUTION = "solution";
    String SCORE = "score";



    public interface Topic_IDs {

        String Circle = "circle";

        String Rectangle = "rectangle";

        String Triangle = "triangle";

        String Percent = "percent";


    }

    interface QuizType {

        String FOUR_QUARTER = "mcq"; // multiple choice question
        String PICKER = "picker";
    }



}
