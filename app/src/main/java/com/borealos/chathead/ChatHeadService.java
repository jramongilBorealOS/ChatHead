package com.borealos.chathead;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by javierramongil on 22/3/18.
 */

public class ChatHeadService extends Service {

    private WindowManager mWindowManager;
    private View chatHead, removeView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatHead = LayoutInflater.from(this).inflate(R.layout.chathead, null);
        removeView = LayoutInflater.from(this).inflate(R.layout.remove, null);

        final WindowManager.LayoutParams paramsRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE, //allows it to be on top of the Window
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        paramsRemove.gravity = Gravity.BOTTOM | Gravity.CENTER;
        removeView.setVisibility(View.GONE);

        mWindowManager.addView(removeView, paramsRemove);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE, //allows it to be on top of the Window
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        mWindowManager.addView(chatHead, params);

        chatHead.setOnTouchListener(new View.OnTouchListener() {

            private int initialX;
            private int initialY;
            private int lastAction;
            private float initialTouchX;
            private float initialTouchY;
            private long initialTouchTime, finalTouchTime;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        initialTouchTime = System.currentTimeMillis();

                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();

                        lastAction = motionEvent.getAction();

                        return true;

                    case MotionEvent.ACTION_UP:

                        finalTouchTime = System.currentTimeMillis();

                        removeView.setVisibility(View.GONE);

                        //calculate diff if it is a move or a touch
                        int diffX = (int) (motionEvent.getRawX() - initialTouchX);
                        int diffY = (int) (motionEvent.getRawY() - initialTouchY);

                        Log.d("ChatHead", Integer.toString(diffX));
                        Log.d("ChatHead", Integer.toString(diffY));
                        Log.d("ChatHead", "Last: " + Integer.toString(lastAction));


                        if ((Math.abs(diffX) < 5 && Math.abs(diffY) < 5)) { //chat head not moved

                            if (finalTouchTime - initialTouchTime < 500) //It is a click(touch) --> go to chat

                            /*Intent intent = new Intent(ChatHeadService.this, ChatActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);*/

                                stopSelf(); //stop service and remove chat head (calls onDestroy)


                        }

                        lastAction = motionEvent.getAction();
                        return true;

                    case MotionEvent.ACTION_MOVE:

                        if (System.currentTimeMillis() - initialTouchTime >= 500)
                            removeView.setVisibility(View.VISIBLE);

                        params.x = (int) (initialX + motionEvent.getRawX() - initialTouchX);
                        params.y = (int) (initialY + motionEvent.getRawY() - initialTouchY);

                        //Update UI
                        mWindowManager.updateViewLayout(chatHead, params);

                        lastAction = motionEvent.getAction();

                        return true;

                }

               return false;

            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ChatHead", "onDestroy");
        if (chatHead != null) mWindowManager.removeView(chatHead);
        if (removeView != null) mWindowManager.removeView(removeView);
    }
}