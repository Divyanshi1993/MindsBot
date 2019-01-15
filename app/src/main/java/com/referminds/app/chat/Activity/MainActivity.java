package com.referminds.app.chat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.referminds.app.chat.Fragment.ChatBoatFragment;
import com.referminds.app.chat.Model.SessionManager;
import com.referminds.app.chat.Model.User;
import com.referminds.app.chat.R;
import com.referminds.app.chat.Singelton.ChatApplication;
import com.referminds.app.chat.Util.CommonSocketManager;
import io.socket.client.Socket;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ArrayList<User> userlist;
    private Socket mSocket;
    private SessionManager session;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        session = new SessionManager(this);
        //check user logged in or not?
        if (session.checkLogin()) {
            setContentView(R.layout.activity_main);
            userlist = new ArrayList();

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

    private void splashScreen() {
        Intent splashIntent = new Intent(this, SplashScreen.class);
        startActivity(splashIntent);
    }

    @Override
    protected void onDestroy() {
        Log.e("activity_state", "activity destroyed");
        super.onDestroy();
    }

    public SessionManager getSession() {
        return session;
    }

    private void initializeSocket() {
        CommonSocketManager commonSocketManager = new CommonSocketManager(this, userlist);
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, commonSocketManager.onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, commonSocketManager.onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, commonSocketManager.onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, commonSocketManager.onConnectError);
        mSocket.on(getString(R.string.userlist), commonSocketManager.onUpdateuUerlist);
        mSocket.connect();
        mSocket.emit(getString(R.string.connect_user), userName);

    }


    public void createSession(String name, String socketid) {
        session.createLoginSession(name, socketid);
        createFragment(new ChatBoatFragment(), getString(R.string.chatbot));
        Log.e("fragment", "fragment creaated");
    }

    public ArrayList<User> getUserList() {
        return userlist;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public void createFragment(Fragment fragment, String tag) {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.myframe, fragment, tag).
                commit();
    }
}
