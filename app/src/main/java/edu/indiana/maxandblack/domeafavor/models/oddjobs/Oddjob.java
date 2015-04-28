package edu.indiana.maxandblack.domeafavor.models.oddjobs;

import edu.indiana.maxandblack.domeafavor.models.chatrooms.Chatroom;
import edu.indiana.maxandblack.domeafavor.models.datatypes.MongoDate;
import edu.indiana.maxandblack.domeafavor.models.datatypes.Oid;
import edu.indiana.maxandblack.domeafavor.models.ServerEntity;
import edu.indiana.maxandblack.domeafavor.models.payments.Payment;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Max on 3/1/15.
 */
public class Oddjob extends ServerEntity implements Parcelable {

    public enum CompletionState {
        IN_PROGRESS, PENDING_PAYMENT,
        PAYMENT_DENIED, COMPLETED, EXPIRED
    }

    /* key to be used in bundle when an Oddjob instance is the main instance in bundle */
    public static final String MAIN_SERIALIZED_ODDJOB_KEY = "main_serialized_oddjob";

    private static final String TAG = "Oddjob";
    protected Oid solicitorId;
    protected String title;
    protected String description;
    protected double price;
    protected Location keyLocation;
    // TODO: handle locale-specific times
    protected MongoDate expiry;
    protected Payment payment;
    protected MongoDate lackeyMarkedFinished;
    //protected Oid[] authorizedLackeys;
    protected Oid[] bannedLackeys = new Oid[0];
    protected Oid[] applicants = new Oid[0];

    private Chatroom chatroom;

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

    public String[] getApplicants() {
        String[] result = new String[applicants.length];
        for (int i = 0; i < applicants.length; i++) {
            result[i] = applicants[i].toString();
        }
        return result;
    }

    public String[] getBannedLackeys() {
        String[] result = new String[bannedLackeys.length];
        for (int i = 0; i < bannedLackeys.length; i++) {
            result[i] = bannedLackeys[i].toString();
        }
        return result;
    }

    public CompletionState getCompletionState() {
        // TODO: Should rank .isExpired higher than all else b/c lackey might == null
        final Date now = new Date();
        if (payment != null) {
            return CompletionState.COMPLETED;
        } else if (now.getTime() >= expiry.getTime()) {
            /* past expiration date */
            if (lackeyMarkedFinished != null) {
                /* lackey claimed this job is finished before expiry */
                // TODO: un-hardcode expiry interval
                final long MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;
                final Date oneDayPastExpiry = new Date(expiry.getTime() + MILLISECONDS_IN_DAY);

                if (now.getTime() < oneDayPastExpiry.getTime()) {
                    /* still sitting in the payment grace period */
                    return CompletionState.PENDING_PAYMENT;
                } else {
                    /* lackey said finished, but solicitor failed to pay */
                    return CompletionState.PAYMENT_DENIED;
                }
            } else {
                /* lackey never finished and solicitor never paid, it's a wash */
                return CompletionState.EXPIRED;
            }
        } else {
            /* task is still in progress */
            return CompletionState.IN_PROGRESS;
        }
    }

    public Chatroom getChatroom() {
        return chatroom;
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

    public void setExpiry(MongoDate expiry) {
        this.expiry = expiry;
    }

    public void setApplicants(Oid[] applicants) {
        this.applicants = applicants;
    }

    public void setBannedLackeys(Oid[] bannedLackeys) {
        this.bannedLackeys = bannedLackeys;
    }

    public void setChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }

    @Override
    public JSONObject getPOSTJson() {
        /* serialize location into mongodb's [lat, lon] format */
        final Double[] locArray = {keyLocation.getLatitude(), keyLocation.getLongitude()};



        HashMap<String, Object > jsonMap = new HashMap<String, Object>(){{
            put("solicitor", solicitorId.toString());
            put("title", title);
            put("expiry", expiry.toString());
            put("description", description);
            put("price", price);
            put("key_location", locArray);
            put("banned_lackeys", getBannedLackeys());
            put("applicants", getApplicants());
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
                        _id = new Oid(oId);
                        break;
                    case "solicitor":
                        JSONObject obj = json.getJSONObject(key);
                        if (obj.has("$oid")) {
                            solicitorId = new Oid(obj);
                        } else {
                            // TODO: solicitor is passed as full JSON, not just an _id string
                        }
                        break;
                    case "title":
                        title = json.getString(key);
                        break;
                    case "description":
                        description = json.getString(key);
                        break;
                    case "price":
                        price = json.getDouble(key);
                        break;
                    case "key_location":
                        JSONArray locArray = json.getJSONArray(key);
                        Location location = new Location("");
                        location.setLatitude(locArray.getDouble(0));
                        location.setLongitude(locArray.getDouble(1));
                        keyLocation = location;
                        break;
                    case "expiry":
                        expiry = new MongoDate(json.getJSONObject(key));
                        int x = 2;
                        break;
                    case "payment":
                        payment = new Payment(json.getJSONObject(key));
                        break;
                    case "lackey_marked_finished":
                        lackeyMarkedFinished = new MongoDate(json.getJSONObject(key));
                        break;
                    case "banned_lackeys":
                        JSONArray bannedLackeyIdArray = json.getJSONArray(key);
                        Oid[] bannedLackeyArray = new Oid[bannedLackeyIdArray.length()];
                        for (int i = 0; i < bannedLackeyArray.length; i++) {
                            JSONObject oidObj = bannedLackeyIdArray.getJSONObject(i);
                            bannedLackeyArray[i] = new Oid(oidObj);
                        }
                        bannedLackeys = bannedLackeyArray;
                        break;
                    case "applicants":
                         JSONArray applicantJsonArray = json.getJSONArray(key);
                        Oid[] applicantsArray = new Oid[applicantJsonArray.length()];
                        for (int i = 0; i < applicantsArray.length; i++) {
                            JSONObject oidObj = applicantJsonArray.getJSONObject(i);
                            applicantsArray[i] = new Oid(oidObj);
                        }
                        applicants = applicantsArray;
                        break;

                }
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Oddjob> CREATOR =
            new Creator<Oddjob>() {
                @Override
                public Oddjob createFromParcel(Parcel source) {
                    return new Oddjob(source);
                }

                @Override
                public Oddjob[] newArray(int size) {
                    return new Oddjob[size];
                }
            };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id.toString());
        dest.writeString(solicitorId.toString());
        dest.writeString(title);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeLong(expiry.getTime());
        dest.writeDouble(keyLocation.getLatitude());
        dest.writeDouble(keyLocation.getLongitude());

        int bannedLackeyCount = bannedLackeys.length;
        dest.writeInt(bannedLackeyCount);
        for (Oid bannedLackeyId : bannedLackeys) {
            dest.writeString(bannedLackeyId.toString());
        }

        int applicantCount = applicants.length;
        dest.writeInt(applicantCount);
        for (Oid applicantId : applicants) {
            dest.writeString(applicantId.toString());
        }

        dest.writeParcelable(chatroom, 0);
    }

    private Oddjob(Parcel in) {
        _id = new Oid(in.readString());
        solicitorId = new Oid(in.readString());
        title = in.readString();
        description = in.readString();
        price = in.readDouble();
        expiry = new MongoDate(in.readLong());

        /* keyLocation */
        double lat = in.readDouble();
        double lon = in.readDouble();
        keyLocation = new Location("");
        keyLocation.setLatitude(lat);
        keyLocation.setLongitude(lon);

        /* bannedLackeys */
        int bannedLackeyCount = in.readInt();
        bannedLackeys = new Oid[bannedLackeyCount];
        for (int i = 0; i < bannedLackeyCount; i++) {
            bannedLackeys[i] = new Oid(in.readString());
        }

        /* applicants */
        int applicantCount = in.readInt();
        applicants = new Oid[applicantCount];
        for (int i = 0; i < applicantCount; i++) {
            applicants[i] = new Oid(in.readString());
        }

        chatroom = in.readParcelable(Chatroom.class.getClassLoader());
    }
}
