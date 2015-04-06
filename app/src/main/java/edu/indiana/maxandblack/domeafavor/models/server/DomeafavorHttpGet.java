package edu.indiana.maxandblack.domeafavor.models.server;

import org.apache.http.client.methods.HttpGet;

import java.net.URI;

import edu.indiana.maxandblack.domeafavor.models.users.MainUser;

/**
 * Created by Max on 2/22/15.
 */
public class DomeafavorHttpGet extends HttpGet {


    public DomeafavorHttpGet() {
        super();
        DomeafavorAppsecretHeaders.applySecretHeader(this);
        MainUser.getInstance().applyAuthorizationHeader(this);
    }

    public DomeafavorHttpGet(String uri) {
        super(uri);
        DomeafavorAppsecretHeaders.applySecretHeader(this);
        MainUser.getInstance().applyAuthorizationHeader(this);
    }

    public DomeafavorHttpGet(URI uri) {
        super(uri);
        DomeafavorAppsecretHeaders.applySecretHeader(this);
        MainUser.getInstance().applyAuthorizationHeader(this);
    }
}
