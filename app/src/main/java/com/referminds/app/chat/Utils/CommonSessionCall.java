package com.referminds.app.chat.Utils;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.referminds.app.chat.Activity.LoginActivity;
import com.referminds.app.chat.Activity.MainActivity;
import com.referminds.app.chat.Activity.Registration;
import com.referminds.app.chat.Controller.RestController;
import com.referminds.app.chat.Model.User;
import com.referminds.app.chat.R;
import com.referminds.app.chat.Singelton.ChatApplication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.ConnectException;
import java.net.SocketTimeoutException;


public class CommonSessionCall {
    private AppCompatActivity activity;
    private Utility utility;

    public CommonSessionCall() {
        utility = new Utility();
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

    public void attemptLogin(String username, String pass) {
        RestController.UserService mAPIService = ChatApplication.getClient(Constants.CHAT_SERVER_URL).create(RestController.UserService.class);
        mAPIService.signin(username, pass).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 404 || response.body() == null) {
                    ((LoginActivity) activity).onAuthenticationFailed();
                    utility.showSnackbar(activity, activity.getString(R.string.unauthorize));
                } else if (response.code() == 201) {
                    utility.showSnackbar(activity, activity.getString(R.string.user_already_exixt));

                } else {
                    Log.e("print", response.body().toString());
                    ((LoginActivity) activity).onAuthenticated();
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
                    ((Registration) activity).clearViews();
                    utility.showSnackbar(activity, activity.getString(R.string.user_already_exixt));
                } else {
                    ((Registration) activity).openLogin();
                    Log.e("print", response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("print", t.toString());
                if (t instanceof ConnectException || t instanceof SocketTimeoutException) {
                    ((Registration) activity).clearViews();
                }
                utility.showSnackbar(activity, activity.getString(R.string.server_failure));
            }
        });
    }
    public void signoutUninstalled(String username) {
        RestController.UserService mAPIService = ChatApplication.getClient(Constants.CHAT_SERVER_URL).create(RestController.UserService.class);
        mAPIService.signout(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 404 || response.body() == null) {
                    Log.e("logout", "Logout Failed");
                } else {
                    Log.e("logout", response.body().toString());

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
}
