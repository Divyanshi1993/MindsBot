package com.referminds.app.chat.Util;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.referminds.app.chat.Adapter.UserlistAdapter;
import com.referminds.app.chat.R;

import java.util.ArrayList;

public class Utility {
    //private  static  Dialog dialog;
    private static AlertDialog b;

    public void showDialog(FragmentActivity activity, ArrayList userlist, Fragment fragment) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View customTitle = inflater.inflate(R.layout.customtitlebar, null);
        final View dialogView = inflater.inflate(R.layout.userlist, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCustomTitle(customTitle);
        b = dialogBuilder.create();
        if (userlist.size() > 0) {
            UserlistAdapter userlistAdapter = new UserlistAdapter(activity, userlist, fragment);

            RecyclerView userlistview = (RecyclerView) dialogView.findViewById(R.id.userlistview);
            userlistview.setLayoutManager(new LinearLayoutManager(activity));
            userlistview.setAdapter(userlistAdapter);

            b.show();
        } else {
            Toast.makeText(activity, "No User Found", Toast.LENGTH_LONG).show();
        }

    }

    public void dissmissDialog() {
        b.dismiss();
    }

    public void showSnackbar(AppCompatActivity activity, String msg) {
        Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT)
                .setActionTextColor(Color.RED)
                .show();
    }


    public void signinbuttonState(Button signUpButton, boolean b, int color) {
        signUpButton.setClickable(b);
        signUpButton.setBackgroundColor(color);
    }

}
