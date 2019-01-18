package com.referminds.app.chat.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.referminds.app.chat.Activity.MainActivity;
import com.referminds.app.chat.Fragment.ChatrRoomFragment;
import com.referminds.app.chat.Model.User;
import com.referminds.app.chat.R;
import com.referminds.app.chat.Utils.Utility;

import java.util.List;

public class UserlistAdapter extends RecyclerView.Adapter<UserlistAdapter.ViewHolder> {
    private List<User> mUsers;
    private Context context;
    private Fragment fragment;
    private Utility utility;

    public UserlistAdapter(Context context, List<User> userlist, Fragment fragment) {
        mUsers = userlist;
        this.context = context;
        this.fragment = fragment;
        utility = new Utility();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.item_userlist, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final String username = mUsers.get(i).getName();
        final String SocketId = mUsers.get(i).getSoketId();
        viewHolder.mUsernameView.setText(username);
        viewHolder.mCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatrRoomFragment chatrRoomFragment = new ChatrRoomFragment();

                Bundle args = new Bundle();
                args.putString(context.getString(R.string.userSocket_id), SocketId + "," + username);

                chatrRoomFragment.setArguments(args);
                ((MainActivity) context).createFragment(chatrRoomFragment, context.getString(R.string.chatroom));
                utility.dissmissDialog();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView;
        private CardView mCardview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mUsernameView = (TextView) itemView.findViewById(R.id.usernameview);
            mCardview = itemView.findViewById(R.id.cardview_userlist);
        }
    }
}
