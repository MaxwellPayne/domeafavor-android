package edu.indiana.maxandblack.domeafavor.models.oddjobs;

import android.graphics.Point;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Max on 3/1/15.
 */
public class Oddjob {

    private final String TAG = "Oddjob";
    protected String _id;
    protected String solicitorId;
    protected String title;
    protected String description;
    protected double price;
    protected Location keyLocation;
    // TODO: handle locale-specific times
    protected Date expiry;
    protected String[] authorizedLackeys;

    public Oddjob() {
        super();
    }

    public Oddjob(JSONObject json) {
        super();
        loadFromJson(json);
    }

    public String get_id() {
        return _id;
    }

    public double getPrice() {
        return price;
    }

    public Location getKeyLocation() {
        return keyLocation;
    }

    public String getDescription() {
        return description;
    }

    public String getSolicitorId() {
        return solicitorId;
    }

    public String getTitle() {
        return title;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setSolicitorId(String solicitorId) {
        this.solicitorId = solicitorId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setKeyLocation(Location keyLocation) {
        this.keyLocation = keyLocation;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public void setAuthorizedLackeys(String[] authorizedLackeys) {
        this.authorizedLackeys = authorizedLackeys;
    }

    private void loadFromJson(JSONObject json) {
        Iterator<String> properties = json.keys();
        while (properties.hasNext()) {
            String key = properties.next();
            try {
                if (key.equals("_id")) {
                    /* drill down to get mongodb _id */
                    JSONObject oId = json.getJSONObject(key);
                    _id = oId.getString("$oid");
                } else if (key.equals("solicitor")) {
                    JSONObject obj = json.getJSONObject(key);
                    if (obj.has("$oid")) {
                        solicitorId = obj.getString("$oid");
                    } else {
                        // TODO: solicitor is passed as full JSON, not just an _id string
                    }
                } else if (key.equals("title")) {
                    title = json.getString(key);
                } else if (key.equals("description")) {
                    description = json.getString(key);
                } else if (key.equals("price")) {
                    price = json.getDouble(key);
                } else if (key.equals("key_location")) {
                    JSONArray locArray = json.getJSONArray(key);
                    Location location = new Location("");
                    location.setLatitude(locArray.getDouble(0));
                    location.setLongitude(locArray.getDouble(1));
                    keyLocation = location;
                } else if (key.equals("expiry")) {
                    JSONObject dateObject = json.getJSONObject(key);
                    long utcTimestamp = dateObject.getInt("$date");
                    expiry = new Date(utcTimestamp * 1000);
                } else if (key.equals("authorized_lackeys")) {
                    JSONArray lackeyIdArray = json.getJSONArray(key);
                    String[] lackeyStringArray = new String[lackeyIdArray.length()];
                    for (int i = 0; i < lackeyIdArray.length(); i++) {
                        JSONObject oIdObj = lackeyIdArray.getJSONObject(i);
                        lackeyStringArray[i] = oIdObj.getString("$oid");
                    }
                    authorizedLackeys = lackeyStringArray;
                }
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }
        }
    }
}
