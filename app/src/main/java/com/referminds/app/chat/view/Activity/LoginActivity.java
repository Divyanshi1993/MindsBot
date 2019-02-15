package com.referminds.app.chat.view.Activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.referminds.app.chat.R;
import com.referminds.app.chat.data.Model.SessionManager;
import com.referminds.app.chat.data.Model.User;
import com.referminds.app.chat.databinding.ActivityLoginBinding;
import com.referminds.app.chat.view.Listners.AuthenticationListner;
import com.referminds.app.chat.view.Listners.LoginNavigator;
import com.referminds.app.chat.view.Utils.CommonSessionCall;
import com.referminds.app.chat.viewmodel.LoginViewModel;


/**
 * A login screen that offers login via username.
 */
public class LoginActivity extends AppCompatActivity implements AuthenticationListner, LoginNavigator {
    private SessionManager sessionManager;
    private CommonSessionCall commonSessionCallbck;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        commonSessionCallbck = new CommonSessionCall(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        LoginViewModel viewModel = new LoginViewModel();
        binding.setLoginViewModel(viewModel);
        viewModel.setNavigator(this);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
       }

    public void onAuthenticationFailed() {
        binding.usernameInput.setError(getString(R.string.wrong_username));
        binding.passwordInput.setError(getString(R.string.wrong_Password));
    }

    @Override
    public void onAuthenticated(String username) {
        sessionManager.createLoginSession(username, null);
        openMainActivity();

    }

    @Override
    public void setError(String type) {
        if (type.equals("username")) {
            binding.usernameInput.setError(getString(R.string.error_field_required));
        } else {
            binding.passwordInput.setError(getString(R.string.error_field_required));
        }
    }

    @Override
    public void attemptSignin(User user) {
        commonSessionCallbck.signIn(user);
    }

    public void navigateToSignup() {
        Intent signUpIntent = new Intent(LoginActivity.this, Registration.class);
        startActivity(signUpIntent);
    }

    @Override
    public void openMainActivity() {
        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
