package com.shopoholic.firebasechat.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.firebasechat.asynctasks.PlayAudioAsyncTask;
import com.shopoholic.firebasechat.utils.FirebaseChatUtils;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * Activity used to record voice for message
 */

public class VoiceRecorderActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvRecording, tvTime;
    private TextView tvSend;
    private TextView tvCancel;
    private MediaRecorder recorder;
    private File audioFile;
    private int responseCode = RESULT_CANCELED;
    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();
    private long time = 0;
    private String outputFile;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isComplete;
    private boolean isRecorded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setListener();
    }

    /**
     * method to initialize the views
     */


    /**
     * method to set listener on views
     */
    private void setListener() {
        tvRecording.setOnClickListener(this);
        tvSend.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 1000ms the TimerTask will run every 600000ms
        timer.schedule(timerTask, 0, 1000); //
    }

    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        tvTime.setText(FirebaseChatUtils.getInstance().milliSecondsToTimer(time));
                        time += 1000;
                    }
                });
            }
        };
    }

    @Override
    public void onBackPressed() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        super.onBackPressed();
    }
}
