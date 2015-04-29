package edu.indiana.maxandblack.domeafavor.activities.createoddjob;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;
import org.lcsky.SVProgressHUD;

import java.util.ArrayList;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.activities.selectfriends.SelectFriendsActivity;
import edu.indiana.maxandblack.domeafavor.andrest.AndrestClient;
import edu.indiana.maxandblack.domeafavor.andrest.RESTException;
import edu.indiana.maxandblack.domeafavor.models.oddjobs.Oddjob;
import edu.indiana.maxandblack.domeafavor.models.users.MainUser;
import edu.indiana.maxandblack.domeafavor.models.users.User;
import edu.indiana.maxandblack.domeafavor.models.datatypes.Oid;


public class CreateOddjobActivity extends ActionBarActivity implements CreateOddjobFragment.CreateOddjobFragmentListener {

    /* request codes for sub-activities */
    public static final int SELECT_FRIENDS_REQUEST = 23;
    public static final int PLACE_PICKER_REQUEST = 32;

    private final AndrestClient domeafavorAndrestClient = new AndrestClient();
    private Boolean postInProgress = false;
    private ArrayList<User> invitedLackeys = new ArrayList<>();

    private CreateOddjobFragment createOddjobFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_oddjob);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_oddjob, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_FRIENDS_REQUEST) {
            if (resultCode == RESULT_OK) {
                /* retrieve the selected friend ids */
                Bundle b = data.getExtras();
                ArrayList<String> selectedFriendIds = data.getStringArrayListExtra(getString(R.string.intent_key_friend_ids));
                invitedLackeys.clear();
                /* retrieve acutal user objects based on friend ids, authorize them */
                for (String userId : selectedFriendIds) {
                    invitedLackeys.add(MainUser.getInstance().getFriend(new Oid(userId)));
                }
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place selectedPlace = PlacePicker.getPlace(data, this);
                LatLng placeLatLong = selectedPlace.getLatLng();
                /* convert Place into Location */
                Location selectedLoc = new Location("");
                selectedLoc.setLatitude(placeLatLong.latitude);
                selectedLoc.setLongitude(placeLatLong.longitude);

                createOddjobFragment.setJobLocation(selectedLoc);
            } else {
                /* some sort of failure, cannot use location */
                createOddjobFragment.setJobLocation(null);
            }
        }
    }

    public void launchFriendPicker() {
        /* bundle the pre-authorized lackey ids */
        ArrayList<String> authorizedLackeyIds = new ArrayList<>();
        for (User friend : invitedLackeys) {
            authorizedLackeyIds.add(friend.get_id().toString());
        }
        Intent authorizeLackeysIntent = new Intent(this, SelectFriendsActivity.class);
        authorizeLackeysIntent.putExtra(getString(R.string.intent_key_friend_ids), authorizedLackeyIds);
        startActivityForResult(authorizeLackeysIntent, CreateOddjobActivity.SELECT_FRIENDS_REQUEST);
    }

    @Override
    public void onBackPressed() {
        if (!postInProgress) {
            /* only allow back presses when not sending a request */
            super.onBackPressed();
        }
    }

    @Override
    public void onOddjobFormCompletion(final Oddjob oddjob) {
        /* extract authorized lackey objectIds */
        Oid[] invitedLackeyOids = new Oid[invitedLackeys.size()];
        for (int i = 0; i < invitedLackeyOids.length; i++) {
            invitedLackeyOids[i] = invitedLackeys.get(i).get_id();
        }
        oddjob.setApplicants(invitedLackeyOids);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        SVProgressHUD.showInView(this, getString(R.string.create_oddjob_svprogress), true);

        AsyncTask<Void, Void, RESTException> postOddjobTask = new AsyncTask<Void, Void, RESTException>() {
            @Override
            protected RESTException doInBackground(Void... params) {
                String postUrl = getString(R.string.dmfv_host) + getString(R.string.dmfv_create_oddjob);
                JSONObject postJson = oddjob.getPOSTJson();
                try {
                    domeafavorAndrestClient.post(postUrl, postJson);
                    return null;
                } catch (RESTException e) {
                    return e;
                }
            }

            @Override
            protected void onPostExecute(RESTException e) {
                Toast message = null;
                if (e != null) {
                    message = Toast.makeText(CreateOddjobActivity.this, getString(R.string.general_server_error), Toast.LENGTH_LONG);
                } else {
                    message = Toast.makeText(CreateOddjobActivity.this, getString(R.string.server_success), Toast.LENGTH_LONG);
                }
                /* return control of the UI to the user */
                SVProgressHUD.dismiss(CreateOddjobActivity.this);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                message.show();
                if (e == null) {
                    /* exit activity if there was no error */
                    finish();
                }
            }
        };
        postOddjobTask.execute();
    }

    @Override
    public void onCreateOddjobCancel() {
        /* abandon this activity */
        finish();
    }

    @Override
    public void launchPlacePicker(CreateOddjobFragment sender) throws
            GooglePlayServicesNotAvailableException,
            GooglePlayServicesRepairableException {

        /* store the calling fragment so that it can be called later */
        createOddjobFragment = sender;
        /* launch the place picker */
        PlacePicker.IntentBuilder placePickerIntent = new PlacePicker.IntentBuilder();
        startActivityForResult(placePickerIntent.build(this), PLACE_PICKER_REQUEST);
    }
}
