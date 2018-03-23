package com.borealos.chathead;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
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
    private View chatHead;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public ChatHeadService() {

    }


    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatHead = LayoutInflater.from(this).inflate(R.layout.chathead, null);


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

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();

                        lastAction = motionEvent.getAction();

                        return true;

                    case MotionEvent.ACTION_UP:

                        //calculate diff if it is a move or a touch
                        int diffX = (int) (motionEvent.getRawX() - initialTouchX);
                        int diffY = (int) (motionEvent.getRawY() - initialTouchY);

                        if ((diffX < 5 && diffY < 5) && lastAction == MotionEvent.ACTION_DOWN) {

                            //if (lastAction == MotionEvent.ACTION_DOWN) { //It is a click(touch) --> go to chat

                            /*Intent intent = new Intent(ChatHeadService.this, ChatActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);*/

                                Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT).show();
                                stopSelf(); //stop service and remove chat head

                        }

                        lastAction = motionEvent.getAction();
                        return true;

                    case MotionEvent.ACTION_MOVE:

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

        if (chatHead != null) mWindowManager.removeView(chatHead);
    }
}