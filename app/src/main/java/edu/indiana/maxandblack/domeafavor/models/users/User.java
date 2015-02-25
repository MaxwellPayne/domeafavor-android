package edu.indiana.maxandblack.domeafavor.models.users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

import com.facebook.model.GraphUser;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Max on 2/19/15.
 */
public class User {

    private static final String TAG = "User";
    protected String _id;
    protected String firstName;
    protected String lastName;
    protected String facebookId;
    protected Location loc;
    protected GraphUser facebookProfile;

    public User(JSONObject json) {
        if (json != null) {
            loadFromJson(json);
        }
    }

    @Override
    public String toString() {
        return String.format("<User _id: %s, firstName: %s, lastName: %s>", _id, firstName, lastName);
    }

    public String get_id() {
        return _id;
    }

    protected void set_id(String _id) {
        this._id = _id;
    }

    protected void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    protected void setLastName(String lastName) {
        this.lastName = lastName;
    }

    protected void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    protected void setLoc(Location loc) {
        this.loc = loc;
    }

    protected void setFacebookProfile(GraphUser graphUser) {
        this.facebookProfile = graphUser;
    }

    protected void loadFromJson(JSONObject json) {
        Iterator<String> properties = json.keys();
        while (properties.hasNext()) {
            String key = properties.next();
            try {
                if (key.equals("_id")) {
                    /* drill down to get mongodb _id */
                    JSONObject oId = json.getJSONObject(key);
                    set_id((String) oId.get("$oid"));
                } else if (key.equals("facebook_profile")) {
                    /* drill down into facebook_profile */
                    JSONObject facebookInfo = json.getJSONObject(key);
                    setFirstName(facebookInfo.getString("first_name"));
                    setLastName(facebookInfo.getString("last_name"));
                    setFacebookId(facebookInfo.getString("id"));
                } else if (key.equals("loc")) {
                    JSONArray locArray = json.getJSONArray(key);
                    Location location = new Location("");
                    location.setLatitude(locArray.getDouble(0));
                    location.setLatitude(locArray.getDouble(1));
                    setLoc(location);
                }
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }
        }
    }
}
