package com.penkov.screendreamer;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Calendar;
import java.util.Locale;

public class DreamerView
{
    private final Context mContext;

    @SuppressWarnings("FieldCanBeLocal")
    private final View mView;    // For future implementation

    private final CircleLoaderView mChargingView;
    private final TextView mTimeTextView;
    private final TextView mDateTextView;
    private final TextView mAlarmTextView;
    private final TextView mPercentTextView;

    private final Calendar nextAlarm;

    private UpdateReceiver mReceiver;

    public DreamerView (Context context, View view)
    {
        mContext = context;
        mView = view;

        mChargingView = new CircleLoaderView(mView.findViewById(R.id.chargingImageView), context);

        mTimeTextView = mView.findViewById(R.id.timeTextView);
        mDateTextView = mView.findViewById(R.id.dateTextView);
        mAlarmTextView = mView.findViewById(R.id.alarmTextView);
        mPercentTextView = mView.findViewById(R.id.percentTextView);

        nextAlarm = getNextAlarm();
        init();
    }

    private void init ()
    {
        if (nextAlarm != null)
            addAlarmText();
        else
            removeAlarmText();

        float percent = getBatteryPercentage();

        setTimeAndDate();
        setAlarm();
        setChargingImage(percent);
        setPercentText(percent);
    }

    private Calendar getNextAlarm ()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return null;

        AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo nextAlarmClock = alarmMgr.getNextAlarmClock();

        if (nextAlarmClock == null)
            return null;

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.setTimeInMillis(nextAlarmClock.getTriggerTime());

        return alarmTime;
    }

    private void removeAlarmText ()
    {
        mAlarmTextView.setVisibility(View.INVISIBLE);
        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) mTimeTextView.getLayoutParams();
        params.verticalBias = 0.5f;
        mTimeTextView.setLayoutParams(params);
    }

    private void addAlarmText ()
    {
        mAlarmTextView.setVisibility(View.VISIBLE);
        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) mTimeTextView.getLayoutParams();
        params.horizontalBias = 0.5f;
        mTimeTextView.setLayoutParams(params);
    }

    private void setAlarm ()
    {
        if (nextAlarm == null)
            return;

        Calendar oneDayAHead = Calendar.getInstance();
        oneDayAHead.add(Calendar.HOUR, 24);

        if (nextAlarm.before(oneDayAHead))
            mAlarmTextView.setText(String.format(Locale.ENGLISH,
                    "%1$tk:%1$tM ", nextAlarm));
        else
            mAlarmTextView.setText(String.format(Locale.ENGLISH,
                "%1$ta %1$tk:%1$tM ", nextAlarm));
    }

    private void setTimeAndDate ()
    {
        Calendar currentTime = Calendar.getInstance();

        mTimeTextView.setText(String.format(Locale.ENGLISH,
                "%1$tR", currentTime));
        mDateTextView.setText(String.format(Locale.ENGLISH,
                "%1$ta, %1$te %1$tb", currentTime));
    }

    private void setChargingImage (float percent)
    {
        mChargingView.drawCircle(percent);
    }

    private void setPercentText (float percent)
    {
        mPercentTextView.setText(String.format(Locale.ENGLISH,
                "%d%%", (int) (percent * 100.0f)));
    }

    private float getBatteryPercentage ()
    {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, filter);

        if (batteryStatus == null)
            return 0;

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return (float) level / scale;
    }

    public void registerUpdateReceiver ()
    {
        if (mReceiver != null)
            unregisterUpdateReceiver();

        mReceiver = new UpdateReceiver(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);

        mContext.registerReceiver(mReceiver, filter);
    }

    public void unregisterUpdateReceiver ()
    {
        if (mReceiver == null)
            return;

        mContext.unregisterReceiver(mReceiver);
        mReceiver = null;
    }

    private static class UpdateReceiver extends BroadcastReceiver {

        private final DreamerView mDreamerView;

        public UpdateReceiver(DreamerView view) {
            mDreamerView = view;
        }

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(Intent.ACTION_TIME_TICK))
            {
                mDreamerView.setTimeAndDate();
                mDreamerView.setAlarm();
            }
            else if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED))
            {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float percent = (float) level / scale;

                mDreamerView.setChargingImage(percent);
                mDreamerView.setPercentText(percent);
            }
            else
            {
                float percent = mDreamerView.getBatteryPercentage();

                mDreamerView.setTimeAndDate();
                mDreamerView.setAlarm();
                mDreamerView.setChargingImage(percent);
                mDreamerView.setPercentText(percent);
            }
        }
    }

}
