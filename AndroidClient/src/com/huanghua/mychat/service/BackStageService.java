
package com.huanghua.mychat.service;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.huanghua.mychat.ChatActivity;
import com.huanghua.mychat.Login;
import com.huanghua.mychat.R;
import com.huanghua.mychat.util.Util;
import com.huanghua.pojo.User;

import java.util.List;

public class BackStageService extends Service {

    public static final String CHAT_ACTION_NEW_MESSAGE = "chat_action_new_mesage";
    public static final String CHAT_ACTION_REMOVE_NOTIFY = "chat_action_remove_notify";
    private ChatService mService;

    private NotificationManager mNotificationManager;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Util.ChatLog("action:" + action);
            if (action != null && CHAT_ACTION_REMOVE_NOTIFY.equals(action)) {
                int id = intent.getIntExtra("id", 1);
                removeNotification(id);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mService = ChatService.getInstance();
        mService.setBackStageService(this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(CHAT_ACTION_NEW_MESSAGE);
        mFilter.addAction(CHAT_ACTION_REMOVE_NOTIFY);
        registerReceiver(mBroadcastReceiver, mFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        Intent intent = new Intent(this, BackStageService.class);
        startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressWarnings("deprecation")
    public void message(String msg, User u) {
        Util.ChatLog("isBack:" + isBackgroundRunning());
        if (!isBackgroundRunning()) {
            return;
        }
        Notification baseNF = new Notification();
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), ChatActivity.class.getName());
        intent.putExtra("userId", u.getId());
        PendingIntent pd = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        baseNF.icon = R.drawable.notify_newmessage;

        baseNF.tickerText = u.getName() + ": " + msg;

        baseNF.defaults |= Notification.DEFAULT_SOUND;
        baseNF.defaults |= Notification.DEFAULT_VIBRATE;
        baseNF.defaults |= Notification.DEFAULT_LIGHTS;

        baseNF.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        baseNF.flags |= Notification.FLAG_ONGOING_EVENT;
        baseNF.flags |= Notification.FLAG_AUTO_CANCEL;

        baseNF.setLatestEventInfo(getApplicationContext(), u.getName(), msg, pd);

        mNotificationManager.notify(1, baseNF);
    }

    @SuppressWarnings("deprecation")
    public void forceOffLineNotify() {
        if (!isBackgroundRunning()) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(this, Login.class);
            startActivity(intent);
        }
        removeNotification(1);
        Notification baseNF = new Notification();
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), Login.class.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pd = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        baseNF.icon = R.drawable.notify;

        baseNF.tickerText = getString(R.string.offlineWarn);

        baseNF.defaults |= Notification.DEFAULT_SOUND;
        baseNF.defaults |= Notification.DEFAULT_VIBRATE;
        baseNF.defaults |= Notification.DEFAULT_LIGHTS;

        baseNF.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        baseNF.flags |= Notification.FLAG_ONGOING_EVENT;
        baseNF.flags |= Notification.FLAG_AUTO_CANCEL;

        baseNF.setLatestEventInfo(getApplicationContext(), getString(R.string.offlineNotification),
                getString(R.string.offlineWarn), pd);

        mNotificationManager.notify(2, baseNF);
    }

    private boolean isBackgroundRunning() {
        String processName = getPackageName();

        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> processList = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processList) {
            if (process.processName.startsWith(processName)) {
                boolean isBackground = process.importance != IMPORTANCE_FOREGROUND
                        && process.importance != IMPORTANCE_VISIBLE;
                boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                if (isBackground || isLockedState) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private void removeNotification(int noticicationId) {
        mNotificationManager.cancel(noticicationId);
    }
}
