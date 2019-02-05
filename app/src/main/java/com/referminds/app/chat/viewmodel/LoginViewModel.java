package com.referminds.app.chat.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.text.TextUtils;
import com.referminds.app.chat.data.Model.SessionManager;
import com.referminds.app.chat.data.Model.User;
import com.referminds.app.chat.R;
import com.referminds.app.chat.view.Activity.LoginActivity;
import com.referminds.app.chat.view.Activity.MainActivity;
import com.referminds.app.chat.view.Activity.Registration;
import com.referminds.app.chat.view.Utils.CommonSessionCall;

public class LoginViewModel extends ViewModel {

    private SessionManager sessionManager;
    private CommonSessionCall commonSessionCallbck;

    public MutableLiveData<String> errorPassword = new MutableLiveData<>();
    public MutableLiveData<String> errorUserNmae = new MutableLiveData<>();

    public MutableLiveData<String> username = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<User> userMutableLiveData;
    private LoginActivity loginActivity;

    public LoginViewModel() { }

    public void init(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        sessionManager = new SessionManager(loginActivity);
        commonSessionCallbck = new CommonSessionCall(this,loginActivity);
    }

    public  LiveData<User> getUser() {
        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }

        return userMutableLiveData;
    }
    public void onSignInbtnClicked() {
        // Reset errors.
        errorUserNmae.setValue(null);

        // Check for a valid username.
        if (TextUtils.isEmpty(username.getValue())) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            errorUserNmae.setValue(loginActivity.getString(R.string.error_field_required));
            return;
        }
        if (TextUtils.isEmpty(password.getValue())) {
           errorPassword.setValue(loginActivity.getString(R.string.error_field_required));
            return;
        }

        User user = new User(username.getValue(), password.getValue());
        commonSessionCallbck.signIn(user);
    }

    public void onSignInLinkClicked(){
        navigateToSignin();
    }

    private void navigateToSignin() {
        Intent signUpIntent = new Intent(loginActivity, Registration.class);
        loginActivity.startActivity(signUpIntent);
    }
    public   void onAuthenticated() {
        sessionManager.createLoginSession(username.getValue(), null);
        Intent loginIntent = new Intent(loginActivity, MainActivity.class);
        loginActivity.startActivity(loginIntent);
        loginActivity.finish();

    }
    public void onAuthenticationFailed() {
         errorUserNmae.setValue(loginActivity.getString(R.string.wrong_username));
        errorPassword.setValue(loginActivity.getString(R.string.wrong_Password));
    }
}
