package com.googlelibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.googlelibrary.interfaces.GoogleSignCallback;

public class GoogleSignInAI implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{
    private static GoogleSignInAI mGoogleSignInAI;
    private Activity mActivity;
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleApiClient mGoogleApiClient;
    private boolean mSignInClicked;
    private int GOOGLE_SIGN_IN_REQUEST_CODE;
    private GoogleSignCallback mGoogleSignCallback;


    /*
   *  Initialize activity instance
   */
    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    /*
    *  Initialize Google callback
    */
    public void setCallback(GoogleSignCallback mGoogleSignCallback) {
        this.mGoogleSignCallback = mGoogleSignCallback;
    }

    /*
    *  Initialize Google request code
    */
    public void setRequestCode(int GOOGLE_SIGN_IN_REQUEST_CODE) {
        this.GOOGLE_SIGN_IN_REQUEST_CODE = GOOGLE_SIGN_IN_REQUEST_CODE;

    }

    /*
    * Configure google sign in request for contacts
    */
    public void setUpGoogleClientForGoogleLogin(){
        mGoogleSignInOptions  = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(Auth.GOOGLE_SIGN_IN_API,mGoogleSignInOptions)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
     if(mGoogleApiClient!=null){
         mGoogleApiClient.connect();
     }
    }

    public void doSignIn(){
        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
        mSignInClicked = true;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mActivity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    public void onActivityResult(Intent data) {
        if(mSignInClicked) {
            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(signInResult);
        }
    }


    private void handleSignInResult(GoogleSignInResult googleSignInResult){
        if (googleSignInResult != null) {
            if (googleSignInResult.isSuccess()) {
                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
                getProfileInfo(googleSignInAccount);
            } else {
                //Failure Message
                mGoogleSignCallback.googleSignInFailureResult(googleSignInResult.getStatus().getStatusMessage());
            }
        }else {
            mGoogleSignCallback.googleSignInFailureResult("");
        }
        mSignInClicked = false;

    }

    private void getProfileInfo(GoogleSignInAccount googleSignInAccount) {
        mGoogleSignCallback.googleSignInSuccessResult(googleSignInAccount);
    }

    public void doSignout(){
        if(mGoogleApiClient!=null&& mGoogleApiClient.isConnected()){
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if(status.isSuccess()){
                        mGoogleApiClient.disconnect();
                        mGoogleSignCallback.googleSignOutSuccessResult(status.getStatusMessage());
                    }else{
                        mGoogleSignCallback.googleSignOutFailureResult(status.getStatusMessage());
                    }
                }
            });
        }
    }

}