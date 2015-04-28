package edu.indiana.maxandblack.domeafavor.models.chatrooms;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;

import edu.indiana.maxandblack.domeafavor.models.ServerEntity;
import edu.indiana.maxandblack.domeafavor.models.datatypes.MongoDate;
import edu.indiana.maxandblack.domeafavor.models.datatypes.Oid;
import edu.indiana.maxandblack.domeafavor.models.users.MainUser;

/**
 * Created by Max on 4/23/15.
 */
public class Message extends ServerEntity implements Parcelable {

    private static final String TAG = "Message";
    private Oid author;
    private String body;
    private MongoDate createdAt;

    public Message(String text) {
        author = MainUser.getInstance().get_id();
        body = text;
    }

    public Message(JSONObject obj) {
        loadFromJson(obj);
    }

    public Oid getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public MongoDate getCreatedAt() {
        return createdAt;
    }

    public boolean isMine() {
        return author.equals(MainUser.getInstance().get_id());
    }

    @Override
    protected void loadFromJson(JSONObject jsonObject) {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                switch (key) {
                    case "_id":
                        _id = new Oid(jsonObject.getJSONObject(key));
                        break;
                    case "author":
                        /* author only received as an _id, not whole User JSON */
                        author = new Oid(jsonObject.getJSONObject(key));
                        break;
                    case "body":
                        body = jsonObject.getString(key);
                        break;
                    case "created_at":
                        createdAt = new MongoDate(jsonObject.getJSONObject(key));
                        break;
                }
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    @Override
    public JSONObject getPOSTJson() {
        JSONObject json = new JSONObject();
        try {
            if (author == null || body == null) {
                throw new JSONException("author and body both must be not null");
            }

            if (_id != null) {
                json.put("_id", _id.toString());
            }

            if (createdAt != null) {
                json.put("created_at", createdAt.toString());
            }

            json.put("author", author.toString());
            json.put("body", body);

            return json;
        } catch (JSONException e) {
            Log.d(TAG, e.toString());
            return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author.toString());
        dest.writeString(body);
        dest.writeLong(createdAt.getTime());
    }

    public Message(Parcel from) {
        author = new Oid(from.readString());
        body = from.readString();
        createdAt = new MongoDate(from.readLong());
    }
}
