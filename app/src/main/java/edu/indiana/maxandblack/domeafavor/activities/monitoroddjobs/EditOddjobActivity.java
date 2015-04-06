package edu.indiana.maxandblack.domeafavor.activities.monitoroddjobs;

import android.location.Location;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
        // TODO: handle location click for OddjobFragment
    }
}
