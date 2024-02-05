package org.dop.floatingtimer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText mMinutesEditTxt, mSecondsEditTxt;
    Button mCreateCountdownBtn, mCreateStopWatchBtn;
    Toolbar toolbar;
    private ActivityResultLauncher<Intent> requestOverlayPermissionLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCreateCountdownBtn = findViewById(R.id.create_countdown_btn);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestOverlayPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (!Settings.canDrawOverlays(MainActivity.this)) {
                            Toast.makeText(MainActivity.this, "Permission denied by user", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        getPermission();

        mCreateCountdownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Settings.canDrawOverlays(MainActivity.this)){
                    getPermission();
                }

                mMinutesEditTxt = findViewById(R.id.minutes_edit_text);
                mSecondsEditTxt = findViewById(R.id.seconds_edit_text);

                String mInputMinutes = mMinutesEditTxt.getText().toString();
                String mInputSeconds = mSecondsEditTxt.getText().toString();

                if(mInputSeconds.length() == 0){
                    mInputSeconds = String.valueOf(0);
                }
                if(mInputMinutes.length() == 0){
                    mInputMinutes = String.valueOf(0);
                }

                Intent intent = new Intent(MainActivity.this, FloatingViewService.class);
                intent.putExtra("minutes", mInputMinutes);
                intent.putExtra("seconds", mInputSeconds);
                intent.putExtra("action", "countdown");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }

            }
        });
        mCreateStopWatchBtn = findViewById(R.id.create_stopwatch_btn);
        mCreateStopWatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Settings.canDrawOverlays(MainActivity.this)){
                    getPermission();
                }
                Intent intent = new Intent(MainActivity.this, FloatingViewService.class);
                intent.putExtra("action","stopwatch");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            }
        });



    }

    public void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            requestOverlayPermissionLauncher.launch(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.opt_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}