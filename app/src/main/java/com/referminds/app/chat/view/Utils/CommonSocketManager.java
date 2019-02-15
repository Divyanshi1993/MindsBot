package com.referminds.app.chat.view.Utils;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.gson.Gson;
import com.referminds.app.chat.R;
import com.referminds.app.chat.data.Model.Conversation;
import com.referminds.app.chat.data.Model.Message;
import com.referminds.app.chat.data.Model.ServerMessage;
import com.referminds.app.chat.data.Model.SessionManager;
import com.referminds.app.chat.data.Model.User;
import com.referminds.app.chat.view.Activity.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class CommonSocketManager {
    private SessionManager sessionManager;
    private CommonSessionCall commonSessionCallbck;
    private AppCompatActivity mContext;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mMessagesView;
    private Utility utility;
    private List<Message> mMessages;
    private Map<String, String> userlist;
    private String conv_name;
    private CommonUiUpdate commonListenerManager;

    public CommonSocketManager() {
        utility = new Utility();
        commonSessionCallbck = new CommonSessionCall();
    }

    public CommonSocketManager(AppCompatActivity activity, Map<String, String> userlist) {
        this();
        mContext = activity;
        this.userlist = userlist;
        commonListenerManager = new CommonUiUpdate(mContext);
        sessionManager = ((MainActivity) mContext).getSession();
    }

    public CommonSocketManager(AppCompatActivity activity, List<Message> mMessages, RecyclerView.Adapter mAdapter, RecyclerView mMessagesView, Map<String, String> userlist, String conv_name) {
        this();
        mContext = activity;
        this.mAdapter = mAdapter;
        this.mMessages = mMessages;
        this.mMessagesView = mMessagesView;
        this.userlist = userlist;
        this.conv_name = conv_name;
        commonListenerManager = new CommonUiUpdate(mContext, mMessages, mAdapter, mMessagesView);
        sessionManager = ((MainActivity) mContext).getSession();
    }

    public void loadConversation(Conversation conversation) {
        commonListenerManager.loadConversation(conversation);
    }

    public Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String username = args[0].toString();
                    //CommonUiUpdate.removeTyping(username,mMessages);
                }
            });
        }
    };

    public Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (mContext != null)
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String message;
                        message = args[0].toString();
                        commonListenerManager.addMessage(sessionManager.getUsername(), message, false);
                        commonListenerManager.updateMsgToRealm("chatbot", "chatbot", message);
                        //removeTyping(username);
                    }
                });
        }
    };
    public Emitter.Listener onNewMessageArriveRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... response) {
            if (mContext != null)
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity)mContext).onNewMsgArrive(response);
                      /*  ServerMessage resp = new Gson().fromJson(response[0].toString(), ServerMessage.class);
                        String from_message = resp.getMessage();
                        String user_socketId = resp.getFromId();

                      if(userlist.get(conv_name).equals(user_socketId)){
                        commonListenerManager.addMessage(conv_name, from_message, false);
                        commonListenerManager.updateMsgToRealm(conv_name, conv_name, from_message);}
                        //removeTyping(username);*/
                   }
                });
        }
    };

    public Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (mContext != null)
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String username;
                        username = args[0].toString();
                        if (!username.equals(null) && !username.equals(sessionManager.getUsername()))
                            commonListenerManager.addTyping(username, mMessagesView);
                    }
                });
        }
    };

    public Emitter.Listener onUpdateuUerlist = new Emitter.Listener() {
        @Override
        public void call(final Object... response) {
            if (mContext != null)
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commonListenerManager.UpdateUserlist(userlist, mContext, response);
                    }
                });
        }
    };

    public Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mContext != null)
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utility.showSnackbar(mContext, mContext.getString(R.string.disconnect));


                    }
                });
        }
    };
    public Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mContext != null)
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utility.showSnackbar(mContext, mContext.getString(R.string.error_connect));
                        //  commonSessionCallbck.signout(mContext, sessionManager.getUsername());

                    }
                });
        }
    };

    public Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mContext != null) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                            if (null != sessionManager.getUsername())
                                ((MainActivity)mContext).onUserConnected();
                                utility.showSnackbar(mContext, mContext.getString(R.string.connect));

                    }
                });
            }
        }
    };

    public void attemptSend(Socket mSocket, EditText mInputMessageView) {
        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }
        mInputMessageView.setText("");
        String username = sessionManager.getUsername();

        commonListenerManager.addMessage(username, message, true);
        // perform the sending message attempt.
        mSocket.emit(mContext.getString(R.string.client_message), message, sessionManager.getSoketId());
        commonListenerManager.updateMsgToRealm("chatbot", username, message);
        // mTyping = false;
    }

    public void sendMsgToOtherUser(String message, Socket mSocket, String userSocket_id, String conv_name) {

        if (userSocket_id != null){
            // perform the sending message to other user.
            if(sessionManager.getSoketId()!= null){
            commonListenerManager.sendMsgToOtherUser(conv_name, sessionManager.getSoketId(), userSocket_id, message, mSocket);}}
        else {
            // user is offline
            utility.showSnackbar(mContext, mContext.getString(R.string.offline_user));
        }

        // mTyping = false;

    }
}
