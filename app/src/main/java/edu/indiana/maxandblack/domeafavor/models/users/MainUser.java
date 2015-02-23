package edu.indiana.maxandblack.domeafavor.models.users;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Max on 2/20/15.
 */
public class MainUser extends User {
    private static final String TAG = "MainUser";
    private static MainUser ourInstance = new MainUser();

    public static MainUser getInstance() {
        return ourInstance;
    }

    private MainUser() {
        super(null);
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

    public void loadFromJson(JSONObject json) {
        super.loadFromJson(json);
    }

    public JSONObject getPOSTJson() {
        Map<String, Object> jsonData = new HashMap<String, Object>();
        if (_id != null) { jsonData.put("_id", _id); }
        if (firstName != null) { jsonData.put("firstName", firstName); }
        if (lastName != null) { jsonData.put("lastName", lastName); }
        if (facebookId != null) { jsonData.put("facebookId", facebookId); }

        return new JSONObject(jsonData);
    }
}
