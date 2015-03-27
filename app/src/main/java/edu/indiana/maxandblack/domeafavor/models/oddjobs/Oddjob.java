package edu.indiana.maxandblack.domeafavor.models.oddjobs;

import edu.indiana.maxandblack.domeafavor.models.Oid;

import android.graphics.Point;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by Max on 3/1/15.
 */
public class Oddjob {

    private final String TAG = "Oddjob";
    protected Oid _id;
    protected Oid solicitorId;
    protected String title;
    protected String description;
    protected double price;
    protected Location keyLocation;
    // TODO: handle locale-specific times
    protected Date expiry;
    protected Oid[] authorizedLackeys;

    public Oddjob() {
        super();
    }

    public Oddjob(JSONObject json) {
        super();
        loadFromJson(json);
    }

    public String get_id() {
        return _id.toString();
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

    public Oid getSolicitorId() {
        return solicitorId;
    }

    public String getTitle() {
        return title;
    }

    public Date getExpiry() {
        return expiry;
    }

    public String[] getAuthorizedLackeyIds() {
        String[] authorizedLackeyIds = new String[authorizedLackeys.length];
        for (int i = 0; i < authorizedLackeys.length; i++) {
            authorizedLackeyIds[i] = authorizedLackeys[i].toString();
        }
        return authorizedLackeyIds;
    }

    public void setSolicitorId(Oid solicitorId) {
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

    public void setAuthorizedLackeys(Oid[] authorizedLackeys) {
        this.authorizedLackeys = authorizedLackeys;
    }

    public JSONObject getPOSTJson() {
        /* serialize location into mongodb's [lat, lon] format */
        final Double[] locArray = {keyLocation.getLatitude(), keyLocation.getLongitude()};



        HashMap<String, Object > jsonMap = new HashMap<String, Object>(){{
            put("solicitor", solicitorId.toString());
            put("title", title);
            put("expiry", expiry);
            put("description", description);
            put("price", price);
            put("key_location", locArray);
            put("authorized_lackeys", getAuthorizedLackeyIds());
        }};
        try {
            Iterator<Map.Entry<String, Object>> jsonMapIterator = jsonMap.entrySet().iterator();
            while (jsonMapIterator.hasNext()) {
                Map.Entry<String, Object> entry = jsonMapIterator.next();
                /* ensure that none of the properties are null */
                assert entry.getValue() != null;
            }
            return new JSONObject(jsonMap);
        } catch (Exception e) {
            return null;
        }

    }

    private void loadFromJson(JSONObject json) {
        Iterator<String> properties = json.keys();
        while (properties.hasNext()) {
            String key = properties.next();
            try {
                if (key.equals("_id")) {
                    /* drill down to get mongodb _id */
                    JSONObject oId = json.getJSONObject(key);
                    _id = new Oid(oId);
                } else if (key.equals("solicitor")) {
                    JSONObject obj = json.getJSONObject(key);
                    if (obj.has("$oid")) {
                        solicitorId = new Oid(obj);
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
                    Oid[] lackeyStringArray = new Oid[lackeyIdArray.length()];
                    for (int i = 0; i < lackeyIdArray.length(); i++) {
                        JSONObject oIdObj = lackeyIdArray.getJSONObject(i);
                        lackeyStringArray[i] = new Oid(oIdObj);
                    }
                    authorizedLackeys = lackeyStringArray;
                }
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }
        }
    }
}
