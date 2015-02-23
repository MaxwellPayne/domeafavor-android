package edu.indiana.maxandblack.domeafavor.models.server;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;

/**
 * Created by Max on 2/22/15.
 */
enum DomeafavorAppsecretHeaders {
    SECRET_HEADER("Domeafavor-Secret"),
    SECRET_TOKEN("nzIIkn0Gg0X7Zw4iRl5RnK2PtE03Ii5bMFMpI8OGlrpFYZKgFPNC89yWaRPCT8O7")
    ;

    private final String text;

    private DomeafavorAppsecretHeaders(final String text) {
        this.text = text;
    }

    static void applySecretHeader(HttpRequest req) {
        req.addHeader(DomeafavorAppsecretHeaders.SECRET_HEADER.toString(), DomeafavorAppsecretHeaders.SECRET_TOKEN.toString());
    }

    @Override
    public String toString() {
        return text;
    }
}
