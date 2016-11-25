package com.pengdi.keyguardapplication;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

/**
 * Created by pengdi on 16-11-21.
 * 锁屏的界面
 */
public class LockView extends LinearLayout implements GestureDetector.OnGestureListener {
    private LockCallback mLockCallback;
    private Context mContext;
    private GestureDetector gestureDetector;
    private LinearLayout lockViewMain;
    private LinearLayout lockViewTime;
    private static final float SCROLL_RATIO = 0.65f;// 阻尼系数
    private static final float MAX_SCROLL_HEIGHT = 300;// 最大的滑动距离
    private float nowY;
    private float alpha = 1;
    private String TAG = "LockViewTAG";

    public LockView(Context context, LockCallback lockCallback) {
        super(context);
        mContext = context;
        mLockCallback = lockCallback;
        initView(context);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    private void initView(Context context) {
        // 绑定界面
        LayoutInflater.from(context).inflate(R.layout.activity_lock, this);
        gestureDetector = new GestureDetector(this);

        lockViewTime = (LinearLayout) findViewById(R.id.lock_view_time);
        lockViewMain = (LinearLayout) findViewById(R.id.lock_view_main);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        nowY = ev.getRawY();
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                int scrollY = (int) ((nowY - event.getRawY()) * SCROLL_RATIO);
                Log.d("scrollY", String.valueOf(scrollY));
                Log.d("alphaLockViewTime", String.valueOf(alpha));
                if (lockViewTime.getScrollY() + scrollY >= 0) {
                    alpha = alpha - scrollY * 0.005f;
                    lockViewTime.scrollBy(0, scrollY);
                    // 更改透明度
                    lockViewTime.setAlpha(alpha);
                }
                nowY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if (event.getAction() == MotionEvent.ACTION_UP){
                    if (lockViewTime.getScrollY() >= MAX_SCROLL_HEIGHT * SCROLL_RATIO){
                        setAlphaAnimation(1000);
                        mLockCallback.dismiss();
                    }
                    alpha = 1;
//                downTranslation(lockViewTime, lockViewTime.getScrollY());
                    lockViewTime.scrollBy(0, -1 * lockViewTime.getScrollY());
                    lockViewTime.setAlpha(alpha);
                }
                break;
        }
        return this.gestureDetector.onTouchEvent(event);
    }

    // 渐变动画
    private void setAlphaAnimation(int duration) {
        /** 设置透明度渐变动画 */
        AlphaAnimation anim = new AlphaAnimation(0f, 0.7f);
        //设置动画持续时间
        anim.setDuration(duration);
        lockViewMain.startAnimation(anim);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

}