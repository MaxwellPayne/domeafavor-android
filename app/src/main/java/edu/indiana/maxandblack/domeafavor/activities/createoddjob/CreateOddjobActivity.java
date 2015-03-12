package edu.indiana.maxandblack.domeafavor.activities.createoddjob;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import org.lcsky.SVProgressHUD;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.andrest.AndrestClient;
import edu.indiana.maxandblack.domeafavor.andrest.RESTException;
import edu.indiana.maxandblack.domeafavor.models.oddjobs.Oddjob;


public class CreateOddjobActivity extends ActionBarActivity implements CreateOddjobFragment.CreateOddjobFragmentListener {

    private final AndrestClient domeafavorAndrestClient = new AndrestClient();

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
    public void onOddjobFormCompletion(Oddjob oddjob) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        SVProgressHUD.showInView(this, getString(R.string.create_oddjob_svprogress), true);

        AsyncTask<Void, Void, RESTException> postOddjobTask = new AsyncTask<Void, Void, RESTException>() {
            @Override
            protected RESTException doInBackground(Void... params) {

                return null;
            }

            @Override
            protected void onPostExecute(RESTException e) {
                /*
                Toast errorToast = null;
                if (e != null) {
                    errorToast = Toast.makeText(CreateOddjobActivity.this, getString(R.string.general_server_error), Toast.LENGTH_LONG);
                }
                SVProgressHUD.dismiss(CreateOddjobActivity.this);
                if (errorToast != null) {
                    errorToast.show();
                }*/
            }
        };

    }

    @Override
    public void onCreateOddjobCancel() {
        /* abandon this activity */
        finish();
    }
}
