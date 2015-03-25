package edu.indiana.maxandblack.domeafavor.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Max on 3/24/15.
 */
public class Oid {
    private String idString;

    public Oid(JSONObject json) throws JSONException {
        try {
            idString = json.getString("$oid");
        } catch (JSONException e) {
            throw e;
        }
    }

    public Oid(String id) {
        idString = id;
    }

    @Override
    public String toString() {
        return idString;
    }
}
