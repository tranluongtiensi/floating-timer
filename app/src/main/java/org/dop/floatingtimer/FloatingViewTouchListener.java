package org.dop.floatingtimer;


import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class FloatingViewTouchListener implements View.OnTouchListener {
    private WindowManager.LayoutParams params;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private WindowManager mWindowManager;
    private CustomCountdownTimer countdownTimer;
    private boolean counting = true;

    public FloatingViewTouchListener(WindowManager.LayoutParams params, WindowManager windowManager, CustomCountdownTimer countdownTimer) {
        super();
        this.params = params;
        this.mWindowManager = windowManager;
        this.countdownTimer = countdownTimer;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //remember the initial position.
                initialX = params.x;
                initialY = params.y;

                //get the touch location
                initialTouchX = motionEvent.getRawX();
                initialTouchY = motionEvent.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                int Xdiff = (int) (motionEvent.getRawX() - initialTouchX);
                int Ydiff = (int) (motionEvent.getRawY() - initialTouchY);

                //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                //So that is click event.
                //TODO: GIẢI QUYẾT ONCLICK TIMER
                if (Xdiff < 10 && Ydiff < 10) {
                    counting = !counting;
                    if (counting) {
                        // Bắt đầu đếm tiếp
                        if (countdownTimer != null) {
                            countdownTimer.onTick(countdownTimer.tosave);
                            countdownTimer.start();
                        }
                    } else {
                        // Dừng đếm tiếp
                        if (countdownTimer != null) {
                            countdownTimer.onStop();
                        }
                    }
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                //Calculate the X and Y coordinates of the view.
                params.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                params.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);

                //Update the layout with new X & Y coordinate
                // Note: mWindowManager should be accessible here
                mWindowManager.updateViewLayout(view, params);
                return true;
        }
        return false;
    }
}
