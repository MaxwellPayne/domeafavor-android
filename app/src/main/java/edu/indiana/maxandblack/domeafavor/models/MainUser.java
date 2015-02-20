package edu.indiana.maxandblack.domeafavor.models;

import org.json.JSONObject;

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

    public void loadFromJson(JSONObject json) {
        super.loadFromJson(json);
    }
}
