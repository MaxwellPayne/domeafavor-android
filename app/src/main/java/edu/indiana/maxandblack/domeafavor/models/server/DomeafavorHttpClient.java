package edu.indiana.maxandblack.domeafavor.models.server;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Created by Max on 2/22/15.
 * A simple wrapper around the DefaultHttpClient.
 * All requests that come from DomeafavorClient get
 * "Domeafavor-Secret: (the domeafavor secret token)" header attached
 * before sending to the server. This token is intended to keep the
 * Domeafavor API private, only for use by this mobile application
 */
public class DomeafavorHttpClient implements HttpClient {
    private static final HttpClient defaultClient = new DefaultHttpClient();
    private static final String secretHeader = "Domeafavor-Secret";
    private static final String domeafavorSecretToken = "nzIIkn0Gg0X7Zw4iRl5RnK2PtE03Ii5bMFMpI8OGlrpFYZKgFPNC89yWaRPCT8O7";

    private static HttpRequest domeafavorHttpRequest(HttpRequest req) {
        req.addHeader(secretHeader, domeafavorSecretToken);
        return req;
    }

    private static HttpUriRequest domeafavorHttpUriRequest(HttpUriRequest req) {
        req.addHeader(secretHeader, domeafavorSecretToken);
        return req;
    }

    @Override
    public HttpParams getParams() {
        return defaultClient.getParams();
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        return defaultClient.getConnectionManager();
    }

    @Override
    public HttpResponse execute(HttpUriRequest httpUriRequest) throws IOException, ClientProtocolException {
        httpUriRequest = domeafavorHttpUriRequest(httpUriRequest);
        return null;
    }

    @Override
    public HttpResponse execute(HttpUriRequest httpUriRequest, HttpContext httpContext) throws IOException, ClientProtocolException {
        httpUriRequest = domeafavorHttpUriRequest(httpUriRequest);
        return defaultClient.execute(httpUriRequest, httpContext);
    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest) throws IOException, ClientProtocolException {
        httpRequest = domeafavorHttpRequest(httpRequest);
        return defaultClient.execute(httpHost, httpRequest);
    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws IOException, ClientProtocolException {
        httpRequest = domeafavorHttpRequest(httpRequest);
        return defaultClient.execute(httpHost, httpRequest, httpContext);
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        httpUriRequest = domeafavorHttpUriRequest(httpUriRequest);
        return defaultClient.execute(httpUriRequest, responseHandler);
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException, ClientProtocolException {
        httpUriRequest = domeafavorHttpUriRequest(httpUriRequest);
        return defaultClient.execute(httpUriRequest, responseHandler, httpContext);
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        httpRequest = domeafavorHttpRequest(httpRequest);
        return defaultClient.execute(httpHost, httpRequest, responseHandler);
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException, ClientProtocolException {
        httpRequest = domeafavorHttpRequest(httpRequest);
        return defaultClient.execute(httpHost, httpRequest, responseHandler, httpContext);
    }
}
