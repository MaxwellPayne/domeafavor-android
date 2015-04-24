package edu.indiana.maxandblack.domeafavor.Login;

/**
 * Created by Max on 3/3/15.
 */

import android.content.res.Resources;

import com.facebook.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import edu.indiana.maxandblack.domeafavor.models.ServerEntity;

public class OAuth2AccessToken extends ServerEntity {

    private final String FB_TOKEN_TYPE = "bearer";
    private String accessToken;
    private String tokenType;
    private Date expiresAt;

    public OAuth2AccessToken(String accessTok, String tokenTyp, Date expiry) {
        accessToken = accessTok;
        tokenType = tokenTyp;
        expiresAt = expiry;
    }

    public OAuth2AccessToken(Session sesh) {
        accessToken = sesh.getAccessToken();
        tokenType = FB_TOKEN_TYPE;
        expiresAt = sesh.getExpirationDate();
    }

    @Override
    protected void loadFromJson(JSONObject jsonObject) {
        throw new RuntimeException("not implemented");
    }

    public JSONObject getPOSTJson() {
        try {
            JSONObject j = new JSONObject();
            j.put("access_token", accessToken);
            j.put("token_type", tokenType);
            j.put("expires_at", expiresAt.getTime());
            return j;
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s", tokenType, accessToken);
    }
}
