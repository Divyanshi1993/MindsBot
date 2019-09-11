package com.referminds.app.chat.view.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.referminds.app.chat.ChatApplication;
import com.referminds.app.chat.R;
import com.referminds.app.chat.data.Model.SessionManager;
import com.referminds.app.chat.view.Fragment.ChatBoatFragment;
import com.referminds.app.chat.view.Fragment.ChatRoomFragment;
import com.referminds.app.chat.view.Utils.CommonSocketManager;

import java.util.HashMap;
import java.util.Map;

import io.socket.client.Socket;


public class MainActivity extends AppCompatActivity implements ChatBoatFragment.MainListner {
    Map<String, String> userSockets;
    private Socket mSocket;
    private SessionManager session;
    private String userName;
    private ChatApplication app;
    private CommonSocketManager commonSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        //check user logged in or not
        if (session.checkLogin()) {
            Log.e("check login", session.checkLogin() + "");
            setContentView(R.layout.activity_main);

            userSockets = new HashMap<>();

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            // Sets the Toolbar to act as the ActionBar for this Activity window.
            // Make sure the toolbar exists in the activity and is not null
            setSupportActionBar(toolbar);
            // get username
            userName = getSession().getUserDetails().get(SessionManager.KEY_NAME);
            Log.e("activity_state", "activity creaated");
            initializeSocket();
            splashScreen();

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null){
        mSocket.off(Socket.EVENT_CONNECT);
        mSocket.off(Socket.EVENT_DISCONNECT);
        mSocket.off(Socket.EVENT_CONNECT_ERROR);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT);
        mSocket.off(getString(R.string.userlist));
        mSocket.off(getString(R.string.get_message));
        mSocket.disconnect();}
    }

    private void splashScreen() {
        Intent splashIntent = new Intent(this, SplashScreen.class);
        startActivity(splashIntent);
        createFragment(new ChatBoatFragment(), getString(R.string.chatbot));
    }

    @Override
    public SessionManager getSession() {
        return session;
    }

    private void initializeSocket() {
        commonSocketManager = new CommonSocketManager(this, userSockets);
        app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, commonSocketManager.onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, commonSocketManager.onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, commonSocketManager.onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, commonSocketManager.onConnectError);
        mSocket.on(getString(R.string.userlist), commonSocketManager.onUpdateuUerlist);
        mSocket.on(getString(R.string.get_message), commonSocketManager.onNewMessageArriveRoom);
        mSocket.connect();
    }

    public void onUserConnected() {
        if (userName != null) {
            mSocket.emit(getString(R.string.connect_user), userName);
        }
    }


    public void createSession(String name, String socketid) {
        session.createLoginSession(name, socketid);
        Log.e("fragment", "fragment creaated");
    }

    @Override
    public Map<String, String> getUserList() {
        return userSockets;
    }

    @Override
    public Socket getSocket() {
        return mSocket;
    }

    public void createFragment(Fragment fragment, String tag) {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.myframe, fragment, tag).
                commit();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof ChatBoatFragment) {
            ChatBoatFragment chatBoatFragment = (ChatBoatFragment) fragment;
            chatBoatFragment.setonMainCall(this);
        } else if (fragment instanceof ChatRoomFragment) {
        }
    }

    public void onNewMsgArrive(final Object... response) {
        ChatRoomFragment chatrRoomFragment = (ChatRoomFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.chatroom));
        if (chatrRoomFragment != null && chatrRoomFragment.isVisible()) {
            chatrRoomFragment.onNewMsgArrive(response);
        }

    }
}