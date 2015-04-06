package edu.indiana.maxandblack.domeafavor.models.server;

import org.apache.http.client.methods.HttpDelete;

import java.net.URI;

import edu.indiana.maxandblack.domeafavor.models.users.MainUser;

/**
 * Created by Max on 2/22/15.
 */
public class DomeafavorHttpDelete extends HttpDelete {
    public DomeafavorHttpDelete() {
        super();
        DomeafavorAppsecretHeaders.applySecretHeader(this);
        MainUser.getInstance().applyAuthorizationHeader(this);
    }

    public DomeafavorHttpDelete(String uri) {
        super(uri);
        DomeafavorAppsecretHeaders.applySecretHeader(this);
        MainUser.getInstance().applyAuthorizationHeader(this);
    }

    public DomeafavorHttpDelete(URI uri) {
        super(uri);
        DomeafavorAppsecretHeaders.applySecretHeader(this);
        MainUser.getInstance().applyAuthorizationHeader(this);
    }
}
