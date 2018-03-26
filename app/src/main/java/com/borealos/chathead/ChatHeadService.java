package com.borealos.chathead;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by javierramongil on 22/3/18.
 */

public class ChatHeadService extends Service {

    private WindowManager mWindowManager;
    private View chatHead, removeView;
    private boolean insideRemove = false;
    private int initialRemoveY;
    private Point sizeWindow = new Point();
    private ImageView removeImg;
    private int remove_img_width, remove_img_height;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mWindowManager.getDefaultDisplay().getSize(sizeWindow);

        chatHead = LayoutInflater.from(this).inflate(R.layout.chathead, null);
        removeView = LayoutInflater.from(this).inflate(R.layout.remove, null);
        removeImg = removeView.findViewById(R.id.remove_img);

        final WindowManager.LayoutParams paramsRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE, //allows it to be on top of the Window
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        paramsRemove.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        //initialRemoveY = paramsRemove.y;
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

                        remove_img_width = removeImg.getLayoutParams().width;
                        remove_img_height = removeImg.getLayoutParams().height;

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


                        if (Math.abs(diffX) < 5 && Math.abs(diffY) < 5) { //chat head not moved

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

                        Log.d("ChatHead", String.valueOf(paramsRemove.x));
                        Log.d("ChatHead", String.valueOf(paramsRemove.y));

                        //Update UI
                        mWindowManager.updateViewLayout(chatHead, params);


                        if(chatHeadRemove(motionEvent.getRawX(), motionEvent.getRawY())) {

                            if (!insideRemove) {

                                Log.d("ChatHead", "Remove");

                            /*removeView.getLayoutParams().width *= 1.5;
                            removeView.getLayoutParams().height *= 1.5;*/

                                //paramsRemove.y += 20;

                                int removeX = (int) (sizeWindow.x - (remove_img_height * 1.5) / 2);
                                int removeY = (int) (sizeWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight()));

                                if(removeImg.getLayoutParams().height == remove_img_height){
                                    removeImg.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                    removeImg.getLayoutParams().width = (int) (remove_img_width * 1.5);

                                    //WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                    params.x = removeX;
                                    params.y = removeY;

                                    mWindowManager.updateViewLayout(chatHead, params);
                                }



                                params.x = removeX + (Math.abs(removeView.getWidth() - chatHead.getWidth()) / 2);
                                params.y = removeY + (Math.abs(removeView.getHeight() - chatHead.getHeight())) / 2;

                                insideRemove = true;

                                mWindowManager.updateViewLayout(chatHead, params);
                                mWindowManager.updateViewLayout(removeView, paramsRemove);
                                break;
                            }

                        } else  {

                            insideRemove = false;

                            //reset removeView aspect
                            paramsRemove.y = initialRemoveY;
                            removeImg.getLayoutParams().width = remove_img_width;
                            removeImg.getLayoutParams().height = remove_img_height;

                            mWindowManager.updateViewLayout(removeView, paramsRemove);



                        }



                        lastAction = motionEvent.getAction();

                        return true;

                }

               return false;

            }
        });

    }

    //detect chat head to be removed by user
    private boolean chatHeadRemove (float chatHeadX, float chatHeadY) {

        //int[] posChatHead = new int[2], posRemove = new int[2];
        //int chatHeadX, chatHeadY, removeX, removeY;

        //define boundaries to detect remove
        int boundLeft = (int) (sizeWindow.x / 2 - remove_img_width * 1.5);
        int boundRight =(int) (sizeWindow.x / 2 + remove_img_width * 1.5);
        int boundTop = (int) (sizeWindow.y - remove_img_height * 1.5);

        //chatHead.getLocationOnScreen(posChatHead);
        //removeView.getLocationOnScreen(posRemove);

        /*chatHeadX = posChatHead[0];
        chatHeadY = posChatHead[1];
        removeX = posRemove[0];
        removeY = posRemove[1];*/

        /*Log.d("ChatHead", "chatHeadX: " + chatHeadX);
        Log.d("ChatHead", "chatHeadY: " + chatHeadY);
        Log.d("ChatHead", "removeX: " + removeX);
        Log.d("ChatHead", "removeY: " + removeY);*/

        return ((chatHeadX >= boundLeft && chatHeadX <= boundRight) && chatHeadY >= boundTop);



        /*return (chatHeadY + chatHead.getHeight() >= removeY &&
                chatHeadX <= removeX + removeView.getWidth() &&
                chatHeadX + chatHead.getWidth() >= removeX);*/


    }

    private int getWidthScreen() {

        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;

    }

    private int getHeightScreen() {

        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;

    }

    private int getStatusBarHeight() {
        return (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ChatHead", "onDestroy");
        if (chatHead != null) mWindowManager.removeView(chatHead);
        if (removeView != null) mWindowManager.removeView(removeView);
    }
}