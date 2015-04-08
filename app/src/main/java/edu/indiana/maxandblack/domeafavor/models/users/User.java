package edu.indiana.maxandblack.domeafavor.models.users;

import edu.indiana.maxandblack.domeafavor.models.datatypes.Oid;
import edu.indiana.maxandblack.domeafavor.models.ServerEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

import com.facebook.model.GraphUser;

import java.util.Iterator;

/**
 * Created by Max on 2/19/15.
 */
public class User extends ServerEntity {

    private static final String TAG = "User";
    protected String firstName;
    protected String lastName;
    protected String username;
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

    public Oid get_id() {
        return _id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public GraphUser getFacebookProfile() {
        return facebookProfile;
    }

    public Location getLoc() {
        return loc;
    }

    public String getFacebookId() {
        return facebookId;
    }

    protected void set_id(String _id) {
        this._id = new Oid(_id);
    }

    protected void setUsername(String uname) {
        this.username = uname;
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

    @Override
    public boolean equals(Object o) {
        return o instanceof User && ((User) o).get_id().equals(_id);
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    protected void loadFromJson(JSONObject json) {
        Iterator<String> properties = json.keys();
        while (properties.hasNext()) {
            String key = properties.next();
            try {
                switch (key) {
                    case "_id":
                    /* drill down to get mongodb _id */
                        JSONObject oId = json.getJSONObject(key);
                        set_id((String) oId.get("$oid"));
                        break;
                    case "username":
                        username = json.getString(key);
                        break;
                    case "first_name":
                        setFirstName(json.getString(key));
                        break;
                    case "last_name":
                        setLastName(json.getString(key));
                        break;
                    case "facebook_profile":
                    /* drill down into facebook_profile */
                        JSONObject facebookInfo = json.getJSONObject(key);
                    /* @hack - should externalize first and last name on server side outside of facebook profile */
                        setFirstName(facebookInfo.getString("first_name"));
                        setLastName(facebookInfo.getString("last_name"));
                        setFacebookId(facebookInfo.getString("id"));
                        break;
                    case "loc":
                        JSONArray locArray = json.getJSONArray(key);
                        Location location = new Location("");
                        location.setLatitude(locArray.getDouble(0));
                        location.setLatitude(locArray.getDouble(1));
                        setLoc(location);
                        break;
                }
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    @Override
    public JSONObject getPOSTJson() {
        throw new UnsupportedOperationException("Not implemented for User other than MainUser");
    }
}
