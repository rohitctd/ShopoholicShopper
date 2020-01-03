package com.shopoholic.firebasechat.asynctasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shopoholic.R;
import com.shopoholic.firebasechat.models.MediaPlayerModel;
import com.shopoholic.firebasechat.utils.FirebaseChatUtils;

/**
 * Async task to play audio
 */

@SuppressLint("StaticFieldLeak")
public class PlayAudioAsyncTask extends AsyncTask<Void,Void,Void> {


    private MediaPlayerModel playerModel;
    private MediaPlayer mediaPlayer;
    private ProgressBar progressBar;
    private ProgressBar mediaProgressBar;
    private ImageView ivPlayAudio;
    private TextView timeTV;
    private String url;
    private boolean isCancelled;

    public PlayAudioAsyncTask(String url, MediaPlayer mediaPlayer, ProgressBar progressBar, ProgressBar mediaProgressBar,
                              ImageView ivPlayAudio, TextView timeTV) {
        this.url = url;
        this.mediaPlayer = mediaPlayer;
        this.progressBar = progressBar;
        this.mediaProgressBar = mediaProgressBar;
        this.ivPlayAudio = ivPlayAudio;
        this.timeTV = timeTV;
    }

    public PlayAudioAsyncTask(MediaPlayerModel playerModel) {
        this.playerModel = playerModel;
        this.url = playerModel.getMediaUrl();
        this.mediaPlayer = playerModel.getMediaPlayer();
        this.progressBar = playerModel.getProgressBar();
        this.mediaProgressBar = playerModel.getTimeProgressBar();
        this.ivPlayAudio = playerModel.getIvPlayAudio();
        this.timeTV = playerModel.getTvTime();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        if (ivPlayAudio != null)
            ivPlayAudio.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (!isCancelled) {
            if (playerModel != null) {
                if (playerModel.isLoading()) {
                    if (progressBar != null)
                        progressBar.setVisibility(View.INVISIBLE);
                    if (ivPlayAudio != null)
                        ivPlayAudio.setVisibility(View.VISIBLE);
                    playerModel.setLoading(false);
                    setAudio();
                }
            } else {
                if (progressBar != null)
                    progressBar.setVisibility(View.INVISIBLE);
                if (ivPlayAudio != null)
                    ivPlayAudio.setVisibility(View.VISIBLE);
                setAudio();
            }
        }
    }

    @Override
    protected void onCancelled() {
        isCancelled = true;
    }

    /**
     * Function to play a song
     *
     **/

    private void setAudio() {
        // Play song
        try {
            mediaPlayer.start();
            if (ivPlayAudio != null)
            // set Progress bar values
            if (mediaProgressBar != null) {
                mediaProgressBar.setProgress(0);
                mediaProgressBar.setMax(100);
            }

            // Updating progress bar
            long totalDuration = mediaPlayer.getDuration();
            updateProgressBar(mediaPlayer, totalDuration);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Update timer on seekbar
     * @param mediaPlayer
     * @param totalDuration
     */
    private void updateProgressBar(final MediaPlayer mediaPlayer, final long totalDuration) {
        new CountDownTimer(totalDuration * 1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                long currentDuration = totalDuration;
                try {
                    if (mediaPlayer != null)
                        currentDuration = mediaPlayer.getCurrentPosition();

                    // Displaying time completed playing
                    if (timeTV != null)
                        timeTV.setText(FirebaseChatUtils.getInstance().milliSecondsToTimer(currentDuration));

                    // Updating progress bar
                    int progress = FirebaseChatUtils.getInstance().getProgressPercentage(currentDuration, totalDuration);
                    if (mediaProgressBar != null)
                        mediaProgressBar.setProgress(progress);
                    if (currentDuration/10 >= totalDuration/10 || progress == 100) {
                        this.onFinish();
                    }

                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    this.cancel();
                }
            }

            @Override
            public void onFinish() {
                if (mediaProgressBar != null) {
                    mediaProgressBar.setProgress(0);
                    timeTV.setText(FirebaseChatUtils.getInstance().milliSecondsToTimer(totalDuration));
                }
                this.cancel();
            }
        }.start();
    }
}