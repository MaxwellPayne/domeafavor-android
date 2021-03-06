package edu.indiana.maxandblack.domeafavor.activities.findoddjob;

import android.app.AlertDialog;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.activities.chats.ChatActivity;
import edu.indiana.maxandblack.domeafavor.andrest.AndrestClient;
import edu.indiana.maxandblack.domeafavor.andrest.RESTException;
import edu.indiana.maxandblack.domeafavor.models.oddjobs.Oddjob;
import edu.indiana.maxandblack.domeafavor.models.users.MainUser;


public class FindOddjobActivity extends ActionBarActivity implements OddjobListFragment.OddjobListFragmentListener, OddjobFragment.OddjobFragmentListener {

    private static final String TAG = "FindOddjobActivity";
    private static final AndrestClient andrestDomeafavorClient = new AndrestClient();
    private OddjobListFragment oddjobListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_oddjob);

        if (savedInstanceState != null) {
            /* already have fragments set up, do nothing */
            return;
        }

        OddjobListFragment listFragment = new OddjobListFragment();
        oddjobListFragment = listFragment;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.findOddjobFragmentContainer, listFragment)
                .commit();
        requeryOddjobs();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_oddjob, menu);
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

    private void requeryOddjobs() {
        /* @hack - should specify parameters, right now just querying for all */

            AsyncTask<Void, Void, ArrayList<Oddjob>> t = new AsyncTask<Void, Void, ArrayList<Oddjob>>() {
                @Override
                protected ArrayList<Oddjob> doInBackground(Void... params) {
                    try {
                        Location mainUserLocation = MainUser.getInstance().getLoc();
                        String requeryUrl = getString(R.string.dmfv_discover_oddjobs,
                                getString(R.string.dmfv_host),
                                MainUser.getInstance().get_id().toString(),
                                getResources().getInteger(R.integer.max_discovery_distance),
                                mainUserLocation.getLatitude(),
                                mainUserLocation.getLongitude());
                        JSONArray oddjobs = andrestDomeafavorClient.getArray(requeryUrl);
                        ArrayList<Oddjob> oddjobArrayList = new ArrayList<>(oddjobs.length());
                        for (int i = 0; i < oddjobs.length(); i++) {
                            Oddjob job = new Oddjob(oddjobs.getJSONObject(i));
                            oddjobArrayList.add(job);
                        }
                        return oddjobArrayList;
                    } catch (RESTException | JSONException e) {
                        Log.d(TAG, e.toString());
                        return new ArrayList<>();
                    }
                }

                @Override
                protected void onPostExecute(ArrayList<Oddjob> j) {
                    if (oddjobListFragment != null) {
                        oddjobListFragment.setOddjobs(j);
                    }
                }
            };
            t.execute();


    }

    /** Conform to OddjobListFragmentListener **/
    @Override
    public void onOddjobSelect(Oddjob job) {
        OddjobFragment oddjobFragment = new OddjobFragment();
        oddjobFragment.setJob(job);

        /* push OddjobFragment detail fragment onto navigation stack */
        FragmentTransaction pushOddjobTransaction = getSupportFragmentManager().beginTransaction();
        pushOddjobTransaction.replace(R.id.findOddjobFragmentContainer, oddjobFragment);
        pushOddjobTransaction.addToBackStack(null);
        pushOddjobTransaction.commit();
    }

    /** Conform to OddjobFragmentListener **/
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
