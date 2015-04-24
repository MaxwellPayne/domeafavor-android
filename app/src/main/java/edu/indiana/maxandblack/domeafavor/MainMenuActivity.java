package edu.indiana.maxandblack.domeafavor;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.Session;

import edu.indiana.maxandblack.domeafavor.Login.LoginActivity;
import edu.indiana.maxandblack.domeafavor.Login.LoginOrRegister;
import edu.indiana.maxandblack.domeafavor.activities.createoddjob.CreateOddjobActivity;
import edu.indiana.maxandblack.domeafavor.activities.findoddjob.FindOddjobActivity;
import edu.indiana.maxandblack.domeafavor.activities.monitoroddjobs.MonitorOddjobsActivity;
import edu.indiana.maxandblack.domeafavor.activities.selectfriends.SelectFriendsActivity;
import edu.indiana.maxandblack.domeafavor.models.users.MainUser;


public class MainMenuActivity extends ActionBarActivity implements LoginOrRegister.OnLoginOrRegisterInteractionListener {

    private final String TAG = "MainMenuActivity";
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        MainUser.getInstance().startUpdatingLocation(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onViewOddjobsButtonClick(View v) {
        /* transition to the FindOddjobActivity */
        Intent viewOddjobsIntent = new Intent(this, FindOddjobActivity.class);
        startActivity(viewOddjobsIntent);
    }

    public void onCreateOddjobButtonClick(View v) {
        /* transition to the CreateOddjobActivity */
        Intent createOddjobsIntent = new Intent(this, CreateOddjobActivity.class);
        startActivity(createOddjobsIntent);
    }

    public void onMonitorOddjobsButtonClick(View v) {
        /* transition to the MonitorOddjobsActivity */
        Intent monitorOddjobsIntent = new Intent(this, MonitorOddjobsActivity.class);
        startActivity(monitorOddjobsIntent);
    }

    public void onDiscoverFriendsButtonClick(View v) {
        Intent discoverFriendsIntent = new Intent(this, SelectFriendsActivity.class);
        discoverFriendsIntent.putExtra(getString(R.string.intent_key_is_discovery_mode), true);
        startActivity(discoverFriendsIntent);
    }

    @Override
    public void onFacebookLoginFailure(LoginOrRegister loginFragment, Session session, Exception exception) {
        /* pass */
    }

    @Override
    public void onFacebookLoginSuccess(LoginOrRegister loginFragment, Session session) {
        /* pass */
    }

    @Override
    public void onFacebookLogout(LoginOrRegister loginFragment, Session session) {
        Intent returnToLoginIntent = new Intent(this, LoginActivity.class);
        startActivity(returnToLoginIntent);
        finish();
    }
}
