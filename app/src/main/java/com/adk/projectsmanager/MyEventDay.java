package com.adk.projectsmanager;

import android.os.Parcel;
import android.os.Parcelable;

import com.applandeo.materialcalendarview.EventDay;

import java.util.Calendar;

/**
 * Created by DeniE46 on 10/3/2017.
 */

public class MyEventDay extends EventDay implements Parcelable {
    private String mNote;
    private String mTitle;

    public MyEventDay(Calendar day, int imageResource, String note, String title) {
        super(day, imageResource);
        mNote = note;
        mTitle = title;
    }

    String getNote(){
        return mNote;
    }


    String getTitle(){
        return mTitle;
    }

    private MyEventDay(Parcel in) {
        super((Calendar) in.readSerializable(), in.readInt());
        mNote = in.readString();
        mTitle = in.readString();
    }

    public static final Creator<MyEventDay> CREATOR = new Creator<MyEventDay>() {
        @Override
        public MyEventDay createFromParcel(Parcel in) {
            return new MyEventDay(in);
        }

        @Override
        public MyEventDay[] newArray(int size) {
            return new MyEventDay[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(getCalendar());
        dest.writeInt(getImageResource());
        dest.writeString(mNote);
        dest.writeString(mTitle);
    }
}
