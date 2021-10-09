package com.penkov.screendreamer;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.service.dreams.DreamService;
import android.view.MotionEvent;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

public class Dreamer extends DreamService implements View.OnClickListener {

    private DreamerView dreamerView;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Wake up upon touch
        setInteractive(true);
        // Dim screen
        setScreenBright(true);
        // Set layout
        View view = View.inflate(this, R.layout.activity_dreamer, null);
        setContentView(view);

        // Sets DreamerView to handle the layout
        dreamerView = new DreamerView(this, view);

        // Hide system UI
        setFullscreen(true);

        view.setClickable(true);
        view.setOnClickListener(this);

        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }

        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        final int uiNewOptions = uiOptions;

        // Fix System navigation bar pop up back
        (new Handler(Looper.getMainLooper())).postDelayed(
                () -> getWindow().getDecorView().setSystemUiVisibility(uiNewOptions),
                200
        );
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        if (dreamerView != null)
            dreamerView.registerUpdateReceiver();
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        if (dreamerView != null)
            dreamerView.unregisterUpdateReceiver();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private long lastClick = 0;

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - lastClick > 300) {
            lastClick = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
