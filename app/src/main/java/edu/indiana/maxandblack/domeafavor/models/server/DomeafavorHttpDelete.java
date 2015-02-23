package edu.indiana.maxandblack.domeafavor.models.server;

import org.apache.http.client.methods.HttpDelete;

import java.net.URI;

/**
 * Created by Max on 2/22/15.
 */
public class DomeafavorHttpDelete extends HttpDelete {
    public DomeafavorHttpDelete() {
        super();
        DomeafavorAppsecretHeaders.applySecretHeader(this);
    }

    public DomeafavorHttpDelete(String uri) {
        super(uri);
        DomeafavorAppsecretHeaders.applySecretHeader(this);
    }

    public DomeafavorHttpDelete(URI uri) {
        super(uri);
        DomeafavorAppsecretHeaders.applySecretHeader(this);
    }
}
