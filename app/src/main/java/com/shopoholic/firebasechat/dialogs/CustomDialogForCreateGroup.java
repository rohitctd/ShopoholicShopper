package com.shopoholic.firebasechat.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.shopoholic.R;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.firebasechat.activities.GroupChatActivity;
import com.shopoholic.firebasechat.adapters.UsersListAdapter;
import com.shopoholic.firebasechat.interfaces.DialogCallback;
import com.shopoholic.firebasechat.interfaces.GroupCallback;
import com.shopoholic.firebasechat.interfaces.RecycleViewCallBack;
import com.shopoholic.firebasechat.models.ChatRoomBean;
import com.shopoholic.firebasechat.models.GroupDetailBean;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.firebasechat.utils.FirebaseEventListeners;
import com.shopoholic.utils.AppSharedPreference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import appinventiv.com.imagecropper.cicularcropper.CropImageView;
import appinventiv.com.imagecropper.cicularcropper.ImageCropper;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;


/**
 * Dialog for image selection
 */

public class CustomDialogForCreateGroup extends Dialog implements View.OnClickListener {
    private final int REQUEST_STORAGE_PERMISSION = 101;
    private final int REQUEST_CAMERA = 1001, REQUEST_GALLERY = 1002, CROPPER_REQUEST_CODE = 1003;

    private Context mContext;
    private GroupCallback createNewGroupCallback;
    private CircleImageView ivGroupImage;
    private EditText etGroupName;
    private RecyclerView rvUsersList;
    private Button btnCreateGroup;
    private GroupDetailBean groupDetailBean;
    private List<UserBean> usersList;
    private List<UserBean> membersList;
    private String groupImage = "";
    private String type;
    private boolean isLoading;
    private ChatRoomBean chatRoom;
    private Uri outputUri;
    private boolean hasGroupImage;
    private UsersListAdapter usersListAdapter;
    private Button btnCancel;
    private FrameLayout progressBar;

    public CustomDialogForCreateGroup(Context mContext, String type, List<UserBean> membersList, ChatRoomBean chatRoom, GroupCallback createNewGroupCallback) {
        super(mContext);
        this.mContext = mContext;
        this.type = type;
        this.membersList = membersList;
        this.chatRoom = chatRoom;
        this.createNewGroupCallback = createNewGroupCallback;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.dialog_create_group);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(null);
        getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().getAttributes().windowAnimations = R.style.DialogBounceAnimation;
        initVariables();
        setListeners();
        setAdapter();
    }



    /**
     * Method to initialize the views
     */
    private void initVariables() {
        groupDetailBean = new GroupDetailBean();
        usersList = new ArrayList<>();
        if (type.equals(FirebaseConstants.UPDATE)) {
            rvUsersList.setVisibility(View.GONE);
            btnCreateGroup.setText("Update");
            etGroupName.setText(chatRoom.getChatRoomTitle());
        }if (type.equals(FirebaseConstants.DETAILS)) {
            showGroupDetails();
        } else {
            getNonMemberUserList();
        }
        usersListAdapter = new UsersListAdapter(mContext, usersList, new RecycleViewCallBack() {
            @Override
            public void onClick(int position, View clickedView) {

            }

            @Override
            public void onLongClick(int position, View clickedView) {
            }
        });
    }


    /**
     * Method to get the user list for group that are not added in group
     */
    private void getNonMemberUserList() {
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE).addListenerForSingleValueEvent(new FirebaseEventListeners() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserBean userBean = snapshot.getValue(UserBean.class);
                        if (userBean != null && !userBean.getUserId().equals(AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_ID))) {
                            usersList.add(userBean);
                            usersListAdapter.notifyItemInserted(usersList.size() - 1);
                        }
                    }

                    if (type.equals(FirebaseConstants.ADD_NEW)) {
                        for (int i = 0; i < usersList.size(); i++) {
                            for (UserBean member : membersList) {
                                if (member.getUserId().equals(usersList.get(i).getUserId())) {
                                    usersList.remove(i);
                                    usersListAdapter.notifyItemRemoved(i);
                                    usersListAdapter.notifyItemRangeChanged(i, usersList.size());
                                    i--;
                                    break;
                                }
                            }
                        }
                        btnCreateGroup.setText("Add members");
                        etGroupName.setText(chatRoom.getChatRoomTitle());
                        etGroupName.setEnabled(false);
                    }
                }
            }
        });
    }


    /**
     * Method to set listener in views
     */
    private void setListeners() {
        if (type.equals(FirebaseConstants.CREATE) || type.equals(FirebaseConstants.UPDATE))
            ivGroupImage.setOnClickListener(this);
        btnCreateGroup.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }


    /**
     * Method to set adapter in views
     */
    private void setAdapter() {
        rvUsersList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvUsersList.setAdapter(usersListAdapter);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
         if (viewId == R.id.btn_cancel) {
            dismiss();
            createNewGroupCallback.onCancel();
        } else {
            dismiss();

        }
    }


    /**
     * Method to show group details
     */
    private void showGroupDetails() {
        for (int i = 0; i < membersList.size(); i++) {
            if (membersList.get(i).getUserId().equals(AppSharedPreference.getInstance().getString(mContext, AppSharedPreference.PREF_KEY.USER_ID))) {
                membersList.remove(i);
                break;
            }
        }
        usersList.addAll(membersList);
        etGroupName.setText(chatRoom.getChatRoomTitle());
        etGroupName.setEnabled(false);
        btnCreateGroup.setVisibility(View.GONE);
    }
/*
    *//**
     * Method to upload group image in firebase
     *//*
    private void uploadGroupImage() {
        FirebaseStorageQueries.getInstance().uploadFile(mContext, outputUri, FirebaseConstants.FILE_IMAGE, new FirebaseFileUploadListener() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getDownloadUrl() != null) {
                    progressBar.setVisibility(View.GONE);
                    String filePath = taskSnapshot.getMetadata().getDownloadUrl().toString();
                    groupDetailBean.setGroupImage(filePath);
                    if (type.equals(FirebaseConstants.CREATE)) {
                        createGroup();
                    }
                    else {
                        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(chatRoom.getChatRoomId())
                                .child(FirebaseConstants.CHAT_ROOM_PIC_NODE).setValue(filePath);
                        dismiss();
                    }
                }else {
                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot, double progress) {}
        });
    }*/

    /**
     * Method used to create group in firebase
     */
    private void createGroup() {
        String groupRoomId = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).push().getKey();
        groupDetailBean.setGroupRoomId(groupRoomId);
        FirebaseDatabaseQueries.getInstance().createGroup(mContext, groupDetailBean, null);
        createNewGroupCallback.onCreateGroup(groupRoomId);
        progressBar.setVisibility(View.GONE);
        dismiss();
    }


    /**
     * Checks permission to Write external storage in Marshmallow and above devices
     */
    private void checkWritePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //do your check here

            if(ContextCompat.checkSelfPermission(mContext instanceof HomeActivity ? mContext instanceof HomeActivity ? (HomeActivity)mContext : ((GroupChatActivity)mContext) : ((GroupChatActivity)mContext), READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(mContext instanceof HomeActivity ? (HomeActivity)mContext : ((GroupChatActivity)mContext), new String[]{READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            } else {
                // permission already granted
                showDialog();
            }
        } else {
            //before marshmallow
            showDialog();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_STORAGE_PERMISSION:
                checkWritePermission();
                break;

        }

    }

    /**
     * Show Image picker dialog at bottom
     */
    private void showDialog() {
        final CustomDialogForImagePicker dialog = new CustomDialogForImagePicker(mContext, new DialogCallback() {
            @Override
            public void onCameraSelection() {
                File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/" + mContext.getString(R.string.app_name));
                if(!myDir.exists()) myDir.mkdir();
                String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
                File file = new File(myDir, fileName);
                outputUri= Uri.fromFile(file);
                ImageCropper.startCaptureImageActivity(mContext instanceof HomeActivity ? (HomeActivity)mContext : ((GroupChatActivity)mContext), REQUEST_CAMERA,CROPPER_REQUEST_CODE);

            }

            @Override
            public void onGallerySelection() {
                File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/" + mContext.getString(R.string.app_name));
                if(!myDir.exists()) myDir.mkdir();
                String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
                File file = new File(myDir, fileName);
                outputUri=Uri.fromFile(file);
                ImageCropper.startPickImageFromGalleryActivity(mContext instanceof HomeActivity ? (HomeActivity)mContext : ((GroupChatActivity)mContext), REQUEST_GALLERY, CROPPER_REQUEST_CODE);

            }
        });
        dialog.show();
    }

    /**
     * handle activity request
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(requestCode==REQUEST_GALLERY){
                ImageCropper.activity(data.getData()).setGuidelines(CropImageView.Guidelines.OFF).
                        setCropShape(CropImageView.CropShape.OVAL).
                        setBorderLineColor(Color.WHITE).
                        setBorderCornerColor(Color.TRANSPARENT).
                        setAspectRatio(80, 80).setBorderLineThickness(5).
                        setOutputUri(outputUri).setActionbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary)).
                        setAutoZoomEnabled(true).start(mContext instanceof HomeActivity ? (HomeActivity)mContext : ((GroupChatActivity)mContext));

            }else if(requestCode==REQUEST_CAMERA) {
                ImageCropper.activity(ImageCropper.getCapturedImageURI()).setGuidelines(CropImageView.Guidelines.OFF).
                        setCropShape(CropImageView.CropShape.OVAL).
                        setBorderLineColor(Color.WHITE).
                        setBorderCornerColor(Color.TRANSPARENT).
                        setAspectRatio(80, 80).setBorderLineThickness(5).
                        setOutputUri(outputUri).setActionbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary)).
                        setAutoZoomEnabled(true).start(mContext instanceof HomeActivity ? (HomeActivity)mContext : ((GroupChatActivity)mContext));
            } else if(requestCode==CROPPER_REQUEST_CODE) {
                hasGroupImage = true;
                ivGroupImage.setImageURI(outputUri);
            }

        }
    }
}
