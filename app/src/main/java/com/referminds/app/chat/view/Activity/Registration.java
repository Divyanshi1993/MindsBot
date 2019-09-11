package com.referminds.app.chat.view.Activity;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.referminds.app.chat.R;
import com.referminds.app.chat.data.Model.User;
import com.referminds.app.chat.databinding.RegistrationBinding;
import com.referminds.app.chat.view.Listners.RegistrationNavigator;
import com.referminds.app.chat.view.Utils.CommonSessionCall;
import com.referminds.app.chat.view.Utils.Utility;
import com.referminds.app.chat.viewmodel.RegistrationViewModel;

public class Registration extends AppCompatActivity implements RegistrationNavigator {
    RegistrationBinding binding;
    private Utility utility;
    private CommonSessionCall commonSessionCallbck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utility = new Utility();
        commonSessionCallbck = new CommonSessionCall(this);
        binding = DataBindingUtil.setContentView(this, R.layout.registration);

        RegistrationViewModel viewModel = new RegistrationViewModel();
        binding.setRegViewModel(viewModel);
        viewModel.setNavigator(this);
        viewModel.getUserValidation().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                attemptSignUp(user);
            }
        });

    }

    @Override
    public void onPasswordMismatched() {
        clearViews();
        binding.passwordInput.setError(getString(R.string.pass_missmatch));
        binding.passwordInput.requestFocus();
        utility.showSnackbar(Registration.this, getString(R.string.pass_missmatch));
    }

    public void clearViews() {
        binding.usernameInput.setText("");
        binding.passwordInput.setText("");
        binding.ConfirmpasswordInput.setText("");
        utility.signinbuttonState(binding.signUpButton, true, getColor(R.color.signinButtonColor));
    }


    @Override
    public void setError(String type) {
        clearViews();
    }

    @Override
    public void attemptSignUp(User user) {
        utility.signinbuttonState(binding.signUpButton, false, getColor(R.color.button_disable));
        commonSessionCallbck.SignUP(user,this);
    }

    @Override
    public void navigateToSignIn() {
        finish();
    }
}