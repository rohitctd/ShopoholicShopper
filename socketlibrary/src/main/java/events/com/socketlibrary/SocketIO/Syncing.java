package events.com.socketlibrary.SocketIO;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.dnitinverma.mylibrary.models.ContactResult;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import events.com.socketlibrary.interfaces.ResponseListener;

public class Syncing {

    private ArrayList<ContactResult> sendingContactList = new ArrayList<>();
    private Socket mSocket;
    private ResponseListener apiResponseListener;
    private String CONTACT_SEND_EVENT,CONTACT_SYNCHED_EVENT,ERROR_EVENT,CONNECT_EVENT,DISCONNECT_EVENT,DELETE_CONTACTS_EVENT, userId;
    private Context mContext;

    public void start(Context context, String url, String CONTACT_SEND_EVENT, String CONTACT_SYNCHED_EVENT, String ERROR_EVENT, String CONNECT_EVENT, String DISCONNECT_EVENT, String DELETE_CONTACTS_EVENT, ResponseListener responseListener, String UserId)
    {
        this.CONTACT_SEND_EVENT = CONTACT_SEND_EVENT;
        this.CONTACT_SYNCHED_EVENT = CONTACT_SYNCHED_EVENT;
        this.ERROR_EVENT = ERROR_EVENT;
        this.DISCONNECT_EVENT=DISCONNECT_EVENT;
        this.CONNECT_EVENT=CONNECT_EVENT;
        this.DELETE_CONTACTS_EVENT=DELETE_CONTACTS_EVENT;
        this.mContext = context;
        this.userId = UserId;
        apiResponseListener = responseListener;
        try {
            IO.Options options=new IO.Options();
            mSocket = IO.socket(url,options);
            mSocket.connect();
            mSocket.on(CONNECT_EVENT,connect_response);
            mSocket.on(this.DISCONNECT_EVENT,disconnect_response);
            mSocket.on(this.ERROR_EVENT,error_response);
        } catch (Exception e) {}
    }

    /**
     * this method is used to sync contacts on server
     * @param contactList
     */
    public void syncContactsOnServer(ArrayList<ContactResult> contactList) {
        JSONArray contacts = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            for (int i = 0; i < contactList.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("name", contactList.get(i).getContactName());
                object.put("mobile", contactList.get(i).getPhoneNumberWithCode());
                object.put("phoneId", contactList.get(i).getRowId());
                object.put("srNumber", contactList.get(i).getContactSrNumber());
                contacts.put(object);
            }
            jsonObject.put("userId", userId);
            jsonObject.put("deviceId", Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
            jsonObject.put("contactList", contacts);

            mSocket.connect();
            if (mSocket.connected()) {
                mSocket.on(CONTACT_SYNCHED_EVENT, contact_sync_response);
                mSocket.emit(CONTACT_SEND_EVENT, jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * this method is used to delete contacts on server
     * @param deletedContacts
     */
    public void deleteContactsFromServer(ArrayList<ContactResult> deletedContacts) {

        JSONArray contacts = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            for (int i = 0; i < deletedContacts.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("srNumber", deletedContacts.get(i).getContactSrNumber());
                contacts.put(object);
            }
            jsonObject.put("contactList", contacts);

            mSocket.connect();
            mSocket.emit(DELETE_CONTACTS_EVENT, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener contact_sync_response = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject obj = (JSONObject) args[0];
            apiResponseListener.contactsSyncResponse(obj,sendingContactList);
        }
    };


    private Emitter.Listener error_response = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
           // JSONArray obj = (JSONArray) args[0];
            apiResponseListener.error("Something went wrong");
        }
    };

    private Emitter.Listener connect_response = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            // JSONArray obj = (JSONArray) args[0];
            Log.i("SOCKET","connect");
        }
    };

    private Emitter.Listener disconnect_response = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            // JSONArray obj = (JSONArray) args[0];
            if (mSocket!=null && mSocket.connected())
                mSocket.disconnect();
            Log.i("SOCKET","disconnect");
        }
    };

}
