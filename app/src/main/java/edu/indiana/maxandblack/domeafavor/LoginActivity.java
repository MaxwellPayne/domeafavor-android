package edu.indiana.maxandblack.domeafavor;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;

import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Request;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import android.widget.Button;
import android.view.View.OnClickListener;
import edu.indiana.maxandblack.domeafavor.LoginOrRegister;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import edu.indiana.maxandblack.domeafavor.User;
import com.zackehh.andrest.AndrestClient;
import com.zackehh.andrest.RESTException;

public class LoginActivity extends ActionBarActivity implements LoginOrRegister.OnLoginOrRegisterInteractionListener {

    public static final String TAG = "LoginActivity";
    private GraphUser fbUserData;

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
    }

    private class GetUserProfile extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String... fbIds) {
            AndrestClient request = new AndrestClient();
            String fbId = fbIds[0];
            String s = String.format(getString(R.string.dmfv_getuser_byfb), DMFV_API_ROOT, fbId);
            try {
                JSONObject response = request.get(s);
                Log.d(TAG, response.toString());
                return null;
            } catch (RESTException e) {
                Log.d(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {

        }
    }
}
