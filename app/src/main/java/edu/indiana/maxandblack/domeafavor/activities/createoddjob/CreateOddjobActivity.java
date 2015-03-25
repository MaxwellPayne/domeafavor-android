package edu.indiana.maxandblack.domeafavor.activities.createoddjob;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONObject;
import org.lcsky.SVProgressHUD;

import java.util.ArrayList;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.activities.selectfriends.SelectFriendsActivity;
import edu.indiana.maxandblack.domeafavor.andrest.AndrestClient;
import edu.indiana.maxandblack.domeafavor.andrest.RESTException;
import edu.indiana.maxandblack.domeafavor.models.oddjobs.Oddjob;
import edu.indiana.maxandblack.domeafavor.models.users.User;
import edu.indiana.maxandblack.domeafavor.models.Oid;


public class CreateOddjobActivity extends ActionBarActivity implements CreateOddjobFragment.CreateOddjobFragmentListener,
        SelectFriendsActivity.SelectFriendsActivityListener {

    private final AndrestClient domeafavorAndrestClient = new AndrestClient();
    private Boolean postInProgress = false;
    private ArrayList<User> authorizedLackeys = new ArrayList<>();

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
    public void onBackPressed() {
        if (!postInProgress) {
            /* only allow back presses when not sending a request */
            super.onBackPressed();
        }
    }

    @Override
    public void onOddjobFormCompletion(final Oddjob oddjob) {
        /* extract authorized lackey objectIds */
        Oid[] authorizedLackeyOids = new Oid[authorizedLackeys.size()];
        for (int i = 0; i < authorizedLackeyOids.length; i++) {
            authorizedLackeyOids[i] = authorizedLackeys.get(i).get_id();
        }
        oddjob.setAuthorizedLackeys(authorizedLackeyOids);

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
    public void didSelectFriends(ArrayList<User> selectedFriends) {
        authorizedLackeys = selectedFriends;
    }

    @Override
    public void didCancelSelectingFriends() {
        // pass
    }
}