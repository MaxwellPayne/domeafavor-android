package edu.indiana.maxandblack.domeafavor.models.users;

import android.location.Location;
import android.util.Log;

import com.facebook.model.GraphUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.indiana.maxandblack.domeafavor.Login.OAuth2AccessToken;

/**
 * Created by Max on 2/20/15.
 */
public class MainUser extends User {
    private static final String TAG = "MainUser";
    private static MainUser ourInstance = new MainUser();
    private static OAuth2AccessToken token;

    public static MainUser getInstance() {
        return ourInstance;
    }

    private MainUser() {
        super(null);
    }

    public static OAuth2AccessToken getToken() {
        return token;
    }

    public void set_id(String _id) {
        super.set_id(_id);
    }

    public void setFirstName(String firstName) {
        super.setFirstName(firstName);
    }

    public void setLastName(String lastName) {
        super.setLastName(lastName);
    }

    public void setFacebookId(String facebookId) {
        super.setFacebookId(facebookId);
    }

    public void setLoc(Location loc) {
        super.setLoc(loc);
    }

    public void setFacebookProfile(GraphUser graphUser) {
        super.setFacebookProfile(graphUser);
    }

    public static void setToken(OAuth2AccessToken token) {
        MainUser.token = token;
    }

    public void loadFromJson(JSONObject json) {
        super.loadFromJson(json);
    }

    public JSONObject getPOSTJson() {
        Map<String, Object> jsonData = new HashMap<String, Object>();
        if (_id != null) { jsonData.put("_id", _id); }
        //if (firstName != null) { jsonData.put("firstName", firstName); }
        //if (lastName != null) { jsonData.put("lastName", lastName); }
        if (facebookId != null) { jsonData.put("facebook_id", facebookId); }
        if (facebookProfile != null) {
            jsonData.put("facebook_profile", facebookProfile.getInnerJSONObject());
        }
        if (token != null) {
            jsonData.put("token", token.toJson());
        }
        if (loc != null) {
            try {
                JSONArray locArray = new JSONArray();
                locArray.put(0, loc.getLatitude());
                locArray.put(1, loc.getLongitude());
                jsonData.put("loc", locArray);
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }

        } else {
            /* @hack - using dummy coordinates right now b/c haven't yet asked for location permissions */
            JSONArray l = new JSONArray();
            try {
                l.put(0, 0.0);
                l.put(1, 0.0);
                jsonData.put("loc", l);
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }
        }

        return new JSONObject(jsonData);
    }
}
