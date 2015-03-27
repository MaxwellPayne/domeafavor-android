package edu.indiana.maxandblack.domeafavor.models.users;

import edu.indiana.maxandblack.domeafavor.models.Oid;

import android.location.Location;
import android.util.Log;

import com.facebook.model.GraphUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.indiana.maxandblack.domeafavor.Login.OAuth2AccessToken;

/**
 * Created by Max on 2/20/15.
 */
public class MainUser extends User {
    private static final String TAG = "MainUser";
    private static MainUser ourInstance = new MainUser();
    private static OAuth2AccessToken token;
    private static HashMap<String, User> friends = new HashMap<>();

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

    public void setUsername(String uname) {
        super.setUsername(uname);
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

    /* all friends extracted from the HashMap */
    public ArrayList<User> getFriends() {
        return new ArrayList<>(friends.values());
    }

    /* user with given _id */
    public User getFriend(String userId) {
        return friends.get(userId);
    }

    public void loadFromJson(JSONObject json) {
        super.loadFromJson(json);

        /* download friends if exist */
        if (json.has("friends")) {
            friends.clear();
            try {
                JSONArray friendDataArray = json.getJSONArray("friends");
                for (int i = 0; i < friendDataArray.length(); i++) {
                    User friend = new User(friendDataArray.getJSONObject(i));
                    friends.put(friend.get_id().toString(), friend);
                }
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }
        }
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

        if (username != null) {
            jsonData.put("username", username);
        }

        if (token != null) {
            jsonData.put("token", token.toJson());
        }

        /* friends should never be null */
        ArrayList<String> friendIds = new ArrayList<>();
        for (User friend : getFriends()) {
            friendIds.add(friend.get_id().toString());
        }
        jsonData.put("friends", friendIds);

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

    //public void
}
