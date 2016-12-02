package com.pengdi.keyguardapplication;

import android.view.View;

/**
 * Created by pengdi on 16-11-28.
 * 解锁界面
 */
public interface LockScreenBase {
    /**
     * 解锁
     */
    void unLock();

    /**
     * 解锁界面的绘制
     */
    void drawView();

    /**
     * 获取界面
     */
    View getView();

    /**
     * 设置title
     */
     void setTitle(int resId);

    /**
     * 获取title
     */
    String getTitle();

    /**
     * 清楚内容
     */
    void delete();
}
