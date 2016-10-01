

package com.visualfiber.apps.visualmaths.persistance;

import android.provider.BaseColumns;

/**
 * Structure of the category table.
 */
public interface TopicTable {

/*   Table columns --->    | _id | name | topicId | totalPS | scores |                */

    String NAME = "topic";
    String COLUMN_ID = BaseColumns._ID;

    String COLUMN_NAME = "name";   // topic name or title
    String COLUMN_TOPIC_ID = "topicId";
    String COLUMN_TOTAL_PS = "totalPS"; // total problem sets
    String COLUMN_SCORES = "scores";

    String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_NAME,
            COLUMN_TOPIC_ID, COLUMN_TOTAL_PS, COLUMN_SCORES};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_TOPIC_ID + " TEXT NOT NULL, "
            + COLUMN_TOTAL_PS + " INTEGER, "
            + COLUMN_SCORES + " INTEGER);";
}
