package com.referminds.app.chat.view.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.referminds.app.chat.R;
import com.referminds.app.chat.data.Model.Message;
import com.referminds.app.chat.data.Model.SessionManager;
import com.referminds.app.chat.data.Model.User;
import com.referminds.app.chat.data.Repository.RealmDB;
import com.referminds.app.chat.view.Activity.LoginActivity;
import com.referminds.app.chat.view.Activity.MainActivity;
import com.referminds.app.chat.view.Adapter.MessageAdapter;
import com.referminds.app.chat.view.Utils.CommonSessionCall;
import com.referminds.app.chat.view.Utils.CommonSocketManager;
import com.referminds.app.chat.view.Utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;


/**
 * A chat fragment containing messages view and input form.
 */
public class ChatBoatFragment extends Fragment implements View.OnClickListener {
    private RecyclerView mMessagesView;
    private AppCompatEditText mInputMessageView;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter;
    private boolean mTyping = false;
    private String mUsername;
    private Socket mSocket;
    private CommonSocketManager commonSocketManager;
    private Utility utility;
    private Context mContext;

    public ChatBoatFragment() {
        super();
    }

    private MainListner mCallback;

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mContext = context;
            mAdapter = new MessageAdapter(context, mMessages);
            utility = new Utility();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mUsername = mCallback.getSession().getUsername();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.chatbot));

    }

    private void initializeSocket() {
        commonSocketManager = new CommonSocketManager((MainActivity) getActivity(), mMessages, mAdapter, mMessagesView, mCallback.getUserList(), "chatbot");
        mSocket = mCallback.getSocket();
        mSocket.on(getString(R.string.server_message), commonSocketManager.onNewMessage);
       /* mSocket.on(getString(R.string.typing), commonSocketManager.onTyping);
        mSocket.on("stop typing", onStopTyping);*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chatfrg, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initializeSocket();
        RealmDB db = new RealmDB();
        db.readConversation("chatbot", commonSocketManager);

    }

    private void initView(View view) {
        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);

        mInputMessageView = (AppCompatEditText) view.findViewById(R.id.message_input);
        mInputMessageView.setOnClickListener(this);
        mInputMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == mUsername) return;
                if (!mSocket.connected()) return;
                if (!mTyping) {
                    mTyping = true;
                    mSocket.emit(getString(R.string.typing), mUsername);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_leave:
                new CommonSessionCall().signout(getActivity(), mUsername);
                break;
            case R.id.action_add_user:
                utility.showDialog(getActivity(), mCallback.getUserList(), ChatBoatFragment.this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_input:
                break;
            case R.id.send_button:
                commonSocketManager.attemptSend(mSocket, mInputMessageView);
                break;
        }
    }

    public void setonMainCall(MainActivity activity) {
        mCallback = activity;
    }

    // Container Activity must implement this interface
    public interface MainListner {
        public SessionManager getSession();
        public Map<String, String> getUserList();
        public Socket getSocket();
    }
}

