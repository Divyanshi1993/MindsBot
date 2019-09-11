package com.referminds.app.chat.view.Listners;

import com.referminds.app.chat.data.Model.User;

public interface RegistrationNavigator {
    void setError(String type);

    void attemptSignUp(User user);

    void navigateToSignIn();
    void  onPasswordMismatched();
}
