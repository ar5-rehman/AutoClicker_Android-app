package com.chiaruy.autoclick;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;


public class AutoService extends AccessibilityService {
    private MainFloat mMainFloat;
    public static final String ACTION = "action";
    public static final String PLAY = "play";
    public static final String STOP = "stop";
    private int number_clicker;
    private int[] mView_click_LocationX = new int[100];
    private int[] mView_click_LocationY = new int[100];
    private float[] mView_click_Time = new float[100];
    private Handler mHandler;
    private int clicker_now = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mMainFloat = new MainFloat(this);
        HandlerThread handlerThread = new HandlerThread("auto-handler");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getStringExtra(ACTION);
            if (PLAY.equals(action)) {
                Log.d("AutoService", action);
                number_clicker = intent.getIntExtra("number_clicker", 0);
                if (number_clicker != 0) {
                    mView_click_LocationX = intent.getIntArrayExtra("clicker_LocationX");
                    mView_click_LocationY = intent.getIntArrayExtra("clicker_LocationY");
                    mView_click_Time = intent.getFloatArrayExtra("clicker_Time");
                    for (int i = 0; i < number_clicker; i++) {
                        Log.d("mView_click_LocationX", String.valueOf(mView_click_LocationX[i]));
                        Log.d("mView_click_LocationY", String.valueOf(mView_click_LocationY[i]));
                        Log.d("mView_click_Time", String.valueOf(mView_click_Time[i]));

                    }
                    if (mRunnable == null) {
                        mRunnable = new IntervalRunnable();
                    }
                    mHandler.postDelayed(mRunnable, (long)0);
                    Toast.makeText(getBaseContext(), "開始", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "請設定點擊點", Toast.LENGTH_SHORT).show();
                }
            } else if (STOP.equals(action)) {
                mHandler.removeCallbacksAndMessages(null);
                Toast.makeText(getBaseContext(), "停止", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    private void playTap() {
        Path path = new Path();
        path.moveTo(mView_click_LocationX[clicker_now], mView_click_LocationY[clicker_now]);
        path.moveTo(mView_click_LocationX[clicker_now], mView_click_LocationY[clicker_now]);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 10L, 10L));
        GestureDescription gestureDescription = builder.build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                clicker_now += 1;
                if(clicker_now==number_clicker){
                    clicker_now = 0;
                }
                mHandler.postDelayed(mRunnable, (long) mView_click_Time[clicker_now]);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
            }
        }, null);
    }

    private IntervalRunnable mRunnable;

    private class IntervalRunnable implements Runnable {
        @Override
        public void run() {
            playTap();
        }
    }


}
