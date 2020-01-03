package com.shopoholic.firebasechat.utils;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.shopoholic.utils.AppSharedPreference;

import java.util.Random;
import java.util.UUID;

/**
 * Created by appinventiv-pc on 7/3/18.
 */

public class FirebaseStorageQueries {

    private static FirebaseStorageQueries instance;
//    private static StorageReference firebaseStorageRef;
    private static FirebaseAuth mAuth;
    private static Random randomNumber;
    private static String loginUserId;
    private static String loginUserName;

    private FirebaseStorageQueries() {
//        if (firebaseStorageRef == null) {
//            firebaseStorageRef = FirebaseStorage.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            randomNumber = new Random();
//        }
    }

    public static FirebaseStorageQueries getInstance() {
        if (instance == null)
            instance = new FirebaseStorageQueries();
        return instance;
    }

    public void updateUserData(Context mContext) {
        loginUserId = AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_ID);
        loginUserName = AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.FIRST_NAME) + " " +
                AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.LAST_NAME);
    }

    /*public void uploadFile(Context mContext, Uri filePath, int fileType, final FirebaseFileUploadListener uploadListener){
        if(filePath != null) {
            StorageReference ref = firebaseStorageRef.child(getFilePath(fileType));
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("Success : ", "Uploaded");
                        uploadListener.onSuccess(taskSnapshot);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Error : ", "Failed " + e.getMessage());
                        uploadListener.onFailure(e);
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        Log.e("Progress : ", String.valueOf(progress));
                        uploadListener.onProgress(taskSnapshot, progress);
                    });
        }else {
//            FirebaseChatUtils.getInstance().showToast(mContext, mContext.getString(R.string.unable_to_upload_file));
        }
    }*/

    /**
     * Method to get image name according the type of file
     * @param fileType  type of the file
     * @return      file name
     */
    private String getFilePath(int fileType) {

        switch (fileType){
            case FirebaseConstants.FILE_IMAGE:
                return "images/IMG_"+ UUID.randomUUID().toString();
            case FirebaseConstants.FILE_VIDEO:
                return "videos/VID_"+ UUID.randomUUID().toString();
            case FirebaseConstants.FILE_AUDIO:
                return "audio/AUD_"+ UUID.randomUUID().toString();
            default:
                return "other/FILE_"+ UUID.randomUUID().toString();
        }
    }
}
