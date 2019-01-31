package com.referminds.app.chat.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.referminds.app.chat.data.Model.SessionManager;
import com.referminds.app.chat.data.Model.User;
import com.referminds.app.chat.R;
import com.referminds.app.chat.view.Activity.Registration;
import com.referminds.app.chat.view.Utils.CommonSessionCall;
import com.referminds.app.chat.view.Utils.Utility;


public class RegistrationViewModel extends ViewModel {

    private SessionManager sessionManager;
    private CommonSessionCall commonSessionCallbck;

    public MutableLiveData<String> errorPassword = new MutableLiveData<>();
    public MutableLiveData<String> errorConfirPassword = new MutableLiveData<>();
    public MutableLiveData<String> username = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> confirm_password = new MutableLiveData<>();
    MutableLiveData<Integer> buttonbackground = new MutableLiveData<>();
    private MutableLiveData<User> userMutableLiveData;
    private Registration registration;
    private  Utility utility;

    public RegistrationViewModel() { }

    public void init(Registration registration) {
        this.registration = registration;
        utility = new Utility();
        commonSessionCallbck = new CommonSessionCall(this,registration);
    }

    public LiveData<User> getUser() {
        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }

        return userMutableLiveData;
    }
    public void onSignUPbtnClicked() {
        if (!password.getValue().equals(null) && !confirm_password.getValue().equals(null)) {
            if (password.getValue().equals(confirm_password.getValue())) {

                commonSessionCallbck.SignUP(username.getValue(), password.getValue());
            } else {
                onPasswordMismatched();
            }
        } }
    private void onPasswordMismatched() {
        clearViews();
        errorPassword.setValue(registration.getString(R.string.pass_missmatch));
        //password.requestFocus();
        utility.showSnackbar(registration, registration.getString(R.string.pass_missmatch));
    }

    public void clearViews() {
        username.setValue("");
        password.setValue("");
        confirm_password.setValue("");
        buttonbackground.setValue(registration.getColor(R.color.signinButtonColor));
        //utility.signinbuttonState(signUpButton, true, registration.getColor(R.color.signinButtonColor));
    }
    public void openLogin() {
        registration.finish();
    }
}
