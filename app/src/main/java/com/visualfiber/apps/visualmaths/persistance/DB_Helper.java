package com.visualfiber.apps.visualmaths.persistance;/*
        
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.visualfiber.apps.visualmaths.R;
import com.visualfiber.apps.visualmaths.helper.JsonHelper;
import com.visualfiber.apps.visualmaths.model.JsonAttributes;
import com.visualfiber.apps.visualmaths.model.Topic;
import com.visualfiber.apps.visualmaths.model.question.MultipleChoiceQuestion;
import com.visualfiber.apps.visualmaths.model.question.PickerQuestion;
import com.visualfiber.apps.visualmaths.model.question.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DB_Helper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "visual_maths";
    private static final String DB_SUFFIX = ".db";
    private static final int DB_VERSION = 13;

    private static List<Topic> topics;

    private static DB_Helper mInstance;
    private final Resources mResources;

    private DB_Helper(Context context) {
        //prevents external instance creation
        super(context, DB_NAME + DB_SUFFIX, null, DB_VERSION);
        mResources = context.getResources();
    }

    public static DB_Helper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DB_Helper(context.getApplicationContext());
        }
        return mInstance;
    }


    /**
     * Gets all Topics with out any data
     *
     * @param context      The context this is running in.
     * @param fromDatabase <code>true</code> if a data refresh is needed, else <code>false</code>.
     * @return All topics stored in the database.
     */
    public static List<Topic> getTopicList(Context context, boolean fromDatabase) {
        if (topics == null || fromDatabase) {

            Topic currentTopic;
            Cursor cursor = DB_Helper.getTopicCursor(context);

            // create topics if unavailable
            topics = new ArrayList<>(cursor.getCount());
            do {

                // "magic numbers" based on TopicTable#PROJECTION
                final String name = cursor.getString(1);
                final String topicId = cursor.getString(2);
                final int totalPS = cursor.getInt(3);

                currentTopic = new Topic(name, topicId);
                currentTopic.setTotalProblemSets(totalPS);

                topics.add(currentTopic);

            } while (cursor.moveToNext());

        }
        return topics;
    }


    public static List<Question> getQuestionList(Context context, Topic topic, int pSetNo) {

        SQLiteDatabase readableDatabase = getReadableDatabase(context);

        String selection = QuestionTable.COLUMN_TOPIC_ID + " = ? AND " + QuestionTable.COLUMN_PS_NO+ " = ?";

        String[] selectionArgs = {topic.topicId, Integer.toString(pSetNo)};
        Cursor cursor = readableDatabase
                .query(QuestionTable.NAME, QuestionTable.PROJECTION, selection,
                        selectionArgs, null, null, null);
        cursor.moveToFirst();

        return loadQuestionList(cursor);
    }




    /********************** Private getter Methods Start  **************************************/

    /**
     * Gets all categories wrapped in a {@link Cursor} positioned at it's first element.
     * <p>There are <b>no questions</b> within the categories obtained from this cursor</p>
     *
     * @param context The context this is running in.
     * @return All categories stored in the database.
     */
    private static Cursor getTopicCursor(Context context) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        Cursor data = readableDatabase
                .query(TopicTable.NAME, TopicTable.PROJECTION, null, null, null, null, null);
        data.moveToFirst();
        return data;
    }

    private static List<Question> loadQuestionList(Cursor cursor) {
        List<Question> qList = new ArrayList<>(cursor.getCount());

        do {

            // "magic numbers" based on QuestionTable#PROJECTION
            final String type = cursor.getString(3);
            final String question = cursor.getString(4);
            final String options = cursor.getString(5);
            final int answer = cursor.getInt(6);
            final String[] cvData = JsonHelper.jsonArrayToStringArray(cursor.getString(7));
            final String[] solution = JsonHelper.jsonArrayToStringArray(cursor.getString(8));

            switch (type){
                case JsonAttributes.QuizType.FOUR_QUARTER:
                    MultipleChoiceQuestion mcq = createMultiPleChoiceQuiz(question, answer, options, false);
                    mcq.setCvData(cvData);
                    mcq.setSolution(solution);
                    qList.add(mcq) ;
                    break;

                case JsonAttributes.QuizType.PICKER: {
                    PickerQuestion picker = createPickerQuiz(question, answer, options, false);
                    picker.setCvData(cvData);
                    picker.setSolution(solution);
                   qList.add(picker) ;
                    break;

                }

            }

        } while (cursor.moveToNext());

        cursor.close();
        return qList;

    }

    private static PickerQuestion createPickerQuiz(String question, int answer, String options, boolean solved) {

        final int[] optionArray = JsonHelper.jsonArrayToIntArray(options);

        return new PickerQuestion(question, answer, optionArray[0], optionArray[1], optionArray[2], solved);



    }

    private static MultipleChoiceQuestion createMultiPleChoiceQuiz(String question, int answer,
                                                     String options, boolean solved) {
        final String[] optionsArray = JsonHelper.jsonArrayToStringArray(options);

        return new MultipleChoiceQuestion(question, answer, optionsArray, solved);
    }



    private static SQLiteDatabase getReadableDatabase(Context context) {
        return getInstance(context).getReadableDatabase();
    }

    private static SQLiteDatabase getWritableDatabase(Context context) {
        return getInstance(context).getWritableDatabase();
    }

    /**********************
     * Private getter Methods End
     **************************************/


    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("TAG", "onCreate Database");


        /*
         * create the topic table first, as question table has a foreign key
         * constraint on category id
         */
        db.execSQL(TopicTable.CREATE);
        db.execSQL(QuestionTable.CREATE);
        preFillDatabase(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("TAG", "onUpgrade: Database");
        db.execSQL("DROP TABLE IF EXISTS " + TopicTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionTable.NAME);

        onCreate(db);

    }


    private void preFillDatabase(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            try {
                fillTopicsAndQuestionses(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "preFillDatabase", e);
        }
    }

    // category bole to topic here
    private void fillTopicsAndQuestionses(SQLiteDatabase db) throws JSONException, IOException {
        ContentValues values = new ContentValues(); // reduce, reuse
        JSONArray jsonArray = new JSONArray(readTopicsFromResources());

        JSONObject topic;
        for (int i = 0; i < jsonArray.length(); i++) {
            topic = jsonArray.getJSONObject(i);
            final String topicId = topic.getString(JsonAttributes.TOPIC_ID); // topic id
            fillTopics(db, values, topic);

            // get problem set array of that topic
            final JSONArray psArray = topic.getJSONArray(JsonAttributes.PSARRAY);
            fillProblemSetArray(db, values, psArray, topicId);
        }
    }

    private String readTopicsFromResources() throws IOException {
        StringBuilder topicsJson = new StringBuilder();
        InputStream rawTopics = mResources.openRawResource(R.raw.topics);
        BufferedReader reader = new BufferedReader(new InputStreamReader(rawTopics));
        String line;

        while ((line = reader.readLine()) != null) {
            topicsJson.append(line);
        }
        return topicsJson.toString();
    }

    // put Topics to topic table in database
    private void fillTopics(SQLiteDatabase db, ContentValues values, JSONObject topicObject) throws JSONException {
        values.clear();
        values.put(TopicTable.COLUMN_NAME, topicObject.getString(JsonAttributes.NAME));
        values.put(TopicTable.COLUMN_TOPIC_ID, topicObject.getString(JsonAttributes.TOPIC_ID));
        values.put(TopicTable.COLUMN_TOTAL_PS, topicObject.getString(JsonAttributes.TOTALPS));
        values.put(TopicTable.COLUMN_SCORES, topicObject.getString(JsonAttributes.SCORES));
        db.insert(TopicTable.NAME, null, values);
    }


    private void fillProblemSetArray(SQLiteDatabase db, ContentValues values, JSONArray psArray,
                                     String topicId) throws JSONException {
        JSONObject problemSet;
        for (int i = 0; i < psArray.length(); i++) {
            problemSet = psArray.getJSONObject(i);

            final int psNo = problemSet.getInt(JsonAttributes.PSNO);   // current Problem Set No


            // get question Array of current problem set
            final JSONArray qnArray = problemSet.getJSONArray(JsonAttributes.QNARRAY);
            fillQuestionArray(db, values, psNo, qnArray, topicId);
        }
    }


    private void fillQuestionArray(SQLiteDatabase db, ContentValues values, int psNo, JSONArray qnArray, String topicId) throws JSONException {

        JSONObject question;
        for (int i = 0; i < qnArray.length(); i++) {

            // get question object
            question = qnArray.getJSONObject(i);

            values.clear();

            values.put(QuestionTable.COLUMN_TOPIC_ID, topicId);
            values.put(QuestionTable.COLUMN_PS_NO, psNo);

            values.put(QuestionTable.COLUMN_TYPE, question.getString(JsonAttributes.TYPE));
            values.put(QuestionTable.COLUMN_QUESTION, question.getString(JsonAttributes.QUESTION));
            values.put(QuestionTable.COLUMN_OPTIONS, question.getString(JsonAttributes.OPTIONS));
            values.put(QuestionTable.COLUMN_ANSWER, question.getString(JsonAttributes.ANSWER));

            putNonEmptyString(values, question, QuestionTable.COLUMN_CV_DATA, JsonAttributes.CVDATA);
            putNonEmptyString(values, question, QuestionTable.COLUMN_SOLUTION, JsonAttributes.SOLUTION);

            values.put(QuestionTable.COLUMN_SCORE, question.getString(JsonAttributes.SCORE));

            //DON'T FORGET TO PUT CONTENT VALUES TO DATABASE
            db.insert(QuestionTable.NAME, null, values);


        }


    }


    /**
     * NOTE THIS METHOD ONLY PUTS STRING IF IT IS NON NULL
     * Puts a non-empty string to ContentValues provided.
     *
     * @param values     The place where the data should be put.
     * @param jsonObject The quiz potentially containing the data.
     * @param contentKey The key use for placing the data in the database.
     * @param jsonKey    The key to look for.
     */
    private void putNonEmptyString(ContentValues values, JSONObject jsonObject,
                                   String contentKey, String jsonKey) {

        final String stringToPut = jsonObject.optString(jsonKey, null);
        if (!TextUtils.isEmpty(stringToPut)) {
            values.put(contentKey, stringToPut);
        }
    }

}
