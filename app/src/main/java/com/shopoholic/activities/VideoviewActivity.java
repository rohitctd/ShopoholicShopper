package com.shopoholic.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.shopoholic.R;
import com.shopoholic.utils.Constants;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoviewActivity extends AppCompatActivity {

    @BindView(R.id.video_view)
    SimpleExoPlayerView videoView;
    @BindView(R.id.circular_progressbar)
    ProgressBar circularProgressbar;
    @BindView(R.id.iv_thumb)
    ImageView ivThumb;
    @BindView(R.id.light_black_layer)
    View lightBlackLayer;
    @BindView(R.id.tv_current_duration)
    TextView tvCurrentDuration;
    @BindView(R.id.progress_bar)
    SeekBar progressBar;
    @BindView(R.id.tv_final_duration)
    TextView tvFinalDuration;
    @BindView(R.id.iv_fullscreen)
    ImageView ivFullscreen;
    @BindView(R.id.rl_bottom_controllers)
    RelativeLayout rlBottomControllers;
    @BindView(R.id.iv_main_play)
    ImageView ivMainPlay;
    @BindView(R.id.fl_controllers)
    FrameLayout flControllers;
    @BindView(R.id.fl_video_player)
    FrameLayout flVideoPlayer;
    @BindView(R.id.rl_video)
    RelativeLayout rlVideo;
    @BindView(R.id.iv_download)
    ImageView ivDownload;
    private boolean isPlay = true, isInitializeVideoPlayer = true, isPlayerTouchEnabled = false;
    private Handler durationUpdateHandler, touchHandler;
    private UpdateProgressAndDuration updateSeekBarThread;
    private TouchHandlerThread touchHandlerThread;
    private int height, width;
    private boolean isPortrait = true;
    private RelativeLayout.LayoutParams videolayoutParams;
    private SimpleExoPlayer player;
    private String videoUrl = "";
    private File file[];
    private boolean isVideoAvailable = false;
    private String thumbNail;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        ButterKnife.bind(this);
        ivDownload.setVisibility(View.GONE);
        getInitialHeightForVideoView();
        initVariables();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isPortrait = false;
            changeToLandscape();
        }
        if (isInitializeVideoPlayer)
            Glide.with(VideoviewActivity.this).load(getIntent().getStringExtra(Constants.VIDEO_URL_THUMB)).asBitmap().centerCrop().placeholder(R.drawable.ic_placeholder).into(new BitmapImageViewTarget(ivThumb) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(VideoviewActivity.this.getResources(), resource);
                    circularBitmapDrawable.setCircular(false);
                    circularBitmapDrawable.setCornerRadius(10);
                    ivThumb.setImageDrawable(circularBitmapDrawable);
                }
            });
        playVideo(getIntent().getStringExtra(Constants.VIDEO_URL));


        videoUrl = getIntent().getStringExtra(Constants.VIDEO_URL);
        thumbNail=getIntent().getStringExtra(Constants.VIDEO_URL_THUMB);

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isPlayerTouchEnabled) {
                    flControllers.setVisibility(View.VISIBLE);
                    ivMainPlay.setVisibility(View.VISIBLE);
                    touchHandler.removeCallbacks(touchHandlerThread);
                    touchHandler.postDelayed(touchHandlerThread, 3000);
                }
                return false;
            }
        });


        File directory = VideoviewActivity.this.getDir("mydir", Context.MODE_PRIVATE);
        file = directory.listFiles();
        for (int i = 0; i < file.length; i++) {
            if (videoUrl.substring(videoUrl.lastIndexOf("/")+1).equalsIgnoreCase(file[i].getName())) {
                isVideoAvailable = true;
                break;
            } else
                isVideoAvailable = false;

        }
        if (isVideoAvailable) {
            ivDownload.setVisibility(View.GONE);
        } else {
            ivDownload.setVisibility(View.GONE);
        }
    }


    /*
   method to set/initialize the variables
    */
    private void initVariables() {
        videolayoutParams = (RelativeLayout.LayoutParams) rlVideo.getLayoutParams();
        durationUpdateHandler = new Handler();
        touchHandler = new Handler();
        updateSeekBarThread = new UpdateProgressAndDuration();
        touchHandlerThread = new TouchHandlerThread();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setFullScreen();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.zoom_out);
    }

    /**
     * this method is used to get the initial or default height of views for son/daughter branches
     */
    private void getInitialHeightForVideoView() {
        ViewTreeObserver vto1 = rlVideo.getViewTreeObserver();
        vto1.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                rlVideo.getViewTreeObserver().removeOnPreDrawListener(this);
                height = rlVideo.getHeight();
                width = rlVideo.getWidth();
                //videoView.setDimensions(width, height);
                return true;
            }
        });
    }

    @OnClick({R.id.iv_fullscreen, R.id.iv_main_play})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_fullscreen:
                if (isPortrait) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    changeToLandscape();
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    changeToPortrait();
                }
                break;
            case R.id.iv_main_play:
                if (isInitializeVideoPlayer) {
                    playVideo(getIntent().getStringExtra(Constants.VIDEO_URL));
                } else
                    doPause();
                break;
        }
    }


    /**
     * this method is used to play the video
     *
     * @param path
     */
    private void playVideo(String path) {
        ivThumb.setVisibility(View.GONE);
        circularProgressbar.setVisibility(View.VISIBLE);
        ivMainPlay.setVisibility(View.GONE);
        ivMainPlay.setImageResource(R.drawable.ic_home_pause);
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(VideoviewActivity.this, trackSelector);
        videoView.setPlayer(player);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(VideoviewActivity.this, Util.getUserAgent(VideoviewActivity.this, "shopoholic"), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(path), dataSourceFactory, extractorsFactory, null, null);

        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                durationUpdateHandler.removeCallbacks(updateSeekBarThread);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress());
                durationUpdateHandler.postDelayed(updateSeekBarThread, 50);

            }
        });
        player.addListener(eventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
            ivMainPlay.setImageResource(R.drawable.ic_home_play);
            touchHandler.removeCallbacks(touchHandlerThread);
            rlBottomControllers.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (player != null) {
            player.setPlayWhenReady(true);
            ivMainPlay.setImageResource(R.drawable.ic_home_pause);
            touchHandler.postDelayed(touchHandlerThread, 2500);
            rlBottomControllers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            changeToLandscape();
        } else {
            changeToPortrait();
        }
    }

    /**
     * this method is used to hide the status bar and set full screen view
     */
    private void setFullScreen() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * this method is used to show the status bar and hide full screen view
     */
    private void showStatusBar() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            // show the status bar.
            int uiOptions = 0;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * this method is used to reset the controllers on video completion or error in playing video
     */
    private void resetControllers() {
        durationUpdateHandler.removeCallbacks(updateSeekBarThread);
        touchHandler.removeCallbacks(touchHandlerThread);
        isInitializeVideoPlayer = true;
        isPlayerTouchEnabled = false;
        isPlay = false;
        circularProgressbar.setVisibility(View.GONE);
        ivThumb.setVisibility(View.VISIBLE);
        ivMainPlay.setImageResource(R.drawable.ic_home_play);
        ivMainPlay.setVisibility(View.VISIBLE);
        flControllers.setVisibility(View.VISIBLE);
        rlBottomControllers.setVisibility(View.GONE);
    }


    /**
     * this method is used to change the video player to landscape mode
     */
    private void changeToLandscape() {
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        videolayoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        videolayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        videolayoutParams.setMargins(0, 0, 0, 0);
        rlVideo.setLayoutParams(videolayoutParams);
        setFullScreen();
        isPortrait = false;
        // ivFullscreen.setImageResource(R.drawable.ic_7_3_new_stand_video_fullscreen);
    }

    /**
     * this method is used to change the video player to portrait mode
     */
    private void changeToPortrait() {
        videolayoutParams.height = height;
        videolayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        videolayoutParams.setMargins(0, 0, 0, 0);
        rlVideo.setLayoutParams(videolayoutParams);
        //initializePlayer();
        isPortrait = true;
        showStatusBar();
        //ivFullscreen.setImageResource(R.drawable.ic_7_3_new_stand_video_fullscreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        }, 50);
    }

    // When user click to "Pause".
    public void doPause() {
        if (!isPlay) {
            isPlay = true;
            player.setPlayWhenReady(true);
            ivMainPlay.setImageResource(R.drawable.ic_home_pause);
            ivThumb.setVisibility(View.GONE);
        } else {
            isPlay = false;
            player.setPlayWhenReady(false);
            ivMainPlay.setImageResource(R.drawable.ic_home_play);
        }
    }

    // Convert millisecond to string.
    @SuppressLint("DefaultLocale")
    private String millisecondsToString(int milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        );
//        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
//        long seconds = TimeUnit.MILLISECONDS.toSeconds((long) milliseconds);
//        return minutes + ":" + seconds;
    }


    // Thread to Update position for SeekBar.
    class UpdateProgressAndDuration implements Runnable {

        public void run() {
            int currentPosition = (int) player.getCurrentPosition();
            String currentPositionStr = millisecondsToString(currentPosition);
            tvCurrentDuration.setText(currentPositionStr);
            progressBar.setProgress(currentPosition);
            progressBar.setSecondaryProgress((int) player.getBufferedPosition());
            // Delay thread 50 milisecond.
            durationUpdateHandler.postDelayed(this, 50);
        }
    }

    // Thread to manage touch event on video view
    class TouchHandlerThread implements Runnable {

        public void run() {
            flControllers.setVisibility(View.GONE);
            // Delay thread 50 milisecond.
            //touchHandler.postDelayed(this, 50);
        }
    }


    /**
     * used to add listener to the exoplayer to get callbacks
     */
    ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case ExoPlayer.STATE_BUFFERING:
                    circularProgressbar.setVisibility(View.VISIBLE);
                    break;
                case ExoPlayer.STATE_ENDED:
                    player.seekTo(0);
                    resetControllers();
                    videoView.getPlayer().setPlayWhenReady(false);
                    player.removeListener(eventListener);
                    finish();
                    overridePendingTransition(0, R.anim.zoom_out);
                    break;
                case ExoPlayer.STATE_IDLE:
                    break;
                case ExoPlayer.STATE_READY:
                    circularProgressbar.setVisibility(View.GONE);
                    //isPlay = true;
                    isPlayerTouchEnabled = true;
                    isInitializeVideoPlayer = false;
                    //circularProgressbar.setVisibility(View.GONE);
                    ivMainPlay.setVisibility(View.VISIBLE);
                    ivMainPlay.setImageResource(R.drawable.ic_home_pause);
                    flControllers.setVisibility(View.VISIBLE);
                    rlBottomControllers.setVisibility(View.VISIBLE);
                    //progressBar.setMax(videoView.getDuration());
                    //durationUpdateHandler.postDelayed(updateSeekBarThread, 50);
                    touchHandler.postDelayed(touchHandlerThread, 2500);
                    progressBar.setMax((int) videoView.getPlayer().getDuration());
                    tvFinalDuration.setText(millisecondsToString((int) videoView.getPlayer().getDuration()));
                    progressBar.setSecondaryProgress((int) videoView.getPlayer().getBufferedPosition());
                    durationUpdateHandler.postDelayed(updateSeekBarThread, 50);
//                        b.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            resetControllers();
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }

    };
}