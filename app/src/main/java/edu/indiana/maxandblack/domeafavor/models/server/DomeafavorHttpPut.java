package edu.indiana.maxandblack.domeafavor.models.server;

import org.apache.http.client.methods.HttpPut;

import java.net.URI;

import edu.indiana.maxandblack.domeafavor.models.users.MainUser;

/**
 * Created by Max on 2/22/15.
 */
public class DomeafavorHttpPut extends HttpPut {

    private void setJsonHeader() {
        setHeader("Content-Type", "application/json");
    }

    public DomeafavorHttpPut() {
        super();
        DomeafavorAppsecretHeaders.applySecretHeader(this);
        MainUser.getInstance().applyAuthorizationHeader(this);
        setJsonHeader();
    }

    public DomeafavorHttpPut(String uri) {
        super(uri);
        DomeafavorAppsecretHeaders.applySecretHeader(this);
        MainUser.getInstance().applyAuthorizationHeader(this);
        setJsonHeader();
    }

    public DomeafavorHttpPut(URI uri) {
        super(uri);
        DomeafavorAppsecretHeaders.applySecretHeader(this);
        MainUser.getInstance().applyAuthorizationHeader(this);
        setJsonHeader();
    }
}
