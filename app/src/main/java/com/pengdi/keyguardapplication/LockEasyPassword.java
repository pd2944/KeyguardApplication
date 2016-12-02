package com.pengdi.keyguardapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.view.Gravity.CENTER;

/**
 * Created by pengdi on 16-11-28.
 * 简单密码
 */
public class LockEasyPassword extends LinearLayout implements LockScreenBase, OnTouchListener{
    private LinearLayout linearLayout;// 主布局
    private RelativeLayout centerLinearLayout;// 中间布局
    private LinearLayout passwordLinearLayout;// 密码布局
    private TextView titleTextView; // 标题
    private LinearLayout number;// 数字布局
    private Button deleteButton;// 删除按钮
    private LinearLayout.LayoutParams layoutParams;
    private Context context;
    private String password = "";
    private final static int DELETETAG = 1001;

    private UnLockCallback mUnLockCallback;

    public LockEasyPassword(Context context, UnLockCallback unLockCallback) {
        super(context);
        this.context = context;
        mUnLockCallback = unLockCallback;
        addView(getView(), new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }

    @Override
    public void unLock() {
        mUnLockCallback.unLock(password);
    }

    @Override
    public void drawView() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        // 绘制圆形数字
        number = new LinearLayout(context);
        number.setOrientation(LinearLayout.HORIZONTAL);
        number.setGravity(CENTER);
        for (int i = 1; i <= 10; i++){
            LinearLayout.LayoutParams numberButtonLp =
                    new LinearLayout.LayoutParams(
                            width/5,
                            width/5);
            numberButtonLp.setMargins(20,20,20,20);
            TextView numberButton = new TextView(context);
            numberButton.setLayoutParams(numberButtonLp);
            numberButton.setTextSize(20);
            numberButton.setTag(i%10);
            numberButton.setText(String.valueOf(i%10));
            numberButton.setBackgroundResource(R.drawable.lock_easy_password_button);
            numberButton.setGravity(CENTER);
            numberButton.setClickable(true);
            numberButton.setOnTouchListener(this);
            number.addView(numberButton);
            if (i % 3 == 0 || i == 10){
                linearLayout.addView(number, layoutParams);
                number = new LinearLayout(context);
                number.setOrientation(LinearLayout.HORIZONTAL);
                number.setGravity(CENTER);
            }
        }
    }

    private View initView(Context context) {

        //删除按钮
        deleteButton = new Button(context);
        deleteButton.setBackgroundResource(R.drawable.pass_circular);
        deleteButton.setText("×");
        deleteButton.setTextColor(Color.WHITE);
        deleteButton.setTag(DELETETAG);
        deleteButton.setOnTouchListener(this);

        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,20,0,0);

        titleTextView = new TextView(context);
        titleTextView.setText(R.string.set_simple_password);
        titleTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(titleTextView, layoutParams);

        // 中间密码内容
        centerLinearLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams passwordLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        linearLayout.addView(centerLinearLayout,passwordLp);

        passwordLinearLayout = new LinearLayout(context);
        passwordLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        passwordLp = new RelativeLayout.LayoutParams(400, ViewGroup.LayoutParams.MATCH_PARENT);
        passwordLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        centerLinearLayout.addView(passwordLinearLayout, passwordLp);

        drawView();

        linearLayout.setBackgroundColor(Color.parseColor("#85000000"));

        return linearLayout;
    }

    @Override
    public View getView() {
        return initView(context);
    }

    // 清除
    @Override
    public void delete(){
        passwordLinearLayout.removeAllViews();
        centerLinearLayout.removeView(deleteButton);
        password = "";
    }

    // 设置title
    @Override
    public void setTitle(int resId){
        titleTextView.setText(resId);
    }

    // 获取title
    @Override
    public String getTitle(){
        return titleTextView.getText().toString();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            int buttonTag = (int) v.getTag();
            switch (buttonTag) {
                case DELETETAG:
                    delete();
                    break;
                default:
                    password = password + buttonTag;
                    Log.d("password", String.valueOf(password));
                    // 添加圆点控件
                    LinearLayout.LayoutParams layoutParams =
                            new LinearLayout.LayoutParams(35, 35);
                    TextView circularTextView = new TextView(context);
                    layoutParams.setMargins(15,0,15,0);
                    circularTextView.setBackgroundResource(R.drawable.pass_circular);
                    passwordLinearLayout.addView(circularTextView, layoutParams);
                    passwordLinearLayout.setGravity(CENTER);

                    // 添加删除按钮
                    if (deleteButton.getParent() == null) {
                        RelativeLayout.LayoutParams deleteLp = new RelativeLayout.LayoutParams(100, 100);
                        deleteLp.setMarginEnd(100);
                        deleteLp.addRule(RelativeLayout.ALIGN_PARENT_END);
                        deleteLp.addRule(RelativeLayout.CENTER_VERTICAL);
                        passwordLinearLayout.setGravity(CENTER);
                        centerLinearLayout.addView(deleteButton, deleteLp);
                    }

                    // 如果字符串大于等于4个则完成
                    if (password.length() >= 4){
                        unLock();
                    }
                    break;
            }
        }
        return false;
    }
}
