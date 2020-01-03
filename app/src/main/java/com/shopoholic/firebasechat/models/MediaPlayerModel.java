package com.shopoholic.firebasechat.models;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by appinventiv on 28/9/17.
 */

public class MediaPlayerModel {
    private int position;
    private String mediaUrl;
    private MediaPlayer mediaPlayer;
    private ProgressBar progressBar;
    private ProgressBar timeProgressBar;
    private ImageView ivPlayAudio;
    private TextView tvTime;
    private boolean isLoading;

    public MediaPlayerModel(int position, String mediaUrl, MediaPlayer mediaPlayer, ProgressBar progressBar, ProgressBar timeProgressBar, ImageView ivPlayAudio, TextView tvTime, boolean isLoading) {
        this.position = position;
        this.mediaUrl = mediaUrl;
        this.mediaPlayer = mediaPlayer;
        this.progressBar = progressBar;
        this.timeProgressBar = timeProgressBar;
        this.ivPlayAudio = ivPlayAudio;
        this.tvTime = tvTime;
        this.isLoading = isLoading;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ProgressBar getTimeProgressBar() {
        return timeProgressBar;
    }

    public void setTimeProgressBar(ProgressBar timeProgressBar) {
        this.timeProgressBar = timeProgressBar;
    }

    public ImageView getIvPlayAudio() {
        return ivPlayAudio;
    }

    public void setIvPlayAudio(ImageView ivPlayAudio) {
        this.ivPlayAudio = ivPlayAudio;
    }

    public TextView getTvTime() {
        return tvTime;
    }

    public void setTvTime(TextView tvTime) {
        this.tvTime = tvTime;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
