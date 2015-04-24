package edu.indiana.maxandblack.domeafavor.models.users;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.facebook.model.GraphUser;

import org.apache.http.HttpRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.indiana.maxandblack.domeafavor.Login.OAuth2AccessToken;
import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.andrest.AndrestClient;
import edu.indiana.maxandblack.domeafavor.models.datatypes.MongoDate;
import edu.indiana.maxandblack.domeafavor.models.datatypes.Oid;

/**
 * Created by Max on 2/20/15.
 */
public class MainUser extends User {
    private static final String TAG = "MainUser";
    private static final AndrestClient domeafavorClient = new AndrestClient();
    private static MainUser ourInstance = new MainUser();

    private LocationManager locationManager;
    private OAuth2AccessToken token;
    private HashMap<Oid, User> friends = new HashMap<>();

    public static MainUser getInstance() {
        return ourInstance;
    }

    private MainUser() {
        super(null);
    }

    public OAuth2AccessToken getToken() {
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

    public void setEmail(String addr) {
        email = addr;
    }

    public void setBirthday(Date bday) {
        birthday = new MongoDate(bday.getTime());
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

    public void setToken(OAuth2AccessToken t) {
        token = t;
    }

    public void startUpdatingLocation(Context context) {
        final int msInMinute = 1000;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, msInMinute, 0, locationListener);
    }

    public boolean addFriend(User friend, Context context) {
        /* only add if not already a friend */
        if (!friends.containsKey(friend.get_id())) {
            try {
                String endpoint = context.getString(R.string.dmfv_modify_friend_byoid,
                        context.getString(R.string.dmfv_host),
                        get_id().toString());
                JSONArray added = new JSONArray();
                added.put(friend.get_id().toString());
                JSONObject putJson = new JSONObject();
                putJson.put("add", added);
                /* comes back as new user object */
                JSONObject updatedUserJson = domeafavorClient.put(endpoint, putJson);
                /* update by unpacking the updated user */
                loadFromJson(updatedUserJson);
                /* return true for success */
                return true;
            } catch (Exception e) {
                /* failed to PUT new friend */
                return false;
            }
        } else {
            /* already a friend, just say things worked */
            return true;
        }
    }

    public boolean removeFriend(User friend, Context context) {
        /* only remove if already a friend */
        if (friends.containsKey(friend.get_id())) {
            try {
                String endpoint = context.getString(R.string.dmfv_modify_friend_byoid,
                        context.getString(R.string.dmfv_host),
                        get_id().toString());
                JSONArray removed = new JSONArray();
                removed.put(friend.get_id().toString());
                JSONObject putJson = new JSONObject();
                putJson.put("remove", removed);
                JSONObject updatedUserJson = domeafavorClient.put(endpoint, putJson);
                loadFromJson(updatedUserJson);
                return true;
            } catch (Exception e) {
                /* failed to PUT remove friend */
                return false;
            }
        } else {
            /* already "removed" just say things worked */
            return true;
        }
    }

    /* all friends extracted from the HashMap */
    public ArrayList<User> getFriends() {
        return new ArrayList<>(friends.values());
    }

    /* user with given _id */
    public User getFriend(Oid userId) {
        return friends.get(userId);
    }

    @Override
    public void loadFromJson(JSONObject json) {
        super.loadFromJson(json);

        /* download friends if exist */
        if (json.has("friends")) {
            friends.clear();
            try {
                JSONArray friendDataArray = json.getJSONArray("friends");
                for (int i = 0; i < friendDataArray.length(); i++) {
                    User friend = new User(friendDataArray.getJSONObject(i));
                    friends.put(friend.get_id(), friend);
                }
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    @Override
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

        if (email != null) {
            jsonData.put("email", email);
        }

        if (birthday != null) {
            jsonData.put("birthday", birthday);
        }

        if (token != null) {
            jsonData.put("token", token.getPOSTJson());
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

    public void applyAuthorizationHeader(HttpRequest req) {
        // TODO: handle authorization for username:password situations
        req.addHeader("Authorization", this.getToken().toString());
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
            MainUser.getInstance().setLoc(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}

