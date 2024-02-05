package org.dop.floatingtimer;

import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

public class CustomCountdownTimer extends CountDownTimer {

    private TextView timeCountDown;
    private ProgressBar progressBar;
    public long tosave;
    public CustomCountdownTimer(long millisInFuture, long countDownInterval, TextView mTimeCountdown, ProgressBar progressBar) {
        super(millisInFuture, countDownInterval);
        this.timeCountDown = mTimeCountdown;
        this.progressBar = progressBar;
    }

    @Override
    public void onTick(long millisUntilFinished) {

        int minutes = (int) ((millisUntilFinished / 1000) / 60);
        int seconds = (int) ((millisUntilFinished / 1000) % 60);
        //update time in timer
        String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timeCountDown.setText(time);
        //update progress bar
        progressBar.setProgress((int) millisUntilFinished);
        tosave = millisUntilFinished;
    }
    public void onStop(){

        cancel();
    }

    @Override
    public void onFinish() {
        // Handle countdown finish
        timeCountDown.setText("00:00");
        // Add any actions you want to perform when the countdown finishes
    }
}
