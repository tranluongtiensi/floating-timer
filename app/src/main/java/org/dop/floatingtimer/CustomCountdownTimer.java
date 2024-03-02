package org.dop.floatingtimer;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class CustomCountdownTimer {

    private CountDownTimer countDownTimer;
    private TextView timeCountDown;
    private ProgressBar progressBar;
    private boolean isPaused;
    private long timeLeftInMillis;
    private long countDownInterval;

    public CustomCountdownTimer(long millisInFuture, long countDownInterval, TextView mTimeCountdown, ProgressBar progressBar) {
        this.timeCountDown = mTimeCountdown;
        this.progressBar = progressBar;
        this.isPaused = false;
        this.timeLeftInMillis = millisInFuture;
        this.countDownInterval = countDownInterval;

        createCountDownTimer();
    }

    private void createCountDownTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isPaused) {
                    timeLeftInMillis = millisUntilFinished;
                    int minutes = (int) ((millisUntilFinished / 1000) / 60);
                    int seconds = (int) ((millisUntilFinished / 1000) % 60);
                    //update time in timer
                    String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                    timeCountDown.setText(time);
                    //update progress bar
                    progressBar.setProgress((int) millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                // Handle countdown finish
                timeCountDown.setText("00:00");
                // Add any actions you want to perform when the countdown finishes
            }
        };
    }

    public void start() {
        countDownTimer.start();
    }

    public void pause() {
        isPaused = true;
        countDownTimer.cancel();
    }

    public void resumeFromPause() {
        isPaused = false;
        createCountDownTimer();
        countDownTimer.start();
    }

    public void onStop() {
        isPaused = true;
        countDownTimer.cancel();
    }
}
