package edu.indiana.maxandblack.domeafavor.activities.selectfriends;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashSet;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.activities.createoddjob.CreateOddjobActivity;
import edu.indiana.maxandblack.domeafavor.models.users.MainUser;
import edu.indiana.maxandblack.domeafavor.models.users.User;

public class SelectFriendsActivity extends ActionBarActivity {

    private HashSet<User> selectedFriendSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);

        /* populate the list view */
        ListView friendsListView = (ListView) findViewById(R.id.selectFriendsListView);
        ArrayList<User> clonedFriends = new ArrayList<>(MainUser.getInstance().getFriends());
        friendsListView.setAdapter(new SelectFriendAdapter(this, clonedFriends));

        ArrayList<String> authorizedIds = getIntent()
                .getStringArrayListExtra(getString(R.string.intent_key_friend_ids));
        /* check the boxes of already authorized friends */
        for (String friendId : authorizedIds) {
            selectedFriendSet.add(MainUser.getInstance().getFriend(friendId));
        }

        Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectFriendResult = new Intent();
                ArrayList<String> friendIds = new ArrayList<String>(selectedFriendSet.size());
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
            /* will be checked if friendInFocus is a member of the hashSet */
            isSelectedCheckBox.setChecked(selectedFriendSet.contains(friendInFocus));
            /* attach user data to this view through its tag */
            v.setTag(friendInFocus);
            v.setOnClickListener(listItemClickListener);
            return v;
        }
    }

    private View.OnClickListener listItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            User clickedUser = (User) v.getTag();
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
    };
}
