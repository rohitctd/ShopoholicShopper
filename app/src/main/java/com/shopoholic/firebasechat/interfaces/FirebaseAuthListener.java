package com.shopoholic.firebasechat.interfaces;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

/**
 * Interface to get authentication response
 */

public interface FirebaseAuthListener {
    void onAuthSuccess(Task<AuthResult> task, FirebaseUser user);
    void onAuthError(Task<AuthResult> task);
}