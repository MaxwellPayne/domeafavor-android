package edu.indiana.maxandblack.domeafavor;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;

import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Request;
import com.facebook.model.GraphUser;

import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.indiana.maxandblack.domeafavor.models.MainUser;
import edu.indiana.maxandblack.domeafavor.models.User;


public class LoginActivity extends ActionBarActivity implements LoginOrRegister.OnLoginOrRegisterInteractionListener {

    public static final String TAG = "LoginActivity";
    private GraphUser fbUserData;
    private static final HttpClient domeafavorClient = new DefaultHttpClient();

    private static String DMFV_API_ROOT;
    private enum AuthenticationMethod {
        FACEBOOK, CUSTOM
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DMFV_API_ROOT = getString(R.string.dmfv_host);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return false;
    }

    /* Conform to LoginOrRegister's interface */
    @Override
    public void onFacebookLoginSuccess(LoginOrRegister loginOrRegister, final Session session) {
        Request meRequest = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser graphUser, Response response) {
                if (Session.getActiveSession() == session && graphUser != null) {
                    fbUserData = graphUser;

                    //loginToDomeafavor(graphUser.getInnerJSONObject(), AuthenticationMethod.FACEBOOK);
                    new GetUserProfile().execute(fbUserData.getId());
                }
                if (response.getError() != null) {
                    /* @hack - no error handling */
                }
            }
        });
        meRequest.executeAsync();
    }

    @Override
    public void onFacebookLoginFailure(LoginOrRegister loginOrRegister, Session session, Exception exception) {

    }

    private void segueIntoApp() {
        /* all logging in is done, transition into the rest of app */
        Toast.makeText(getApplicationContext(), "Logged in as " + MainUser.getInstance().toString(), Toast.LENGTH_LONG).show();
    }

    private void handleFailedServerConnection() {
        /* couldn't get a response from the server */
        Toast.makeText(getApplicationContext(), "Problem connecting to the server", Toast.LENGTH_LONG).show();
    }

    /**
     * Attempt to get user profile. Response status 200 logs in a user
     * with an existing profile then segues into the app. Response status 204
     * prompts user to create an account. Anything else calls handleFailedServerConnection()
     */
    private class GetUserProfile extends AsyncTask<String, Void, HttpResponse> {
        @Override
        protected HttpResponse doInBackground(String... fbIds) {
            String fbId = fbIds[0];
            String url = String.format(getString(R.string.dmfv_getuser_byfb), DMFV_API_ROOT, fbId);
            HttpGet request = new HttpGet(url);
            try {
                return domeafavorClient.execute(request);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                /* login success */
                    try {
                        /* unpack json */
                        BufferedReader jsonReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                        String jsonAsString = "", line;
                        while ((line = jsonReader.readLine()) != null) {
                            jsonAsString += line;
                        }
                        JSONObject mainUserJson = new JSONObject(jsonAsString);
                        /* instantiate the main user from json */
                        MainUser.getInstance().loadFromJson(mainUserJson);
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                    /* done logging in! */
                    LoginActivity.this.segueIntoApp();
                } else if (statusCode == 204) {
                /* create profile */
                    Toast.makeText(getApplicationContext(), "{Zach's Register Screen Here}", Toast.LENGTH_LONG).show();
                } else {
                    /* got unexpected status code */
                    LoginActivity.this.handleFailedServerConnection();
                }
            } else {
                /* could not connect to server */
                LoginActivity.this.handleFailedServerConnection();
            }
        }
    }
}
