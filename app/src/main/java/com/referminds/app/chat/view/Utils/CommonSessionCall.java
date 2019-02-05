package com.referminds.app.chat.view.Utils;

import android.arch.lifecycle.ViewModel;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.referminds.app.chat.ChatApplication;
import com.referminds.app.chat.R;
import com.referminds.app.chat.data.Controller.RestController;
import com.referminds.app.chat.data.Model.User;
import com.referminds.app.chat.view.Activity.MainActivity;
import com.referminds.app.chat.viewmodel.LoginViewModel;
import com.referminds.app.chat.viewmodel.RegistrationViewModel;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CommonSessionCall {
    private AppCompatActivity activity;
    private Utility utility;
    private ViewModel viewModel;

    public CommonSessionCall() {
        utility = new Utility();
    }
    public CommonSessionCall(ViewModel viewModel , AppCompatActivity activity) {
        this();
        this.viewModel = viewModel;
        this.activity = activity;
    }
    public CommonSessionCall(AppCompatActivity activity) {
        this();
        this.activity = activity;
    }

    public void signout(final FragmentActivity activity, String username) {
        RestController.UserService mAPIService = ChatApplication.getClient(Constants.CHAT_SERVER_URL).create(RestController.UserService.class);
        mAPIService.signout(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 404 || response.body() == null) {
                    Log.e("logout", "Logout Failed");
                } else {
                    Log.e("logout", response.body().toString());
                    MainActivity act = ((MainActivity) activity);
                    act.getSocket().disconnect();
                    act.getSession().logoutUser();

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("retrofitEeption", t.toString());
                if (t instanceof ConnectException || t instanceof SocketTimeoutException) {
                    Log.e("logout", "Logout Failed");
                }
            }
        });

    }

    public void signIn(User user) {
        RestController.UserService mAPIService = ChatApplication.getClient(Constants.CHAT_SERVER_URL).create(RestController.UserService.class);
        mAPIService.signin(user.getName(), user.getPwd()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 404 || response.body() == null) {
                    ((LoginViewModel)viewModel).onAuthenticationFailed();
                    utility.showSnackbar(activity, activity.getString(R.string.wrong_username_password));
                } else if (response.code() == 201) {
                    utility.showSnackbar(activity, activity.getString(R.string.user_already_exixt));

                } else {
                    Log.e("print", response.body().toString());
                    ((LoginViewModel)viewModel).onAuthenticated();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("retrofitEeption", t.toString());
                if (t instanceof ConnectException || t instanceof SocketTimeoutException) {
                    utility.showSnackbar(activity, activity.getString(R.string.server_failure));
                }
            }
        });

    }

    public void SignUP(String musername, String mpassword) {
        RestController.UserService mAPIService = ChatApplication.getClient(Constants.CHAT_SERVER_URL).create(RestController.UserService.class);
        mAPIService.signup(musername, mpassword).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 400 || response.body() == null) {
                    ((RegistrationViewModel)viewModel).clearViews();
                    utility.showSnackbar(activity, activity.getString(R.string.user_already_exixt));
                } else {
                    ((RegistrationViewModel)viewModel).openLogin();
                    Log.e("print", response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("print", t.toString());
                if (t instanceof ConnectException || t instanceof SocketTimeoutException) {
                    ((RegistrationViewModel)viewModel).clearViews();
                }
                utility.showSnackbar(activity, activity.getString(R.string.server_failure));
            }
        });
    }
}
