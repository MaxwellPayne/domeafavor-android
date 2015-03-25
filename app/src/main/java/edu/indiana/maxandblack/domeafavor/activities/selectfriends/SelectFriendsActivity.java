package edu.indiana.maxandblack.domeafavor.activities.selectfriends;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.models.users.MainUser;
import edu.indiana.maxandblack.domeafavor.models.users.User;

public class SelectFriendsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);
        ListView friendsListView = (ListView) findViewById(R.id.selectFriendsListView);
        ArrayList<User> clonedFriends = new ArrayList<>(MainUser.getInstance().getFriends());
        friendsListView.setAdapter(new SelectFriendAdapter(this, clonedFriends));
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

            realNameTextView.setText(String.format("%s %s", friendInFocus.getFirstName(), friendInFocus.getLastName()));
            usernameTextView.setText(friendInFocus.getUsername());
            return v;
        }
    }

    public interface SelectFriendsActivityListener {
        public void didCancelSelectingFriends();
        public void didSelectFriends(ArrayList<User> selectedFriends);
    }
}
