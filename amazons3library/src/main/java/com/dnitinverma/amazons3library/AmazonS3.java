package com.dnitinverma.amazons3library;

import android.app.Activity;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.dnitinverma.amazons3library.imageutils.ImageCompressor;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;

import java.io.File;
import java.util.Calendar;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by appinventiv on 7/9/17.
 */

public class AmazonS3 {
    private Activity mActivity;
    private AmazonCallback amazonCallback;
    private TransferUtility mTransferUtility;
    private String AMAZON_POOLID,BUCKET,AMAZON_SERVER_URL,END_POINT,REGION;


    /*
 *  Initialize activity instance
 */
    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    /*
    *  Initialize AmazonS3 callback
    */
    public void setCallback(AmazonCallback amazonCallback) {
        this.amazonCallback = amazonCallback;
    }


    /*
    * Initialize global variable
    */
    public void setVariables(String AMAZON_POOLID, String BUCKET, String AMAZON_SERVER_URL, String END_POINT, String REGION) {
        this.AMAZON_POOLID = AMAZON_POOLID;
        this.BUCKET = BUCKET;
        this.AMAZON_SERVER_URL = AMAZON_SERVER_URL;
        this.END_POINT = END_POINT;
        this.REGION = REGION;
    }

    /**
     * to upload and compress image
     * @param imageBean
     */

    public void uploadImage(final ImageBean imageBean) {
        File file = new File(imageBean.getImagePath());
        if(file.exists()) {
            mTransferUtility = AmazonUtils.getTransferUtility(mActivity,AMAZON_POOLID,END_POINT,REGION);
            if (imageBean.getFileType() == 1) {
                ImageCompressor.getDefault(mActivity)
                        .compressToFileAsObservable(file)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<File>() {
                            @Override
                            public void call(File file) {
                                uploadFileOnAmazon(imageBean, file);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
            }else {
                uploadFileOnAmazon(imageBean, file);
            }
        }else{
            imageBean.setIsSucess("0");
            Toast.makeText(mActivity, mActivity.getString(R.string.file_not_found_for_send), Toast.LENGTH_LONG).show();
            amazonCallback.uploadError(new Exception(mActivity.getString(R.string.file_not_found_for_send)),imageBean);
        }
    }

    private ImageBean addDataInBean(String id, String name, String path) {
        ImageBean bean = new ImageBean();
        bean.setId(id);
        bean.setName(name);
        bean.setImagePath(path);
        return bean;
    }


    /**
     * Method to upload image on amazon.
     * @param imageBean
     * @param file
     */

    private void uploadFileOnAmazon(ImageBean imageBean, File file) {
        TransferObserver observer = null;
        switch (imageBean.getFileType()) {
            case 1:
                observer = mTransferUtility.upload(BUCKET, "android" + String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".jpg", file, CannedAccessControlList.PublicRead);
                break;
            case 2:
                observer = mTransferUtility.upload(BUCKET, "android" + String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".mp4", file, CannedAccessControlList.PublicRead);
                break;
            case 3:
                observer = mTransferUtility.upload(BUCKET, "android" + String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".pdf", file, CannedAccessControlList.PublicRead);
                break;
        }
        if (observer != null) {
            observer.setTransferListener(new UploadListener(imageBean));
            imageBean.setmObserver(observer);
        }
    }



    private class UploadListener implements TransferListener {
        private ImageBean imageBean;

        public UploadListener(ImageBean imageBean) {
            this.imageBean = imageBean;
        }

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
                imageBean.setIsSucess("0");
                amazonCallback.uploadError(e,imageBean);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
               int progress = (int) ((double) bytesCurrent * 100 / bytesTotal);
                imageBean.setProgress(progress);
                amazonCallback.uploadProgress(imageBean);
        }


        @Override
        public void onStateChanged(int id, TransferState newState) {
            if (newState == TransferState.COMPLETED) {
                imageBean.setIsSucess("1");
                String url = AMAZON_SERVER_URL + imageBean.getmObserver().getKey();
                imageBean.setServerUrl(url);
                amazonCallback.uploadSuccess(imageBean);
            }
            else  if (newState == TransferState.FAILED) {
                imageBean.setIsSucess("0");
                amazonCallback.uploadFailed(imageBean);
            }
        }
    }

}
