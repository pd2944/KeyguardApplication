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
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class LockService extends Service implements LockCallback, UnLockCallback {
    private static final String TAG = "LockServiceLOG";

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private LinearLayout mainLayout;// 主布局
    private LockView mLockView;

    private int PasswordType;
    private KeyguardApplication application;

    private LockScreenBase mLockScreenBase;
    private Vibrator mVibrator; //震动
    @Override
    public void onCreate() {
        application = (KeyguardApplication) getApplicationContext();

        mVibrator = (Vibrator) application.getSystemService(Service.VIBRATOR_SERVICE);


        // 设置窗口管理器，制定悬浮窗
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        // 设置窗体显示类型——TYPE_SYSTEM_ALERT(系统提示)　TYPE_SYSTEM_ERROR
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
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

        mainLayout = new LinearLayout(getApplicationContext());

        mLockView = new LockView(getApplicationContext(), LockService.this);

        mainLayout.setBackground(new BitmapDrawable(getKeyguardWallpaper()));

        super.onCreate();
        // 屏蔽系统的锁屏
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("KeyguardLock");
        keyguardLock.disableKeyguard();

        // 注册监听屏幕开启和关闭的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        registerReceiver(screenReceiver, intentFilter);
    }

    public LockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Bitmap getKeyguardWallpaper(){
        Bitmap bm = application.get(Constant.PRE_WALLPAPER);
        if (bm == null) {
            // 获取壁纸管理器
            WallpaperManager wallpaperManager = WallpaperManager
                    .getInstance(this);
            // 获取当前壁纸
            Drawable wallpaperDrawable = wallpaperManager.getDrawable();
            // 将Drawable,转成Bitmap
            bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
        }
        return bm;
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
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
                Intent bootIntent = new Intent(context,LockService.class);
                context.startService(bootIntent);
            }
            if (/*intent.getAction().equals("android.intent.action.SCREEN_ON") ||*/
                    intent.getAction().equals("android.intent.action.SCREEN_OFF") ||
                    intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                mWindowManager.removeView(mainLayout);
                mainLayout.removeAllViews();
                if (mainLayout.getParent() == null) {
                    mainLayout.setBackground(new BitmapDrawable(getKeyguardWallpaper()));
                    mLockView = new LockView(getApplicationContext(), LockService.this);
                    mainLayout.addView(mLockView, mLayoutParams);
                    mWindowManager.addView(mainLayout, mLayoutParams);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        unregisterReceiver(screenReceiver);
        startService(new Intent(this,LockService.class));
    }

    @Override
    public void dismiss() {
        Log.d(TAG,"LockCallback!!");
        if (mainLayout.getParent() != null) {
            mainLayout.removeAllViews();
        }

        // 获取密码类型
        PasswordType = application.get(Constant.PASSWORDTYPE, 0);

        // 查看解锁类型
        switch (PasswordType){
            case Constant.EASYPASSWORDTYPE:
                mLockScreenBase = new LockEasyPassword(getApplicationContext(), this);
                mainLayout.addView((View) mLockScreenBase, mLayoutParams);
                mLockScreenBase.unLock();
                mLockScreenBase.setTitle(R.string.input_password);
                break;
            default:
                mWindowManager.removeView(mainLayout);
                break;
        }
    }

    @Override
    public void unLock(String password) {
        if (password.equals(application.get(Constant.PRE_STR,""))){
            mainLayout.removeAllViews();
            mWindowManager.removeView(mainLayout);
        }else if (!password.equals("")){
            mVibrator.vibrate(100);
            mLockScreenBase.delete();
            mLockScreenBase.setTitle(R.string.password_try_again);
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
}
