package com.chiaruy.autoclick;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLDisplay;

public class MainFloat extends FrameLayout implements View.OnClickListener {
    private Context mContext;
    private View mView_add;
    //private View mView_click;
    private View[] mView_click = new View[100];
    private int[] mView_click_LocationX = new int[100];
    private int[] mView_click_LocationY = new int[100];
    private float[] mView_click_LocationTime = new float[100];
    private View mView_edit;
    private WindowManager mWindowManager, mEditWindowManager;
    private WindowManager.LayoutParams mWindowParams_add, mWindowParams_click, mWindowParams_edit;
    private int SizeX, SizeY;
    private int MoveTime;
    private int number_clicker = 0;
    private ImageView mImage_add, mImage_play;
    private boolean Play = false;

    public MainFloat(Context context) {
        super(context);
        mContext = context.getApplicationContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        mView_add = mLayoutInflater.inflate(R.layout.floating_add, null);
        mImage_add = (ImageView) mView_add.findViewById(R.id.add);
        mImage_play = (ImageView) mView_add.findViewById(R.id.play);
        mView_add.setOnTouchListener(mOnTouchListener_add);
        mImage_add.setOnClickListener(this);
        mImage_play.setOnClickListener(this);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mEditWindowManager = (WindowManager)
                mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = mWindowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        SizeX = point.x;
        SizeY = point.y;
        mWindowParams_add = show(mView_add, Gravity.CENTER, SizeX / 2, SizeY / 2);

    }

    public WindowManager.LayoutParams show(View mView, int gravity, int x, int y) {

        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = gravity;
        mWindowParams.x = x;
        mWindowParams.y = y;
        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mWindowParams.width = LayoutParams.WRAP_CONTENT;
        mWindowParams.height = LayoutParams.WRAP_CONTENT;
        if (Build.VERSION.SDK_INT >= 26) {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        try {
            mWindowManager.addView(mView, mWindowParams);
        } catch (Exception e) {
            Log.d("Exception", "addView");
            e.printStackTrace();
        }
        return mWindowParams;
    }


    public WindowManager.LayoutParams show_edit(View mView, int gravity, int x, int y) {
        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = gravity;
        mWindowParams.x = x;
        mWindowParams.y = y;
        mWindowParams.flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mWindowParams.width = LayoutParams.WRAP_CONTENT;
        mWindowParams.height = LayoutParams.WRAP_CONTENT;
        if (Build.VERSION.SDK_INT >= 26) {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        try {
            mEditWindowManager.addView(mView, mWindowParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mWindowParams;
    }

    private OnTouchListener mOnTouchListener_add = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mWindowParams_add.x = (int) event.getRawX() - SizeX / 2;
                    mWindowParams_add.y = (int) event.getRawY() - SizeY / 2;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d("MainFloat", "Add_ACTION_MOVE");
                    MoveTime += 1;
                    mWindowParams_add.x = (int) event.getRawX() - SizeX / 2;
                    mWindowParams_add.y = (int) event.getRawY() - SizeY / 2;
                    try {
                        mWindowManager.updateViewLayout(view, mWindowParams_add);
                    } catch (Exception e) {
                        Log.d("Exception", "Add_MotionEvent");
                        e.printStackTrace();
                    }
                    break;
                case MotionEvent.ACTION_UP:
            }
            return true;
        }
    };


    private OnTouchListener mOnTouchListener_click = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    MoveTime = 0;
                    Log.d("MainFloat", "Click_ACTION_DOWN");
                    if (view.getTag() == null) {
                        view.setTag(number_clicker-1);
                    }
                    Log.d("MainFloat_viewID", String.valueOf(view.getTag()));
                    mWindowParams_click.x = (int) event.getRawX() - SizeX / 2;
                    mWindowParams_click.y = (int) event.getRawY() - SizeY / 2;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d("MainFloat", "Click_ACTION_MOVE");
                    MoveTime += 1;
                    mWindowParams_click.x = (int) event.getRawX() - SizeX / 2;
                    mWindowParams_click.y = (int) event.getRawY() - SizeY / 2;
                    try {
                        mWindowManager.updateViewLayout(view, mWindowParams_click);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    Log.d("MainFloat_X_"+ view.getTag().toString(), String.valueOf(location[0]));
                    Log.d("MainFloat_Y_"+ view.getTag().toString(), String.valueOf(location[1]));
                    mView_click_LocationX[(int) view.getTag()] = location[0] - 1;
                    mView_click_LocationY[(int) view.getTag()] = location[1] - 1;
                    if (MoveTime < 5) {
                        LayoutInflater mLayoutInflater_click = LayoutInflater.from(mContext);
                        mView_edit = mLayoutInflater_click.inflate(R.layout.clicker_windows, null);
                        TextView mText = (TextView) mView_edit.findViewById(R.id.Number);
                        mText.setText("Number " + String.valueOf(Integer.parseInt(String.valueOf(view.getTag()))+1));
                        final EditText mEdit = (EditText) mView_edit.findViewById(R.id.time);
                        if(mView_click_LocationTime[(int) view.getTag()]!=10){
                            mEdit.setText(String.valueOf(mView_click_LocationTime[(int) view.getTag()]));
                        }
                        mEdit.setFocusable(true);
                        Button mOk = (Button) mView_edit.findViewById(R.id.ok);
                        Button mCancel = (Button) mView_edit.findViewById(R.id.cancel);
                        mOk.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mView_click_LocationTime[(int) view.getTag()] =  Float.parseFloat(mEdit.getText().toString());
                                mEditWindowManager.removeViewImmediate(mView_edit);
                            }
                        });
                        mCancel.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mEditWindowManager.removeViewImmediate(mView_edit);
                            }
                        });
                        mWindowParams_edit = show_edit(mView_edit, Gravity.CENTER, 0, 0);

                    }
                    break;
            }
            return true;
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), AutoService.class);
        switch (v.getId()) {
            case R.id.play:
                if (!Play) {
                    Play = true;
                    mImage_play.setImageDrawable(getResources().getDrawable(R.drawable.stop, null));
                    mImage_add.setVisibility(View.GONE);
                    for (int i = 0; i < number_clicker; i++) {
                        mView_click[i].setVisibility(GONE);
                    }
                    intent.putExtra("number_clicker", number_clicker);
                    intent.putExtra("clicker_LocationX", mView_click_LocationX);
                    intent.putExtra("clicker_LocationY", mView_click_LocationY);
                    intent.putExtra("clicker_Time", mView_click_LocationTime);
                    intent.putExtra(AutoService.ACTION, AutoService.PLAY);
                    getContext().startService(intent);
                    Log.d("MainFloat","Start");
                } else {
                    Play = false;
                    mImage_play.setImageDrawable(getResources().getDrawable(R.drawable.play, null));
                    intent.putExtra(AutoService.ACTION, AutoService.STOP);
                    getContext().startService(intent);
                    mImage_add.setVisibility(View.VISIBLE);
                    for (int i = 0; i < number_clicker; i++) {
                        mView_click[i].setVisibility(VISIBLE);
                    }

                }
                break;
            case R.id.add:
                LayoutInflater mLayoutInflater_click = LayoutInflater.from(mContext);
                mView_click[number_clicker] = (View) mLayoutInflater_click.inflate(R.layout.floating_click, null);
                mView_click[number_clicker].setOnTouchListener(mOnTouchListener_click);
                mWindowParams_click = show(mView_click[number_clicker], Gravity.CENTER, 0, 0);
                mView_click_LocationTime[number_clicker] = 10;
                number_clicker += 1;
                break;
        }
    }
}
