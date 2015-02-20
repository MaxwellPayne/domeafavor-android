package edu.indiana.maxandblack.domeafavor;

/**
 * Created by Max on 2/19/15.
 */
public class User {

    private String _id;
    private String displayName;

    User(String _id, String displayName) {
        _id = _id;
        displayName = displayName;
    }

    public String get_id() {
        return _id;
    }

    public String getDisplayName() {
        return displayName;
    }
}
