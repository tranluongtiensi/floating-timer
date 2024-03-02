package org.dop.floatingtimer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FloatingViewService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private WindowManager.LayoutParams params;
    private long mMinutesInMilis, mSecondInMilis;

    private String timeFormatted;
    private long mStartTimerInMilis;
    private ProgressBar countdownProBar;
    int LAYOUT_FLAG;
    TextView mTimeCountdown;
    TextView mTimeStopwatch;
    Handler handler;
    private CustomCountdownTimer countdownTimer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public WindowManager getmWindowManager() {
        return mWindowManager;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            );
        }
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);
        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;


        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        String action = intent.getStringExtra("action");
        if(action != null){
            if(action.equals("countdown")){
                    mTimeCountdown = mFloatingView.findViewById(R.id.time_txt);
                    String minutesValue = intent.getStringExtra("minutes");
                    String secondsValue = intent.getStringExtra("seconds");



                    mMinutesInMilis = Long.parseLong(minutesValue) * 60000;
                    mSecondInMilis = Long.parseLong(secondsValue) * 1000;

                    int Minutes = (int) ((mMinutesInMilis / 1000) % 3600) / 60;
                    int Seconds = (int) (mSecondInMilis / 1000) % 60;

                    timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", Minutes, Seconds);
                            //set time for countdown timer
                    mTimeCountdown.setText(timeFormatted);
                            // Initialize and start countdown timer
                    mStartTimerInMilis = mMinutesInMilis + mSecondInMilis;

                    countdownProBar = mFloatingView.findViewById(R.id.countdown_probar);
                    countdownProBar.setMax((int) mStartTimerInMilis);
                    countdownProBar.setProgress((int) mStartTimerInMilis);

                    // nếu countdownTimer chưa được khởi tạo, tạo mới nó và bắt đầu
                    countdownTimer = new CustomCountdownTimer(mStartTimerInMilis, 1000, mTimeCountdown, countdownProBar);
                    //drag movement for widget
                    countdownTimer.start();

                    FloatingViewTouchListener touchListener = new FloatingViewTouchListener(params, getmWindowManager(), countdownTimer);
                    mFloatingView.setOnTouchListener(touchListener);



            } else if (action.equals("stopwatch")) {
                    mTimeStopwatch = mFloatingView.findViewById(R.id.time_txt);
                    CustomStopwatchTimer customStopwatchTimer = new CustomStopwatchTimer(mTimeStopwatch);
                    customStopwatchTimer.Start();
            }
        }



        return START_STICKY;
    }





    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            stopSelf();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startForeground(1, new Notification());
        }


//    public void onDestroy() {
//        super.onDestroy();
//        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
//    }
    }

    private void startMyOwnForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String NOTIFICATION_CHANNEL_ID = getPackageName();
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("App is running in background")
                    .setContentText("Your additional text here")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }
    }
}


