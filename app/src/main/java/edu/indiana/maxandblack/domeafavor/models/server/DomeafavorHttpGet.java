package edu.indiana.maxandblack.domeafavor.models.server;

import org.apache.http.client.methods.HttpGet;

import java.net.URI;

/**
 * Created by Max on 2/22/15.
 */
public class DomeafavorHttpGet extends HttpGet {


    public DomeafavorHttpGet() {
        super();
        DomeafavorAppsecretHeaders.applySecretHeader(this);
    }

    public DomeafavorHttpGet(String uri) {
        super(uri);
        DomeafavorAppsecretHeaders.applySecretHeader(this);
    }

    public DomeafavorHttpGet(URI uri) {
        super(uri);
        DomeafavorAppsecretHeaders.applySecretHeader(this);
    }
}
