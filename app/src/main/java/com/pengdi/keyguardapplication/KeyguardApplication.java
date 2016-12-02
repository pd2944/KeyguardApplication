package com.pengdi.keyguardapplication;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by pengdi on 16-11-24.
 */
public class KeyguardApplication extends Application {
    private static Context context;
    private static final String NAME = "KEYGUARDAPPLICATION";
    //当前framework是否大于等于GINGERBREAD(Andrdoi 2.3)
    //因为在GINGERBREAD以前的更新sharedPreferences只能
    //调用commit函数,在GINGERBREAD及其以
    //后的版本可以调用性能更好的apply函数
    private static boolean sIsAtLeastGB;

    private LockCallback mLockCallback; // 锁屏服务

    //判断当前framework版本是否大于等于GINGERBREAD
    //Build.VERSION.SDK_INIT已int形式保存当前framework版本号
    //在Build.VERSION_CODES类里保存所知的版本号
    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sIsAtLeastGB = true;
        }
    }

    public KeyguardApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        mLockCallback = new LockService();

        // 启动锁屏服务
        startService(new Intent(this, mLockCallback.getClass()));
    }

    public static void apply(SharedPreferences.Editor editor) {
        if (sIsAtLeastGB) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public static synchronized KeyguardApplication getContext() {
        return (KeyguardApplication) context;
    }

    public static void set(String key, int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        apply(editor);
    }

    public static void set(String key, boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        apply(editor);
    }

    public static void set(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        apply(editor);
    }

    public static void set(String key, Bitmap bitmap){
        //第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray=byteArrayOutputStream.toByteArray();
        String imageString=new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
        //第三步:将String保持至SharedPreferences
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, imageString);
        editor.commit();
    }

    public static Bitmap get(String key){
        //第一步:取出字符串形式的Bitmap
        String imageString = getPreferences().getString(key, "");
        //第二步:利用Base64将字符串转换为ByteArrayInputStream
        byte[] byteArray=Base64.decode(imageString, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
        //第三步:利用ByteArrayInputStream生成Bitmap
        Bitmap bitmap= BitmapFactory.decodeStream(byteArrayInputStream);
        return bitmap;
    }

    public static boolean get(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static String get(String key, String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public static int get(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    public static long get(String key, long defValue) {
        return getPreferences().getLong(key, defValue);
    }

    public static float get(String key, float defValue) {
        return getPreferences().getFloat(key, defValue);
    }

    public static SharedPreferences getPreferences() {
        SharedPreferences pre = getContext().getSharedPreferences(NAME,
                Context.MODE_MULTI_PROCESS);
        return pre;
    }
}