package edu.indiana.maxandblack.domeafavor.models.payments;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;

import edu.indiana.maxandblack.domeafavor.models.datatypes.MongoDate;
import edu.indiana.maxandblack.domeafavor.models.datatypes.Oid;
import edu.indiana.maxandblack.domeafavor.models.ServerEntity;

/**
 * Created by Max on 4/6/15.
 */
public class Payment extends ServerEntity {
    protected MongoDate transactionDate;

    public Payment(JSONObject j) {
        loadFromJson(j);
    }

    @Override
    protected void loadFromJson(JSONObject jsonObject) {
        Iterator<String> properties = jsonObject.keys();
        while (properties.hasNext()) {
            try {
                String key = properties.next();
                if (key.equals("$oid")) {
                    _id = new Oid(jsonObject.getJSONObject(key));
                } else if (key.equals("transaction_date")) {
                    transactionDate = new MongoDate(jsonObject.getJSONObject(key));
                }
            } catch (JSONException e) {

            }
        }
    }

    @Override
    public JSONObject getPOSTJson() {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
