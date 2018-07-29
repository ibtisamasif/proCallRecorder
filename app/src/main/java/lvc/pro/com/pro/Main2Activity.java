package lvc.pro.com.pro;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.callrecorder.pro.R;

import java.io.IOException;

import lvc.pro.com.pro.constants.Constants;
import lvc.pro.com.pro.utility.SharedPreferenceUtility;

import static lvc.pro.com.pro.contacts.ContactProvider.getFolderPath;

public class Main2Activity extends AppCompatActivity {
    public static final String TAG = "Main2Activity";

    FloatingActionButton buttonPlayPause;
    SeekBar seekBarProgress;
    MediaPlayer mediaPlayer;
    Handler seekHandler = new Handler();
    private int mediaFileLengthInMilliseconds;
    TextView title;
    String path;
    public static boolean mIsDestroying = false;

//    NativeExpressAdView mAdView;
//    VideoController mVideoController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musicplayer);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        path = getIntent().getStringExtra("PATH");
        title = findViewById(R.id.name);
        title.setText(path);
        getInit();
        ads();
    }

    private void ads() {
//        // Locate the NativeExpressAdView.
//        mAdView = findViewById(R.id.adView);
//
//        // Set its video options.
//        mAdView.setVideoOptions(new VideoOptions.Builder()
//                .setStartMuted(true)
//                .build());
//
//        // The VideoController can be used to get lifecycle events and info about an ad's video
//        // asset. One will always be returned by getVideoController, even if the ad has no video
//        // asset.
//        mVideoController = mAdView.getVideoController();
//        mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
//            @Override
//            public void onVideoEnd() {
//                Log.d(TAG, "Video playback is finished.");
//                super.onVideoEnd();
//            }
//        });
//
//        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
//        // loading.
//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                if (mVideoController.hasVideoContent()) {
//                    Log.d(TAG, "Received an ad that contains a video asset.");
//                } else {
//                    Log.d(TAG, "Received an ad that does not contain a video asset.");
//                }
//            }
//        });
//
//        mAdView.loadAd(new AdRequest.Builder().build());
    }

    public void getInit() {
        buttonPlayPause = (FloatingActionButton) findViewById(R.id.button1);
        seekBarProgress = (SeekBar) findViewById(R.id.seekBar2);
        seekBarProgress.setMax(99);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getFolderPath(this) + "/" + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekUpdation();
            mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!mediaPlayer.isPlaying()) {
            buttonPlayPause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
        } else {
            buttonPlayPause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
        }
        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    buttonPlayPause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                } else {
                    mediaPlayer.pause();
                    buttonPlayPause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                }
                seekUpdation();
            }
        });
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!mediaPlayer.isPlaying()) {
                    int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * seekBarProgress.getProgress();
                    mediaPlayer.seekTo(playPositionInMillisecconds);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start();
                if (!mediaPlayer.isPlaying()) {
                    buttonPlayPause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                } else {
                    buttonPlayPause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                }
                seekUpdation();
            }
        });
    }

    private void seekUpdation() {
        seekBarProgress.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    seekUpdation();
                }
            };
            seekHandler.postDelayed(notification, 1000);
        } else {
            buttonPlayPause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsDestroying = true;
        SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Constants.sFROM_LISTEN_TO_MAIN) {
            if (SharedPreferenceUtility.getLockActivatedStatus(getApplicationContext())) {
                if ((SharedPreferenceUtility.getBackgroundStatus(getApplicationContext()))
                        && (!(Constants.sIS_FROM_ANOTHER_ACTIVITY))) {
                    Constants.sIS_FROM_BACKGROUND = true;
                    Intent intent = new Intent(Main2Activity.this, NewPinLock.class);
                    startActivity(intent);
                }
            }
        }
        Constants.sFROM_LISTEN_TO_MAIN = false;
        Constants.sIS_FROM_ANOTHER_ACTIVITY = false;
        mIsDestroying = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.sIS_FROM_ANOTHER_ACTIVITY = true;
        // mIsDestroying=false;
        SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Constants.sIS_FROM_ANOTHER_ACTIVITY = true;
        SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), false);
    }
}
