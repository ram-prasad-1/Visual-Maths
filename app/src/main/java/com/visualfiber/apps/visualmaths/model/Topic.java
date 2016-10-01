package com.visualfiber.apps.visualmaths.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  Topic
 */
// todo know benefits of parcelable

public class Topic implements Parcelable {

    public String title;
    public String topicId;

    private int totalProblemSets;


    public Topic(String title, String topicId) {
        this.title = title;
        this.topicId = topicId;
    }

    protected Topic(Parcel in) {

        title = in.readString();
        topicId = in.readString();

        totalProblemSets = in.readInt();
    }


    public int getTotalProblemSets() {
        return totalProblemSets;
    }

    public void setTotalProblemSets(int totalProblemSets) {
        this.totalProblemSets = totalProblemSets;
    }


    @Override
    public String toString() {
        return "Topic{" +
                ", name='" + title  +
                ", id='" + topicId  +
                ", totalProblemSetNo='" + totalProblemSets +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(title);
        dest.writeString(topicId);
        dest.writeInt(totalProblemSets);

    }

    // CREATOR field
    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };
}
