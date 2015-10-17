package com.josh.connexus;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;


public class LogIn extends Activity {

    private final static int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 0;
    private boolean isLoggingIn = false;
    private static LogInHandler logInHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        logInHandler = new LogInHandler(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        isLoggingIn = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            performLogIn(accountName);
        }
    }


    private void performLogIn(final String email){
        new Thread(){
            @Override
            public void run(){
                if(isLoggingIn) return;
                isLoggingIn = true;
                Credential.login(email, LogIn.this);
                if(!isLoggingIn){
                    Credential.logout();
                    return;
                }
                isLoggingIn = false;
                logInHandler.sendMessage(new Message());
            }
        }.start();
    }

    public void onLogInClick(View v){
        if(isLoggingIn) return;
        Intent accountSelector = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false,
                "Select the account to log in.", null, null, null);
        startActivityForResult(accountSelector, ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION);
    }

    public void onViewStreamsClick(View v){

    }
}

class LogInHandler extends Handler {
    private Context context;

    public LogInHandler(Context context){
        this.context = context;
    }
    @Override
    public void handleMessage(Message msg){
        if(Credential.isLoggedIn())
            Toast.makeText(context, "Log in success!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Log in fails!", Toast.LENGTH_LONG).show();
    }
}