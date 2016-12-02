package com.pengdi.keyguardapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{
    private static final int SAVE_PICTURE = 1000;
    private KeyguardApplication application;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application = (KeyguardApplication) getApplicationContext();

        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage(application.getString(R.string.set_wallpaper_wite));
        pd.setCancelable(false); // 设置ProgressDialog 是否可以按退回键取消
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_float_window:
                getAppDetailSettingIntent();
                break;
            case R.id.close_system_keyguard:
                toSecuritySettings();
                break;
            case R.id.set_num_password:
                Intent intent = new Intent(this, SetEasyPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.set_picture:
                openAlbum();
                break;
            case R.id.permission_notification:
                if (!isNotificationListenEnabled())
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                else
                    Toast.makeText(getApplicationContext(), "已开启通知权限", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    // 跳转到系统设置界面
    private void getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }

    // 跳转到系统安全界面
    private void toSecuritySettings(){
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        startActivity(intent);
    }

    // 打开相册
    public void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 0001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0001 && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData(); //返回的是uri
            String [] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null,
                    null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);

            pd.show();
            new PictureThread(path).start();

        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1){
                case SAVE_PICTURE:
                    pd.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 用于储存锁屏壁纸
     */
    class PictureThread extends Thread{
        String path;

        public PictureThread(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            Message message = new Message();
            WindowManager wm = MainActivity.this.getWindowManager();

            // 获取屏幕宽度和高度对图片进行压缩
            Bitmap bitmap = PhotoUtils.getSmallBitmap(path,
                    wm.getDefaultDisplay().getWidth(),
                    wm.getDefaultDisplay().getHeight());

            message.arg1 = SAVE_PICTURE;
            application.set(Constant.PRE_WALLPAPER, bitmap);
            handler.sendMessage(message);
        }
    }

    // 检查是否打开通知监听
    private boolean isNotificationListenEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
