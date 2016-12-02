package com.pengdi.keyguardapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by pengdi on 16-11-28.
 * 设置简单密码界面
 */
public class SetEasyPasswordActivity extends Activity implements UnLockCallback {
    private LockEasyPassword lockEasyPassword; // 密码布局
    private KeyguardApplication application;
    private LinearLayout linearLayout; // 主布局
    private LinearLayout.LayoutParams chileLayoutParams;
    private LinearLayout.LayoutParams mainLayoutParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (KeyguardApplication) getApplication();
        chileLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        lockEasyPassword = new LockEasyPassword(this,this);
        linearLayout.addView(lockEasyPassword,chileLayoutParams);

        mainLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setContentView(linearLayout,mainLayoutParams);
    }

    @Override
    public void unLock(String password) {
        if (lockEasyPassword.getTitle().equals(application.getString(R.string.set_simple_password))){
            // 保存密码
            application.set(Constant.PRE_STR, password);
            // 重新添加密码布局
            linearLayout.removeAllViews();
            lockEasyPassword = new LockEasyPassword(this,this);
            linearLayout.addView(lockEasyPassword,chileLayoutParams);
            // 改变title
            lockEasyPassword.setTitle(R.string.centain_easy_password);
        }else if (lockEasyPassword.getTitle().equals(application.getString(R.string.centain_easy_password))){
            if (password.equals(application.get(Constant.PRE_STR,"null"))){
                application.set(Constant.PASSWORDTYPE, Constant.EASYPASSWORDTYPE);
                finish();
                Toast.makeText(getApplicationContext(), "set ok", Toast.LENGTH_SHORT).show();
            }else{
                Log.d("SetEasyPasswordActivity", "input error");
                lockEasyPassword.delete();
                Toast.makeText(getApplicationContext(), "set error", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
