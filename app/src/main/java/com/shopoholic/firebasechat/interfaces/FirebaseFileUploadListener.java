package com.shopoholic.firebasechat.interfaces;

//import com.google.firebase.storage.UploadTask;

/**
 * interface to get response of upload file in firebase storage
 */

public interface FirebaseFileUploadListener {
//    void onSuccess(UploadTask.TaskSnapshot taskSnapshot);
    void onFailure(Exception e);
//    void onProgress(UploadTask.TaskSnapshot taskSnapshot, double progress);
}
