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

package com.visualfiber.apps.visualmaths.persistance;

import android.provider.BaseColumns;

/**
 * Structure of the quiz table.
 */
public interface QuestionTable {

    /* | _id | topicId | problemSetNo | type | question | options | answer | cvData | solution | score |   */
    /* |  0  |     1   |       2      |   3  |    4     |    5    |    6   |    7   |     8    |    9  |   */


    String NAME = "question";

    String COLUMN_ID = BaseColumns._ID;
    String COLUMN_TOPIC_ID = "topicId";
    String COLUMN_PS_NO = "problemSetNo";

    String COLUMN_TYPE = "type";  // type of question (four-quarter or picker)
    String COLUMN_QUESTION = "question";
    String COLUMN_OPTIONS = "options";
    String COLUMN_ANSWER = "answer";

    String COLUMN_CV_DATA = "cvData"; // custom view data
    String COLUMN_SOLUTION = "solution"; // question solution data

    String COLUMN_SCORE = "score";
    

    String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_TOPIC_ID, COLUMN_PS_NO, COLUMN_TYPE,
            COLUMN_QUESTION,  COLUMN_OPTIONS, COLUMN_ANSWER, COLUMN_CV_DATA,
            COLUMN_SOLUTION, COLUMN_SCORE};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "

            + COLUMN_TOPIC_ID + " REFERENCES "
            + TopicTable.NAME + "(" + TopicTable.COLUMN_TOPIC_ID + "), "

            + COLUMN_PS_NO + " INTEGER NOT NULL, "
            + COLUMN_TYPE + " TEXT NOT NULL, "
            + COLUMN_QUESTION + " TEXT NOT NULL, "
            + COLUMN_OPTIONS + " TEXT, "
            + COLUMN_ANSWER + " TEXT NOT NULL, "
            + COLUMN_CV_DATA + " TEXT, "
            + COLUMN_SOLUTION + " TEXT, "
            + COLUMN_SCORE + " INTEGER "
            + ");";
}