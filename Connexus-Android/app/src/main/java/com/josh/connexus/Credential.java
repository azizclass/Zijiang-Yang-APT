package com.josh.connexus;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;

public class Credential {
    private static final String LOG_TAG = "Credential";
    private static final String WEB_CLIENT_ID = "1050209727214-68oqhbc3l2fsfrbdup4dg08d4cmvusi8.apps.googleusercontent.com";
    private static final String AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;
    private static GoogleAccountCredential credential;


    public static GoogleAccountCredential getCredential(){
        return credential;
    }

    public static boolean isLoggedIn(){
        return credential != null;
    }

    public static void logout(){
        credential = null;
    }

    public static boolean login(String email, Context context){
        logout();
        Log.i(LOG_TAG, "Try to log in using "+email);
        if (!checkGooglePlayServicesAvailable(context)) return false;
        if(email == null || email.length() == 0) return false;
        try {
            // If the application has the appropriate access then a token will be retrieved, otherwise
            // an error will be thrown.
            GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context, AUDIENCE);
            credential.setSelectedAccountName(email);
            credential.getToken();
            Credential.credential = credential;
            Log.d(LOG_TAG, "AccessToken retrieved");
            // Success.
            return true;
        } catch (GoogleAuthException unrecoverableException) {
            Log.e(LOG_TAG, "Exception checking OAuth2 authentication.", unrecoverableException);
            // Failure.
            return false;
        } catch (IOException ioException) {
            Log.e(LOG_TAG, "Exception checking OAuth2 authentication.", ioException);
            // Failure or cancel request.
            return false;
        }
    }

    private static boolean checkGooglePlayServicesAvailable(Context context) {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode))
            return false;
        return true;
    }
}
