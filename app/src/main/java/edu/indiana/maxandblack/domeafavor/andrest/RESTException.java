package edu.indiana.maxandblack.domeafavor.andrest;

import org.json.JSONObject;

/**
 * Extremely simple Exception class to be used alongside Andrest.
 * 
 * Not entirely sure where this will go, but the ability to pass a
 * JSONObject means that custom errors can be built easily (e.g.
 * for passing things like status codes and metadata).
 * 
 * @author 	Isaac Whitfield
 * @version	09/03/2014
 *
 */
public class RESTException extends Exception {

    public static enum StatusCode {
        FAILED_CONNECTION, OK, CREATED, ACCEPTED, NO_CONTENT, UNAUTHORIZED,
        FORBIDDEN, NOT_FOUND, REQUEST_TIMEOUT, SERVER_ERROR;

        public static StatusCode fromInt(int code) {
            switch (code) {
                case 200:
                    return OK;
                case 201:
                    return CREATED;
                case 202:
                    return ACCEPTED;
                case 204:
                    return NO_CONTENT;
                case 401:
                    return UNAUTHORIZED;
                case 403:
                    return FORBIDDEN;
                case 404:
                    return NOT_FOUND;
                case 408:
                    return REQUEST_TIMEOUT;
                case 500:
                    return SERVER_ERROR;
                default:
                    return FAILED_CONNECTION;
            }
        }
    }
	
	private static final long serialVersionUID = 4491098305202657442L;
    private JSONObject errorObject;
    private StatusCode statusCode;

	public RESTException(String message, StatusCode code){
        super(message);
        statusCode = code;
	}
	
	/*public RESTException(JSONObject errorObject){
		super(errorObject.toString());
        this.errorObject = errorObject;
	}*/

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
