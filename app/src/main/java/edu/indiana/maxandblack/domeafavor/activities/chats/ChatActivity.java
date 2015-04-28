package edu.indiana.maxandblack.domeafavor.activities.chats;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.lcsky.SVProgressHUD;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.andrest.AndrestClient;
import edu.indiana.maxandblack.domeafavor.andrest.RESTException;
import edu.indiana.maxandblack.domeafavor.models.chatrooms.Chatroom;
import edu.indiana.maxandblack.domeafavor.models.chatrooms.Message;
import edu.indiana.maxandblack.domeafavor.models.oddjobs.Oddjob;

public class ChatActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String TAG = "ChatActivity";

    private Oddjob oddjob;
    private final AndrestClient domeafavorClient = new AndrestClient();
    //private final Semaphore canSubmitMessageSemaphore = new Semaphore(1);
    private MessageAdapter listAdapter;

    private TextView chatOddjobTitle;
    private ListView chatMessageListView;
    private EditText chatMessageEditText;
    private Button chatButtonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        oddjob = getIntent().getParcelableExtra(Oddjob.MAIN_SERIALIZED_ODDJOB_KEY);

        chatOddjobTitle = (TextView) findViewById(R.id.chatOddjobTitle);
        chatMessageListView = (ListView) findViewById(R.id.chatMessageListView);
        chatMessageEditText = (EditText) findViewById(R.id.chatMessageEditText);
        chatButtonSubmit = (Button) findViewById(R.id.chatButtonSubmit);

        chatButtonSubmit.setOnClickListener(this);

        /* tie up the UI */
        SVProgressHUD.showInView(this, getString(R.string.svprogress_default), true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        /* query for the chatroom */
        new Thread(new Runnable() {
            @Override
            public void run() {
                String getChatroomEndpoint = getString(R.string.dmfv_get_chatroom,
                        getString(R.string.dmfv_host),
                        oddjob.get_id());
                try {
                    final JSONObject chatroomJson = domeafavorClient.get(getChatroomEndpoint);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            oddjob.setChatroom(new Chatroom(chatroomJson));

                            chatOddjobTitle.setText(oddjob.getTitle());
                            listAdapter = new MessageAdapter(ChatActivity.this);
                            chatMessageListView.setAdapter(listAdapter);
                            chatMessageListView.setSelection(listAdapter.getCount() - 1);
                        }
                    });
                } catch (RESTException e) {
                    Toast.makeText(ChatActivity.this, "Oh no, couldn't get the chatroom", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                } finally {
                    /* always free up the UI */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SVProgressHUD.dismiss(ChatActivity.this);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    });
                }
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

    protected void updateChatroom(Chatroom newChatroom) {
        ArrayList<Message> oldMessages = oddjob.getChatroom().getMessages();
        ArrayList<Message> newMessages = newChatroom.getMessages();
        oddjob.setChatroom(newChatroom);
        /* calculate how many new messages there were */
        int howManyNewMessages = newMessages.size() - oldMessages.size();

        ArrayList<Message> addedMessages = new ArrayList<>(howManyNewMessages);
        /* extract the new, differing messages */
        for (int i = howManyNewMessages; i < newMessages.size(); i++) {
            addedMessages.add(newMessages.get(i));
        }
        /* add new messages to adapter and notify */
        listAdapter.addAll(addedMessages);
        chatMessageListView.setSelection(listAdapter.getCount() - 1);
    }

    private class MessageAdapter extends ArrayAdapter<Message> {

        public MessageAdapter(Context context) {
            super(context, R.layout.chatmessage_list_item, oddjob.getChatroom().getMessages());
        }

        @Override
        public int getCount() {
            return oddjob.getChatroom().getMessages().size();
        }

        @Override
        public Message getItem(int position) {
            return oddjob.getChatroom().getMessages().get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View messageView = convertView;

            if (messageView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                messageView = inflater.inflate(R.layout.chatmessage_list_item, parent, false);
            }

            TextView chatmessageTextView = (TextView) messageView.findViewById(R.id.chatmessageTextView);
            Message msg = getItem(position);

            chatmessageTextView.setText(msg.getBody());
            boolean isMine = msg.isMine();
            chatmessageTextView.setBackgroundResource( (isMine) ? R.drawable.bubble_green : R.drawable.bubble_yellow );
            chatmessageTextView.setGravity( (isMine) ? Gravity.RIGHT : Gravity.LEFT);
            return messageView;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == chatButtonSubmit) {
            String body = chatMessageEditText.getText().toString();
            if (body.length() == 0) {
                /* ensure that body is not blank */
                Toast.makeText(this, "Cannot post blank message", Toast.LENGTH_SHORT).show();
                return;
            }
            chatButtonSubmit.setEnabled(false);
            chatMessageEditText.setEnabled(false);
            final Message message = new Message(body);

            /* post the new Message async */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Chatroom updatedChatroom = null;
                    String postMessageEndpoint = getString(R.string.dmfv_postmessage,
                            getString(R.string.dmfv_host),
                            oddjob.get_id());
                    try {
                        JSONObject postResponse = domeafavorClient.post(postMessageEndpoint,
                                message.getPOSTJson());
                        /* create a new Message based on the response */
                        updatedChatroom = new Chatroom(postResponse);
                    } catch (RESTException e) {
                        Log.d(TAG, e.toString());
                    } finally {
                        /* make above variables final so they can be used on UI thread */
                        final Chatroom finallyUpdatedChatroom = updatedChatroom;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /* always re-enable the text submission widgets */
                                chatButtonSubmit.setEnabled(true);
                                chatMessageEditText.setEnabled(true);
                                if (finallyUpdatedChatroom != null) {
                                    /* success! */
                                    updateChatroom(finallyUpdatedChatroom);
                                     /* clear the EditText on success */
                                    chatMessageEditText.setText("");
                                } else {
                                    /* failed */
                                    Toast.makeText(ChatActivity.this, "Error posting message", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
                    }
                }
            }).start();
        }
    }
}
