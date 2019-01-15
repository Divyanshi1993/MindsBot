package com.referminds.app.chat.Fragment;

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
import android.view.*;
import android.widget.ImageButton;
import com.referminds.app.chat.Activity.MainActivity;
import com.referminds.app.chat.Adapter.MessageAdapter;
import com.referminds.app.chat.Model.Message;
import com.referminds.app.chat.R;
import com.referminds.app.chat.Util.CommonSessionCallbck;
import com.referminds.app.chat.Util.CommonSocketManager;
import com.referminds.app.chat.Util.Utility;
import io.socket.client.Socket;

import java.util.ArrayList;
import java.util.List;

public class ChatrRoomFragment extends Fragment {

    private RecyclerView mMessagesView;
    private AppCompatEditText mInputMessageView;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter;
    private boolean mTyping = false;
    private Socket mSocket;
    private Boolean isConnected = true;
    private String userSocket_id, user_name, mUsername;
    private CommonSocketManager commonSocketManager;
    private Utility utility;
    private CommonSessionCallbck commonSessionCallbck;

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
            commonSessionCallbck = new CommonSessionCallbck();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsername = ((MainActivity) getActivity()).getSession().getUsername();

        String argsString = this.getArguments().getString(getString(R.string.userSocket_id), null);

        userSocket_id = argsString.split(",")[0];
        user_name = argsString.split(",")[1];

        setHasOptionsMenu(true);
        initializeSocket();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(user_name);
    }

    private void initializeSocket() {
        commonSocketManager = new CommonSocketManager((MainActivity) getActivity(), mMessages, mAdapter, mMessagesView, ((MainActivity) getActivity()).getUserList());
        mSocket = ((MainActivity) getActivity()).getSocket();
        // mSocket.on("server message", onNewMessage);
        mSocket.on(getString(R.string.typing), commonSocketManager.onTyping);
        mSocket.on(getString(R.string.get_message), commonSocketManager.onNewMessageArriveRoom);

      /*
        mSocket.on("stop typing", onStopTyping);*/
        mSocket.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chatfrg, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
/*
        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
         mSocket.off("new message", onNewMessage);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);*/
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initializeSocket();

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
                commonSocketManager.sendMsgToOtherUser(mInputMessageView, mSocket, userSocket_id, getActivity());

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
            case R.id.action_favorite:
                utility.showDialog(getActivity(), ((MainActivity) getActivity()).getUserList(), ChatrRoomFragment.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}