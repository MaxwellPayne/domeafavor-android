package edu.indiana.maxandblack.domeafavor.models.chatrooms;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import edu.indiana.maxandblack.domeafavor.R;
import edu.indiana.maxandblack.domeafavor.andrest.AndrestClient;
import edu.indiana.maxandblack.domeafavor.andrest.RESTException;
import edu.indiana.maxandblack.domeafavor.models.ServerEntity;
import edu.indiana.maxandblack.domeafavor.models.datatypes.Oid;
import edu.indiana.maxandblack.domeafavor.models.chatrooms.Message;

/**
 * Created by Max on 4/23/15.
 */
public class Chatroom extends ServerEntity {

    private final String TAG = "Chatroom";
    private ArrayList<Message> messages;

    private final Semaphore canPostMessageSemaphore = new Semaphore(1);
    private final AndrestClient domeafavorClient = new AndrestClient();

    public Message[] getMessages() {
        if (messages == null) {
            return new Message[0];
        }
        Message[] msgArray = new Message[messages.size()];
        for (int i = 0; i < messages.size(); i++) {
            msgArray[i] = messages.get(i);
        }
        return msgArray;
    }


    public void sendMessage(String body, final ChatroomListener listener, final Context context) {
        final Message m = new Message(body);
        if (canPostMessageSemaphore.tryAcquire()) {
            /* attempt to POST the new message */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject messageJson = m.getPOSTJson();
                        String endpoint = context.getString(R.string.dmfv_postmessage,
                                context.getString(R.string.dmfv_host),
                                _id.toString());
                        JSONObject successJson = domeafavorClient.post(endpoint, messageJson);
                        listener.onMessagePostSuccess(new Message(successJson));
                    } catch (RESTException e) {
                        listener.onMessagePostFailure(e);
                    } finally {
                        /* always put back the semaphore */
                        canPostMessageSemaphore.release();
                    }
                }
            }).start();
        } else {
            /* busy, didn't even attempt to post the message */
            listener.onMessagePostNotAttempted();
        }
    }


    @Override
    protected void loadFromJson(JSONObject jsonObject) {
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            try {
                String key = keys.next();
                switch (key) {
                    case "_id":
                        _id = new Oid(jsonObject.getString(key));
                    case "messages":
                        JSONArray messageJsonArray = jsonObject.getJSONArray(key);
                        messages = new ArrayList<>(messageJsonArray.length());
                        for (int i = 0; i < messageJsonArray.length(); i++) {
                            Message m = new Message(messageJsonArray.getJSONObject(i));
                            messages.add(m);
                        }
                }
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    @Override
    public JSONObject getPOSTJson() {
        return null;
    }

    public interface ChatroomListener {
        public void onMessagePostSuccess(Message msg);
        public void onMessagePostFailure(RESTException e);
        public void onMessagePostNotAttempted();
    }
}
