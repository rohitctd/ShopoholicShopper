package com.dnitinverma.fblibrary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dnitinverma.fblibrary.interfaces.FBSignCallback;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.concurrent.Exchanger;

/**
 * Created by appinventiv on 7/9/17.
 */

public class FBSignInAI {
    private Activity mActivity;
    private CallbackManager callbackManager;
    private FBSignCallback fbSignCallback;
    private AccessToken accessToken;


    /*
    *  Initialize activity instance
    */
    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    /*
    *  Initialize FB callback
    */
    public void setCallback(FBSignCallback fbSignCallback) {
        this.fbSignCallback = fbSignCallback;
    }

    /*
    *  Sign In Method
    */
    public void doSignIn() {
        callbackManager = CallbackManager.Factory.create();
        LoginViaFacebook();
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "user_friends", "user_location", "email", "user_birthday"));
    }


    /*
   *  Sign out Method
   */
    public void doSignOut() {
        LoginManager.getInstance().logOut();
        fbSignCallback.fbSignOutSuccessResult();
    }

    /*
     * To get user profile information from facebook
     */
    public void LoginViaFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                final GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject jsonObject,
                                    GraphResponse response) {
                                if (jsonObject != null) {
                                    try {
                                        fbSignCallback.fbSignInSuccessResult(jsonObject);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, picture, age_range, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                fbSignCallback.fbSignInCancel();
            }

            @Override
            public void onError(FacebookException exception) {
                fbSignCallback.fbSignInFailure(exception);
            }
        });
    }

    /*
    *  return callback to facebook using callbackmanager
    */
    public void setActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /*
    *  get Friends from facebook
    */
    public void getFriends() {
        if(accessToken!=null)
        {
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/frizzends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
            /* handle the result */
                            try {
                                JSONObject responseObject = response.getJSONObject();
                                JSONArray dataArray = responseObject.getJSONArray("data");
                                fbSignCallback.fbFriends(dataArray);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();
        }
        else
        {
            callbackManager = CallbackManager.Factory.create();
            getFriendsUsingLogin();
            LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "user_friends", "user_location", "email", "user_birthday"));

        }

    }

    /*
    *  get Friends from facebook with login
    */
    private void getFriendsUsingLogin()
    {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
            /* handle the result */
                                try {
                                    JSONObject responseObject = response.getJSONObject();
                                    JSONArray dataArray = responseObject.getJSONArray("data");
                                    fbSignCallback.fbFriends(dataArray);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();
            }

            @Override
            public void onCancel() {
                fbSignCallback.fbSignInCancel();
            }

            @Override
            public void onError(FacebookException exception) {
                fbSignCallback.fbSignInFailure(exception);
            }
        });
    }

    /*
    *  Share URL
    */
    public void shareUrl(String url)
    {
        ShareDialog shareDialog = new ShareDialog(mActivity);

        ShareLinkContent content = new ShareLinkContent.Builder()
        .setContentUrl(Uri.parse(url))
        .build();

        shareDialog.show(content);
    }

    /*
    *  Share Image
    */
    public void shareImage(Bitmap bitmap)
    {
        ShareDialog shareDialog = new ShareDialog(mActivity);


        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setUserGenerated(true)
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        shareDialog.show(content);
    }

    /*
    *  Share Video
    */
    public void shareVideo( Uri videoUrl)
    {
        ShareDialog shareDialog = new ShareDialog(mActivity);

        ShareVideo video = new ShareVideo.Builder()
                .setLocalUrl(videoUrl)
                .build();

        ShareVideoContent videoContent = new ShareVideoContent.Builder()
                .setVideo(video)
                .build();

        shareDialog.show(videoContent);

    }

    /*
    *  Share Image and Video both
    */
    public void shareImageAndVideo(Bitmap bitmap, Uri videoUrl)
    {
        ShareDialog shareDialog = new ShareDialog(mActivity);

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setUserGenerated(true)
                .build();

        ShareVideo video = new ShareVideo.Builder()
                .setLocalUrl(videoUrl)
                .build();

        ShareContent shareContent = new ShareMediaContent.Builder()
                .addMedium(photo)
                .addMedium(video)
                .build();

        shareDialog.show(shareContent);
    }


}
