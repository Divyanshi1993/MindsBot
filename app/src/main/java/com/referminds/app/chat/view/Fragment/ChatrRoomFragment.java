package com.referminds.app.chat.view.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import com.referminds.app.chat.data.Repository.RealmDB;
import com.referminds.app.chat.view.Activity.MainActivity;
import com.referminds.app.chat.view.Adapter.MessageAdapter;
import com.referminds.app.chat.view.Utils.CommonSessionCall;
import com.referminds.app.chat.view.Utils.CommonSocketManager;
import com.referminds.app.chat.view.Utils.Utility;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

public class ChatrRoomFragment extends Fragment {

    private RecyclerView mMessagesView;
    private AppCompatEditText mInputMessageView;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter;
    private boolean mTyping = false;
    private Socket mSocket;
    private Boolean isConnected = true;
    private String userSocket_id, mUsername;
    private String conv_name;
    private CommonSocketManager commonSocketManager;
    private Utility utility;
    private CommonSessionCall commonSessionCallbck;

    public ChatrRoomFragment() {
        super();
    }


    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //  mAdapter = new MessageAdapter(context, mMessages);
        if (context instanceof Activity) {
            mAdapter = new MessageAdapter(context, mMessages);
            utility = new Utility();
            commonSessionCallbck = new CommonSessionCall();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsername = ((MainActivity) getActivity()).getSession().getUsername();

        String argsString = this.getArguments().getString(getString(R.string.userSocket_id), null);

        userSocket_id = argsString.split(",")[0];
        conv_name = argsString.split(",")[1];

        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(conv_name);

    }

    private void initializeSocket() {
        commonSocketManager = new CommonSocketManager((MainActivity) getActivity(), mMessages, mAdapter, mMessagesView, ((MainActivity) getActivity()).getUserList(),conv_name);
        mSocket = ((MainActivity) getActivity()).getSocket();
        // mSocket.on("server message", onNewMessage);
        mSocket.on(getString(R.string.typing), commonSocketManager.onTyping);
        mSocket.on(getString(R.string.get_message), commonSocketManager.onNewMessageArriveRoom);

      /*
        mSocket.on("stop typing", onStopTyping);*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chatfrg, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initializeSocket();
        RealmDB db = new RealmDB();
        db.readConversation(conv_name,commonSocketManager);
    }

    private void initView(View view) {
        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);

        mInputMessageView = (AppCompatEditText) view.findViewById(R.id.message_input);
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
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonSocketManager.sendMsgToOtherUser(mInputMessageView, mSocket, userSocket_id,conv_name);

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_chatroom, menu);
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
                commonSessionCallbck.signout(getActivity(), mUsername);
                break;
            case R.id.action_chatbot:
                ((MainActivity) getActivity()).createFragment(new ChatBoatFragment(), getString(R.string.chatbot));
                break;
            case R.id.action_add_user:
                utility.showDialog(getActivity(), ((MainActivity) getActivity()).getUserList(), ChatrRoomFragment.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}