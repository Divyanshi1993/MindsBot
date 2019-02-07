package com.referminds.app.chat.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;
import com.referminds.app.chat.data.Model.User;
import com.referminds.app.chat.view.Listners.LoginNavigator;

public class LoginViewModel extends BaseViewModel<LoginNavigator> {

    public MutableLiveData<String> username = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<User> userMutableLiveData;

    public LoginViewModel() { }



    public  LiveData<User> getUser() {
        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }

        return userMutableLiveData;
    }
    public void onSignInbtnClicked() {

        // Check for a valid username.
        if (TextUtils.isEmpty(username.getValue())) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            getNavigator().setError("username");
            return;
        }
        if (TextUtils.isEmpty(password.getValue())) {
           getNavigator().setError("password");
            return;
        }

        User user = new User(username.getValue(), password.getValue());
        getNavigator().attemptSignin(user);
    }

    public void onSignInLinkClicked(){
        getNavigator().navigateToSignup();
    }

}
