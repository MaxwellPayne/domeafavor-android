package edu.indiana.maxandblack.domeafavor.activities.monitoroddjobs;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import edu.indiana.maxandblack.domeafavor.activities.chats.ChatActivity;
import edu.indiana.maxandblack.domeafavor.activities.findoddjob.OddjobFragment;
import edu.indiana.maxandblack.domeafavor.models.oddjobs.Oddjob;
import edu.indiana.maxandblack.domeafavor.R;

public class EditOddjobActivity extends ActionBarActivity implements OddjobFragment.OddjobFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_oddjob);

        /* retrieve oddjob from the arguments */
        Bundle args = getIntent().getExtras();
        Oddjob mainJob = (Oddjob) args.get(Oddjob.MAIN_SERIALIZED_ODDJOB_KEY);
        /* oddjob CANNOT be null */
        assert mainJob != null;

        /* set up an OddjobFragment with this job */
        OddjobFragment oddjobFragment = new OddjobFragment();
        oddjobFragment.setJob(mainJob);

        FragmentTransaction oddjobFragmentTransaction = getSupportFragmentManager().beginTransaction();
        oddjobFragmentTransaction.add(R.id.editOddjobFragmentContainer, oddjobFragment);
        oddjobFragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_oddjob, menu);
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
    public void onLocationClick(Location loc) {
        /* view this loc in maps app */
        String uriString = String.format(getString(R.string.maps_intent_lat_lon_format),
                loc.getLatitude(), loc.getLongitude(),
                loc.getLatitude(), loc.getLongitude());
        Intent showMap = new Intent(Intent.ACTION_VIEW);
        showMap.setData(Uri.parse(uriString));
        if (showMap.resolveActivity(getPackageManager()) != null) {
            /* can open in maps app */
            startActivity(showMap);
        } else {
            /* maps app not installed or something */
            new AlertDialog.Builder(this)
                    .setTitle("No Maps App")
                    .setMessage("Need to be able to open in Maps to view locations")
                    .show();
        }
    }

    @Override
    public void onCommentClick(Oddjob job) {
        Intent enterChatroomIntent = new Intent(this, ChatActivity.class);
        enterChatroomIntent.putExtra(Oddjob.MAIN_SERIALIZED_ODDJOB_KEY, job);

        startActivity(enterChatroomIntent);
    }
}
