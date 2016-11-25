package com.pengdi.keyguardapplication;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Intent;

/**
 * Created by pengdi on 16-11-24.
 */
public class KeyguardApplication extends Application {
    private Activity instanceActivity;
    public KeyguardApplication() {
    }

    public void toNewActivity(Activity current, Activity activity){
        Intent intent = new Intent(current, activity.getClass());
        startActivity(intent);
        setInstanceActivity(activity);
    }

    public void setInstanceActivity(Activity activity) {
        instanceActivity = activity;
    }

    public Activity getInstanceActivity(){
        return instanceActivity;
    }

    public void toNewActivity(Service current, Activity activity){
        Intent intent = new Intent(current, activity.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        setInstanceActivity(activity);
    }
}
