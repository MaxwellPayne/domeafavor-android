package edu.indiana.maxandblack.domeafavor.models.server;

import org.apache.http.client.methods.HttpPost;

import java.net.URI;

/**
 * Created by Max on 2/22/15.
 */
public class DomeafavorHttpPut extends HttpPost {

    private void setJsonHeader() {
        setHeader("Content-Type", "application/json");
    }

    public DomeafavorHttpPut() {
        super();
        DomeafavorAppsecretHeaders.applySecretHeader(this);
        setJsonHeader();
    }

    public DomeafavorHttpPut(String uri) {
        super(uri);
        DomeafavorAppsecretHeaders.applySecretHeader(this);
        setJsonHeader();
    }

    public DomeafavorHttpPut(URI uri) {
        super(uri);
        DomeafavorAppsecretHeaders.applySecretHeader(this);
        setJsonHeader();
    }
}
