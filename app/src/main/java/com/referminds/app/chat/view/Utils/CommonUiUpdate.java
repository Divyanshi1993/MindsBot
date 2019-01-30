package com.referminds.app.chat.view.Utils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;
import com.referminds.app.chat.R;
import com.referminds.app.chat.data.Model.Conversation;
import com.referminds.app.chat.data.Model.Message;
import com.referminds.app.chat.data.Model.ServerMessage;
import com.referminds.app.chat.data.Model.User;
import com.referminds.app.chat.data.Repository.RealmDB;
import com.referminds.app.chat.view.Activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.Socket;

public class CommonUiUpdate {
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mMessagesView;
    private List<Message> mMessages;
    private Context context;
    private String prefUserName;

    public CommonUiUpdate(){}
    public CommonUiUpdate(AppCompatActivity mContext) {
        this();
        prefUserName = ((MainActivity) mContext).getSession().getUsername();
    }

    public CommonUiUpdate(Context mContext, List<Message> mMessages, RecyclerView.Adapter mAdapter, RecyclerView mMessagesView) {
        this();
        this.context = mContext;
        this.mAdapter = mAdapter;
        this.mMessages = mMessages;
        this.mMessagesView = mMessagesView;
        prefUserName = ((MainActivity) mContext).getSession().getUsername();
    }
    public void loadConversation(Conversation conversation) {
        for(Message message : conversation.getMessageList()){
        if (message.getUsername().equals(prefUserName)) {
            mMessages.add(new Message.Builder(Message.TYPE_My_MESSAGE)
                    .username(message.getUsername()).message(message.getMessage()).build());
            mAdapter.notifyItemInserted(mMessages.size() - 1);
        } else {
            mMessages.add(new Message.Builder(Message.TYPE_OTHER_MESSGE)
                    .username(message.getUsername()).message(message.getMessage()).build());
            mAdapter.notifyItemInserted(mMessages.size() - 1);
        }
        scrollToBottom(mMessagesView);}
    }

    public void addMessage(String username, String message, Boolean isMyMessage) {
        if (isMyMessage) {
            mMessages.add(new Message.Builder(Message.TYPE_My_MESSAGE)
                    .username(username).message(message).build());
            mAdapter.notifyItemInserted(mMessages.size() - 1);
        } else if (!isMyMessage) {
            mMessages.add(new Message.Builder(Message.TYPE_OTHER_MESSGE)
                    .username(username).message(message).build());
            mAdapter.notifyItemInserted(mMessages.size() - 1);
        }
        scrollToBottom(mMessagesView);
    }

    private void scrollToBottom(RecyclerView mMessagesView) {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    public void UpdateUserlist(ArrayList<User> userlist, AppCompatActivity mContext, Object... response) {
        userlist.clear();
        try {
            if (response[1].toString() != null && response[0].toString() != null) {
                // String prefSocketId = ((MainActivity) mContext).getSession().getSoketId();
                String prefSocketId = null;

                //String mSocketId = prefSocketId == null ? response[1].toString() : prefSocketId;
                String usersJSON = response[0].toString();
                Gson gson = new Gson();
                User users[] = gson.fromJson(usersJSON, User[].class);
                for (User user : users) {
                    if (!user.getName().equals(prefUserName)) {
                        userlist.add(user);
                    } else {
                        prefSocketId = user.getSoketId();
                    }
                }
                ((MainActivity) mContext).createSession(prefUserName, prefSocketId);
                Log.e(mContext.getString(R.string.socket_id), prefSocketId);
            }


        } catch (Exception e) {
            Log.e("exception", e.getMessage());
        }

    }

    public void addTyping(String username, RecyclerView mMessagesView) {
        mMessages.add(new Message.Builder(Message.TYPE_ACTION)
                .username(username).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        // scrollToBottom(mMessagesView);
    }


    public void removeTyping(String username, List<Message> mMessages) {
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            Message message = mMessages.get(i);
            if (message.getType() == Message.TYPE_ACTION && message.getUsername().equals(username)) {
                mMessages.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
    }

    public void sendMsgToOtherUser(String conv_name,String fromId, String toId, String msg, Socket mSocket) {
        ServerMessage userMessage = new ServerMessage(fromId, toId, msg);
        Gson gson = new Gson();
        mSocket.emit(context.getString(R.string.send_message), gson.toJson(userMessage));
        String username =((MainActivity) context).getSession().getUsername();

        addMessage(username, msg, true);

        updateMsgToRealm(conv_name,username,msg);


    }
    public void updateMsgToRealm(String conv_name ,String username ,String msg){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String millisInString  = dateFormat.format(new Date());
        RealmDB realmDB = new RealmDB();
        realmDB.updateConversation(conv_name,username,msg,millisInString);
    }
}
