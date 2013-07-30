
package com.huanghua.mychat.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.huanghua.mychat.ChatActivity;
import com.huanghua.mychat.R;

public class BackStageService extends Service {

    private ChatService mService;

    private NotificationManager mNotificationManager;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
        }
    };

    @Override
    public void onCreate() {
        Log.i("huanghua", "onCreate");
        mService = ChatService.getInstance();
        mService.setBackStageService(this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("huanghua", "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i("huanghua", "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("huanghua", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    public void message() {
        Notification baseNF = new Notification();
        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pd = PendingIntent.getActivity(this, 0, intent, 0);

        baseNF.icon = R.drawable.notify_newmessage;

        // 通知时在状态栏显示的内容
        baseNF.tickerText = "You clicked BaseNF!";

        baseNF.defaults |= Notification.DEFAULT_SOUND;
        baseNF.defaults |= Notification.DEFAULT_VIBRATE;
        baseNF.defaults |= Notification.DEFAULT_LIGHTS;

        baseNF.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

        baseNF.flags |= Notification.FLAG_ONGOING_EVENT;

        // 第二个参数：下拉状态栏时显示的消息标题 expanded message title
        // 第三个参数：下拉状态栏时显示的消息内容 expanded message text
        // 第四个参数：点击该通知时执行页面跳转
        baseNF.setLatestEventInfo(this, "Title01", "Content01", pd);

        mNotificationManager.notify(1, baseNF);

    }
}
