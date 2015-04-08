package edu.indiana.maxandblack.domeafavor.models;

import org.json.JSONObject;

import edu.indiana.maxandblack.domeafavor.models.datatypes.Oid;

/**
 * Created by Max on 4/6/15.
 */
public abstract class ServerEntity {
    protected Oid _id;

    abstract protected void loadFromJson(JSONObject jsonObject);

    abstract public JSONObject getPOSTJson();
}
