package com.dyh.android.roundcornerprogressbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.dyh.android.roundcornerprogressbar.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding activityMainBinding;
    Handler handler = new Handler();
    int progress = 0;
    Runnable progressRun = new Runnable() {
        @Override
        public void run() {
            progress+=1;
            if(progress > 100){
                progress = 0;
            }
            handler.postDelayed(progressRun, 50);
            activityMainBinding.mTileImageProgressBar1.setProgress(progress);
            activityMainBinding.mTileImageProgressBar2.setProgress(progress);
            activityMainBinding.mTileImageProgressBar3.setProgress(progress);
            activityMainBinding.mTileImageProgressBar4.setProgress(progress);
            activityMainBinding.mTileImageProgressBar5.setProgress(progress);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(progressRun);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding =
                ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

//        activityMainBinding.mSelectableRoundedImageView.setImageResource(R.mipmap.icon_progressbar_red);
        activityMainBinding.mTileImageProgressBar1.post(progressRun);
    }
}