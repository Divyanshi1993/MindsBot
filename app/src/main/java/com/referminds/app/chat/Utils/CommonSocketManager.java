package com.referminds.app.chat.Utils;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.EditText;
import com.google.gson.Gson;
import com.referminds.app.chat.Activity.MainActivity;
import com.referminds.app.chat.Model.Message;
import com.referminds.app.chat.Model.ServerMessage;
import com.referminds.app.chat.Model.SessionManager;
import com.referminds.app.chat.Model.User;
import com.referminds.app.chat.R;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.util.ArrayList;
import java.util.List;

public class CommonSocketManager {
    SessionManager sessionManager;
    CommonSessionCallbck commonSessionCallbck;
    private AppCompatActivity mContext;
    public Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String username;
                    username = args[0].toString();
                    //CommonListenerManager.removeTyping(username,mMessages);
                }
            });
        }
    };
    private CommonListenerManager commonListenerManager;
    public Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (mContext != null)
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String username;
                        String message;

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        message = args[0].toString();
                        commonListenerManager.addMessage(sessionManager.getUsername(), message, false);
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
                        ServerMessage userMessage = new Gson().fromJson(response[0].toString(), ServerMessage.class);
                        String from_message = userMessage.getMessage();
                        String fromSocketid = userMessage.getFromId();
                        String username = "";

                        commonListenerManager.addMessage("", userMessage.getMessage(), false);
                        //removeTyping(username);
                    }
                });
        }
    };
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mMessagesView;
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
    private List<Message> mMessages;
    private ArrayList<User> userlist;
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
    private Utility utility;
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
                        commonSessionCallbck.signout(mContext, sessionManager.getUsername());

                    }
                });
        }
    };
    private Boolean isConnected = true;
    public Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mContext != null) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isConnected) {
                            if (null != sessionManager.getUsername())
                                utility.showSnackbar(mContext, mContext.getString(R.string.connect));
                            isConnected = true;
                        }
                    }
                });
            }
        }
    };
    private Boolean mTyping = true;
    public CommonSocketManager() {

    }
    public CommonSocketManager(AppCompatActivity activity, ArrayList<User> userlist) {
        this();
        mContext = activity;
        this.userlist = userlist;
        commonListenerManager = new CommonListenerManager();
        sessionManager = ((MainActivity) mContext).getSession();
        utility = new Utility();
        commonSessionCallbck = new CommonSessionCallbck();

    }
    public CommonSocketManager(AppCompatActivity activity, List<Message> mMessages, RecyclerView.Adapter mAdapter, RecyclerView mMessagesView, ArrayList<User> userlist) {
        this();
        mContext = activity;
        this.mAdapter = mAdapter;
        this.mMessages = mMessages;
        this.mMessagesView = mMessagesView;
        this.userlist = userlist;
        commonListenerManager = new CommonListenerManager(mContext, mMessages, mAdapter, mMessagesView);
        utility = new Utility();
        sessionManager = ((MainActivity) mContext).getSession();
        commonSessionCallbck = new CommonSessionCallbck();
    }

    public void attemptSend(Socket mSocket, EditText mInputMessageView, FragmentActivity activity) {
        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }
        mInputMessageView.setText("");
commonListenerManager.addMessage(sessionManager.getUsername(),message,true);
        // perform the sending message attempt.
        mSocket.emit(mContext.getString(R.string.client_message), message, sessionManager.getSoketId());
        mTyping = false;

    }

    public void sendMsgToOtherUser(EditText mInputMessageView, Socket mSocket, String userSocket_id, FragmentActivity activity) {
        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }
        mInputMessageView.setText("");

        // perform the sending message attempt.
        commonListenerManager.sendMsgToOtherUser(sessionManager.getSoketId(), userSocket_id, message, mSocket);
        mTyping = false;

    }
}
