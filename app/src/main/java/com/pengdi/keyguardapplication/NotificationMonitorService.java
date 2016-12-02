package com.pengdi.keyguardapplication;

import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by pengdi on 16-12-1.
 * 通知接受service类
 */
public class NotificationMonitorService extends NotificationListenerService {
    private static final String TAG = "NotificationMonitorServiceTAG";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }

    //新的Notification到达
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "open"+"-----"+sbn.toString());
    }

    //新的Notification到达，api 21新增
    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        Log.i(TAG, "open"+"-----"+sbn.toString());
    }

    //Notification被移除
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    //Notification被移除，api 21新增
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationRemoved(sbn, rankingMap);
    }

    //Notification排序变动，api 21新增
    @Override
    public void onNotificationRankingUpdate(RankingMap rankingMap) {
        super.onNotificationRankingUpdate(rankingMap);
    }

    //Service与系统通知栏完成绑定时回调，绑定后才能收到通知栏回调，api 21新增
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

}
