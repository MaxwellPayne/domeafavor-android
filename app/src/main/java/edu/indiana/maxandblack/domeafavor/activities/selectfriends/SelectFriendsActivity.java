package edu.indiana.maxandblack.domeafavor.activities.selectfriends;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.lcsky.SVProgressHUD;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.andrest.AndrestClient;
import edu.indiana.maxandblack.domeafavor.models.datatypes.Oid;
import edu.indiana.maxandblack.domeafavor.models.users.MainUser;
import edu.indiana.maxandblack.domeafavor.models.users.User;

public class SelectFriendsActivity extends ActionBarActivity implements
        SearchView.OnCloseListener, SearchView.OnQueryTextListener {

    public final String TAG = "SelectFriendsActivity";
    private HashSet<User> selectedFriendSet = new HashSet<>();
    private final ArrayList<User> friendsDataSource = new ArrayList<>();

    /* semaphore used to control discovery as text changes */
    private final Semaphore canQueryForDiscoverySemaphore = new Semaphore(1);
    private final AndrestClient domeafavorClient = new AndrestClient();
    private final Timer queryScheduler = new Timer();
    private String previousQueryString;

    private boolean isDiscoveryMode = false;
    private Button doneButton;
    private SearchView discoverFriendsSearchView;
    private ListView selectFriendsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);

        discoverFriendsSearchView = (SearchView) findViewById(R.id.discoverFriendsSearchView);
        selectFriendsListView = (ListView) findViewById(R.id.selectFriendsListView);
        selectFriendsListView.setAdapter(new SelectFriendAdapter(this, friendsDataSource));

        isDiscoveryMode = getIntent().getBooleanExtra(getString(R.string.intent_key_is_discovery_mode), false);
        if (isDiscoveryMode) {
            /* using this activity for friend discovery */
            discoverFriendsSearchView.setOnCloseListener(this);
            discoverFriendsSearchView.setOnQueryTextListener(this);
            /* schedule recurring query */
            queryScheduler.schedule(new DiscoverFriendsQuery(), new Date(), 1000);
        } else {
            /* using this activity for selecting friends */
            discoverFriendsSearchView.setVisibility(View.INVISIBLE);

            friendsDataSource.addAll(MainUser.getInstance().getFriends());
            ArrayList<String> authorizedIds = getIntent()
                    .getStringArrayListExtra(getString(R.string.intent_key_friend_ids));
                /* check the boxes of already authorized friends */
            for (String friendId : authorizedIds) {
                selectedFriendSet.add(MainUser.getInstance().getFriend(new Oid(friendId)));
            }
            if (selectedFriendSet.isEmpty()) {
                /* trying to select existing friends when none exist; bail */
                Toast.makeText(this, getString(R.string.select_friends_youhave_nofriends),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }


        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectFriendResult = new Intent();
                ArrayList<String> friendIds = new ArrayList<>(selectedFriendSet.size());
                /* extract the _ids as strings from all friends */
                for (User friend : selectedFriendSet) {
                    friendIds.add(friend.get_id().toString());
                }
                selectFriendResult.putExtra(getString(R.string.intent_key_friend_ids),
                        friendIds);
                setResult(RESULT_OK, selectFriendResult);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_friends, menu);
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

    private void notifyDataSetChanged() {
        SelectFriendAdapter listAdapter =
                (SelectFriendAdapter) selectFriendsListView.getAdapter();
        listAdapter.notifyDataSetChanged();
    }

    private class SelectFriendAdapter extends ArrayAdapter<User> {

        public SelectFriendAdapter(Context context, ArrayList<User> friendList) {
            super(context, R.layout.friend_list_item, friendList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            User friendInFocus = getItem(position);
            View v = convertView;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.friend_list_item, parent, false);
            }
            TextView realNameTextView = (TextView) v.findViewById(R.id.friendListItemRealName);
            TextView usernameTextView = (TextView) v.findViewById(R.id.friendListItemUsername);
            CheckBox isSelectedCheckBox = (CheckBox) v.findViewById(R.id.isSelectedCheckBox);

            realNameTextView.setText(String.format("%s %s", friendInFocus.getFirstName(), friendInFocus.getLastName()));
            usernameTextView.setText(friendInFocus.getUsername());

            if (isDiscoveryMode) {
                /* check mark this user if they are a friend */
                isSelectedCheckBox.setChecked(MainUser.getInstance().getFriend(friendInFocus.get_id()) != null);
            } else {
                /* will be checked if friendInFocus is a member of the hashSet */
                isSelectedCheckBox.setChecked(selectedFriendSet.contains(friendInFocus));
            }
            /* attach user data to this view through its tag */
            v.setTag(friendInFocus);
            v.setOnClickListener(listItemClickListener);
            isSelectedCheckBox.setClickable(false);
            return v;
        }
    }

    private View.OnClickListener listItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final User clickedUser = (User) v.getTag();
            final boolean isAlreadyFriend = MainUser.getInstance().getFriend(clickedUser.get_id()) != null;
            if (isDiscoveryMode) {
                /* show a confirm dialog */
                AlertDialog.Builder confirmAlert = new AlertDialog.Builder(SelectFriendsActivity.this);
                DialogInterface.OnClickListener confirmListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                final MainUser mainUser = MainUser.getInstance();
                                SVProgressHUD.showInView(SelectFriendsActivity.this,
                                        getString(R.string.svprogress_default),
                                        false);
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                /* attempt to update the MainUser's friends on server */
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean updateFriendSuccess;
                                        if (isAlreadyFriend) {
                                            updateFriendSuccess = mainUser.removeFriend(clickedUser, SelectFriendsActivity.this);
                                        } else {
                                            /* adding friend */
                                            updateFriendSuccess = mainUser.addFriend(clickedUser, SelectFriendsActivity.this);
                                        }
                                        final int completionMsg = (updateFriendSuccess) ? R.string.success : R.string.failure;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                SVProgressHUD.dismiss(SelectFriendsActivity.this);
                                                notifyDataSetChanged();
                                                Toast.makeText(SelectFriendsActivity.this,
                                                        getString(completionMsg),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).start();

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                String message;
                if (isAlreadyFriend) {
                    /* deleting user */
                    message = getString(R.string.select_friends_remove_confirm_msg, clickedUser.getUsername());
                } else {
                    /* adding user */
                    message = getString(R.string.select_friends_add_confirm_msg, clickedUser.getUsername());
                }
                confirmAlert.setMessage(message)
                        .setPositiveButton(android.R.string.yes, confirmListener)
                        .setNegativeButton(android.R.string.no, confirmListener)
                        .show();

            } else {
                CheckBox selectedBox = (CheckBox) v.findViewById(R.id.isSelectedCheckBox);
                Boolean previouslyWasSelected = selectedBox.isChecked();
                if (previouslyWasSelected) {
                /* deselecting this friend */
                    selectedFriendSet.remove(clickedUser);
                } else {
                /* selecting this friend */
                    selectedFriendSet.add(clickedUser);
                }
            /* toggle selection of box */
                selectedBox.setChecked(!previouslyWasSelected);
            }
        }
    };

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() == 0) {
            friendsDataSource.clear();
            notifyDataSetChanged();
        }
        Log.d(TAG, String.format("onQueryTextChange %s", newText));
        return true;
    }

    @Override
    public boolean onClose() {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    protected class DiscoverFriendsQuery extends TimerTask {
        @Override
        public void run() {
        final String queryString = discoverFriendsSearchView.getQuery().toString();
            if (queryString.equals(previousQueryString))
                return;
            if (queryString.length() > 0) {
                if (canQueryForDiscoverySemaphore.tryAcquire()) { // can acquire semaphore
                    /* can make a meaningful query */
                    try {
                        String discoveryEndpoint = getString(R.string.dmfv_discover_friends,
                                getString(R.string.dmfv_host),
                                MainUser.getInstance().get_id().toString(),
                                queryString,
                                getString(R.string.qstring_false));
                        JSONArray possibleFriendsJson = domeafavorClient.getArray(discoveryEndpoint);
                        final ArrayList<User> possibleFriends = new ArrayList<>(possibleFriendsJson.length());
                        for (int i = 0; i < possibleFriendsJson.length(); i++) {
                            possibleFriends.add(new User(possibleFriendsJson.getJSONObject(i)));
                        }
                        /* update the list view on main thread */
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (discoverFriendsSearchView.getQuery().toString().length() == 0) {
                                    /* don't modify anything if the query is blank right now */
                                    return;
                                }
                                friendsDataSource.clear();
                                friendsDataSource.addAll(possibleFriends);
                                notifyDataSetChanged();
                                previousQueryString = queryString;
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle failed discover friends query
                        Log.d(TAG, e.toString());
                    } finally {
                        /* always put back the semaphore */
                        canQueryForDiscoverySemaphore.release();
                    }
                }
            }
        }
    }
}
