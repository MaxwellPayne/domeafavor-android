package edu.indiana.maxandblack.domeafavor.models.datatypes;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Max on 4/7/15.
 */
public class MongoDate extends Date {

    public static final String TAG = "MongoDate";

    public MongoDate(JSONObject jsonObject) {
        super();
        try {
            long utcTimestamp = jsonObject.getLong("$date");
            setTime(utcTimestamp);
        } catch (JSONException e) {
            Log.d(TAG, e.toString());
            throw new RuntimeException("Ill-formatted JSONObject " + jsonObject.toString());
        }
    }

    public MongoDate(long date) {
        super(date);
    }

    public MongoDate(Date oldCopy) {
        super(oldCopy.getTime());
    }
}
