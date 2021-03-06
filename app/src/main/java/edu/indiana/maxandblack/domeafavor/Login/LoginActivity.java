package edu.indiana.maxandblack.domeafavor.Login;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;

import com.facebook.HttpMethod;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Request;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.indiana.maxandblack.domeafavor.MainMenuActivity;
import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.andrest.AndrestClient;
import edu.indiana.maxandblack.domeafavor.andrest.RESTException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.indiana.maxandblack.domeafavor.models.users.MainUser;


public class LoginActivity extends ActionBarActivity implements LoginOrRegister.OnLoginOrRegisterInteractionListener, RegisterFragment.RegisterFragmentListener {

    public static final String TAG = "LoginActivity";
    private GraphUser fbUserData;
    private ArrayList<String> friendFacebookIds;
    private static final HttpClient httpDomeafavorClient = new DefaultHttpClient();
    private static final AndrestClient andrestDomeafavorClient = new AndrestClient();

    private static String DMFV_API_ROOT;
    private enum AuthenticationMethod {
        FACEBOOK, CUSTOM
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DMFV_API_ROOT = getString(R.string.dmfv_host);
        setContentView(R.layout.activity_login);

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                /* @hack - I don't know what this does */
                return;
            }
            /* initialize the login button fragment */
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
            LoginOrRegister loginFragment = new LoginOrRegister();
            transaction.add(R.id.fragment_container, loginFragment);
            transaction.commit();
        }
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MainUser.getInstance().setToken(new OAuth2AccessToken(session));
                    OAuth2AccessToken shortLivedToken = new OAuth2AccessToken(session);
                    //JSONObject res = andrestDomeafavorClient.post(getString(R.string.dmfv_exchangetoken_fb,
                    //        getString(R.string.dmfv_host), "hi"), shortLivedToken.getPOSTJson());
                    final String oddjobId = "5538338eb2eb6c7a7c775f8b";
                    JSONObject res = andrestDomeafavorClient.get(getString(R.string.dmfv_host) + "/chatrooms/" + oddjobId);
                } catch (RESTException e) {

                }
            }
        }).start();

        Request meRequest = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser graphUser, Response response) {
                if (Session.getActiveSession() == session && graphUser != null) {
                    fbUserData = graphUser;
                    MainUser.getInstance().setFacebookId(fbUserData.getId());
                    MainUser.getInstance().setFirstName(fbUserData.getFirstName());
                    MainUser.getInstance().setLastName(fbUserData.getLastName());

                    SimpleDateFormat birthdayFmt = new SimpleDateFormat("MM/dd/yyyy");
                    try {
                        MainUser.getInstance().setBirthday(birthdayFmt.parse(graphUser.getBirthday()));
                    } catch (ParseException e) {
                        Log.d(TAG, "err parsing fb date");
                    }

                    Map<String, Object> fbUserMap = graphUser.asMap();
                    final String emailKey = "email";
                    if (fbUserMap.containsKey(emailKey)) {
                        MainUser.getInstance().setEmail((String) fbUserMap.get(emailKey));
                    }

                    MainUser.getInstance().setFacebookProfile(fbUserData);
                    MainUser.getInstance().setToken(new OAuth2AccessToken(session));
                    /* @hack - need to set custom username here */
                    MainUser.getInstance().setUsername(fbUserData.getId() + "_" +
                            fbUserData.getFirstName() + "_" + fbUserData.getLastName());

                    /* get user friends after ascertaining facebook id */
                    String friendsGraphPath = String.format(getString(R.string.facebook_friends_endpoint), fbUserData.getId());
                    Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {
                        @Override
                        public void onCompleted(List<GraphUser> graphUsers, Response response) {
                            if (response.getError() != null) {
                                /* @hack - unhandled facebook error */
                                System.exit(1);
                            }
                            friendFacebookIds = new ArrayList<String>(graphUsers.size());
                            /* grab facebook ids of all friends */
                            for (GraphUser friend : graphUsers) {
                                friendFacebookIds.add(friend.getId());
                            }
                            /* log into domeafavor server */
                            new GetUserProfile().execute(fbUserData.getId());
                        }
                    }).executeAsync();
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
        /* @hack - unhandled facebook failure */
    }

    @Override
    public void onFacebookLogout(LoginOrRegister loginFragment, Session session) {

    }

    @Override
    public void onRegisterFormCompletion() {
        /**
         * User filled out the registration form and
         * is now creating their profile. Do this async,
         * and upon success transition into the app
         */

         AsyncTask<Void, Void, RESTException> registerTask =  new AsyncTask<Void, Void, RESTException>() {
            @Override
            protected RESTException doInBackground(Void... params) {
                String createUserFbUrl = String.format(getString(R.string.dmfv_createuser_byfb) ,DMFV_API_ROOT);
                JSONObject createUserResponse;
                try {
                    /* create user, don't yet add friends */
                    createUserResponse = andrestDomeafavorClient.post(createUserFbUrl, MainUser.getInstance().getPOSTJson());
                    try {
                        /* assign facebook friends who are already using the app */
                        JSONObject facebookFriendJson = new JSONObject();
                        facebookFriendJson.put("facebook_friends", new JSONArray(friendFacebookIds));
                        /* @hack - not implemented on the server side */
                        //createUserResponse = andrestDomeafavorClient.put("/somePostFBFriendsEndpoint", facebookFriendJson);
                    } catch (JSONException e) {
                        Log.d(TAG, e.toString());
                    }
                    /* unpack created user json into the MainUser */
                    MainUser.getInstance().loadFromJson(createUserResponse);
                    return null;
                } catch (RESTException e) {
                    Log.d(TAG, e.toString());
                    return e;
                }
            }

            @Override
            protected void onPostExecute(RESTException e) {
                super.onPostExecute(e);

                if (e != null) {
                    Log.d(TAG, "Failed at registration - exiting");
                    System.exit(1);
                }
                MainUser u = MainUser.getInstance();
                LoginActivity.this.segueIntoApp();
            }
        };
        registerTask.execute();

    }

    @Override
    public void onFormDismiss() {
        segueIntoApp();
    }

    private void segueIntoApp() {
        /* all logging in is done, transition into the rest of app */
        Toast.makeText(getApplicationContext(), "Logged in as " + MainUser.getInstance().toString(), Toast.LENGTH_LONG).show();
        Intent segueIntoAppIntent = new Intent(this, MainMenuActivity.class);
        startActivity(segueIntoAppIntent);
        finish();
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
                return httpDomeafavorClient.execute(request);
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
                    if (fbUserData != null) {
                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        RegisterFragment registerFragment = new RegisterFragment();
                        fragmentTransaction.replace(R.id.fragment_container, registerFragment);
                        fragmentTransaction.commit();
                    } else {
                        Log.d(TAG, "Attempting to register with strategy other than facebook Not Implemented");
                        System.exit(1);
                    }
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
