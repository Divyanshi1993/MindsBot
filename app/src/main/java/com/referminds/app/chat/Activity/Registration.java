package com.referminds.app.chat.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.referminds.app.chat.Model.User;
import com.referminds.app.chat.R;
import com.referminds.app.chat.Util.CommonSessionCallbck;
import com.referminds.app.chat.Util.Utility;

public class Registration extends AppCompatActivity {
    Button signUpButton;
    EditText username, password, confirm_password;
    String mpassword, mConfrimPassword, musername;
    User user;
    private Utility utility;
    private CommonSessionCallbck commonSessionCallbck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        user = new User();
        utility = new Utility();
        commonSessionCallbck = new CommonSessionCallbck(this);
        init();
        initListners();


    }

    private void initListners() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utility.signinbuttonState(signUpButton, false, getColor(R.color.button_disable));
                signUp();


            }
        });
    }


    private void init() {
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        username = findViewById(R.id.username_input);
        password = findViewById(R.id.password_input);
        confirm_password = findViewById(R.id.Confirmpassword_input);
        confirm_password.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    signUp();
                    return true;
                }
                return false;
            }
        });

    }

    private void signUp() {
        mpassword = password.getText().toString();
        mConfrimPassword = confirm_password.getText().toString();
        musername = username.getText().toString();
        if (!password.getText().equals(null) && !confirm_password.equals(null)) {
            if (mpassword.equals(confirm_password.getText().toString())) {

                commonSessionCallbck.SignUP(musername, mpassword);
            } else {
                onPasswordMismatched();
            }
        }
    }

    public void openLogin() {
        finish();
    }

    private void onPasswordMismatched() {
        clearViews();
        password.setError(getString(R.string.pass_missmatch));
        password.requestFocus();
        utility.showSnackbar(Registration.this, getString(R.string.pass_missmatch));
    }

    public void clearViews() {
        username.setText("");
        password.setText("");
        confirm_password.setText("");
        utility.signinbuttonState(signUpButton, true, getColor(R.color.signinButtonColor));
    }


}
