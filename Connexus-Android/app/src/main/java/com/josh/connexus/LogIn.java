package com.josh.connexus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogIn extends Activity {
    private EditText userName;
    private EditText passWord;
    private Button logIn;
    private Button viewStreams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        userName = (EditText)findViewById(R.id.login_user_name);
        passWord = (EditText)findViewById(R.id.login_password);
        logIn = (Button)findViewById(R.id.login_btn_log_in);
        viewStreams = (Button)findViewById(R.id.login_btn_view_streams);
    }

    public void onLogInClick(View v){

    }

    public void onViewStreamsClick(View v){

    }
}
