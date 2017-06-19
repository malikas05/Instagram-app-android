/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

    TextView changeSignupModeTextView;
    EditText passwordEditText;
    Boolean loginModeActive = true;

    public void showUserList(){
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        // Press enter and make login or signup
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            signUpLogin(view);
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.changeSignupModeTextView){
            Button signupButton = (Button)findViewById(R.id.signupLoginButton);
            if (loginModeActive){
                loginModeActive = false;
                signupButton.setText("Signup");
                changeSignupModeTextView.setText("or, Login");
            }
            else {
                loginModeActive = true;
                signupButton.setText("Login");
                changeSignupModeTextView.setText("or, Signup");
            }
        }
        else if (view.getId() == R.id.backgroundRelativeLayout || view.getId() == R.id.logoImageView){
            //Hide keyboard when you click on the screen or on the logo
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void signUpLogin(View view){
        EditText usernameEditText = (EditText)findViewById(R.id.usernameEditText);

        if (usernameEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")){
            Toast.makeText(this, "A username and password are required.", Toast.LENGTH_SHORT).show();
        }
        else {
            if (!loginModeActive) {
                //Sign up a new user
                ParseUser user = new ParseUser();
                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Signup", "Successful");
                            showUserList();
                        }
                        else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null && user != null){
                            Log.i("Login", "Successful");
                            showUserList();
                        }
                        else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setTitle("Instagram");

        RelativeLayout backgroundRelativeLayout = (RelativeLayout)findViewById(R.id.backgroundRelativeLayout);
        ImageView logoImageView = (ImageView)findViewById(R.id.logoImageView);
        backgroundRelativeLayout.setOnClickListener(this);
        logoImageView.setOnClickListener(this);

        changeSignupModeTextView = (TextView)findViewById(R.id.changeSignupModeTextView);
        changeSignupModeTextView.setOnClickListener(this);

        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        passwordEditText.setOnKeyListener(this);

        if (ParseUser.getCurrentUser() != null){
            showUserList();
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}