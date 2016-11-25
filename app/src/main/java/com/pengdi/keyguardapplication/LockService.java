package com.pengdi.keyguardapplication;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

public class LockService extends Service implements LockCallback {
    private static final String TAG = "LockServiceLOG";

    private KeyguardApplication mApplication;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private LockView mLockView;

    @Override
    public void onCreate() {
        mApplication = (KeyguardApplication) getApplication();
        // 设置窗口管理器，制定悬浮窗
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        // 设置窗体显示类型——TYPE_SYSTEM_ALERT(系统提示)
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        // 设置宽度为全屏
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        // 设置高度为全屏
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        // 设置窗口的偏移量
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
        // 设置窗口透明
        mLayoutParams.format = PixelFormat.TRANSLUCENT;

        mLockView = new LockView(getApplicationContext(), LockService.this);

        // 获取壁纸管理器
        WallpaperManager wallpaperManager = WallpaperManager
                .getInstance(this);
        // 获取当前壁纸
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        // 将Drawable,转成Bitmap
        Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();

        mLockView.setBackground(new BitmapDrawable(bm));

        super.onCreate();
        // 屏蔽系统的锁屏
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("KeyguardLock");
        keyguardLock.disableKeyguard();

        // 注册监听屏幕开启和关闭的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, intentFilter);
    }

    public LockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * 用于监听屏幕启动和关闭的广播
     * 判断Action是否屏幕启动或关闭
     * 在窗口管理器中添加锁屏悬浮窗
     * */
    BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "mScreenOffReceiver-->" + intent.getAction());
            if (/*intent.getAction().equals("android.intent.action.SCREEN_ON") ||*/
                    intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                if (mLockView.getParent() == null) {
                    mWindowManager.addView(mLockView, mLayoutParams);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        unregisterReceiver(screenReceiver);
    }

    @Override
    public void dismiss() {
        Log.d(TAG,"LockCallback!!");
        if (mLockView.getParent() != null) {
            mWindowManager.removeView(mLockView);
        }
    }
}
