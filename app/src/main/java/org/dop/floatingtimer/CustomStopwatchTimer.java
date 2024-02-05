package org.dop.floatingtimer;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.TextView;

import java.util.Locale;

public class CustomStopwatchTimer {
    private Handler internalHandler;
    private final TextView timeStopWatch;

    public CustomStopwatchTimer(TextView mTimeStopWatch) {
        this.timeStopWatch = mTimeStopWatch;
    }

    public void Start(){
        internalHandler = new Handler(Looper.getMainLooper());
        Stopwatch stopwatch = new Stopwatch();
        internalHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long elapsedTime = stopwatch.getElapsedTime();
                int Seconds = (int) (elapsedTime / 1000) %60;
                int Minutes = (int) (elapsedTime / 1000) / 60;
                String time = String.format(Locale.getDefault(), "%02d:%02d", Minutes, Seconds);
                //set time for countdown timer
                timeStopWatch.setText(time);
                internalHandler.postDelayed(this,1000);
            }
        }, 0);
    }
    public void Stop(){
        
    }
    private static class Stopwatch {
        private final long startTime;

        public Stopwatch(){
            startTime = SystemClock.uptimeMillis();
        }

        public long getElapsedTime(){
            return SystemClock.uptimeMillis() - startTime;
        }
    }

}
