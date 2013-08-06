
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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.huanghua.mychat.ChatActivity;
import com.huanghua.mychat.Login;
import com.huanghua.mychat.R;
import com.huanghua.mychat.util.Util;
import com.huanghua.pojo.User;
import com.huanghua.provider.DatabaseHelper;
import com.huanghua.provider.MyChatTable;

import java.util.List;

public class BackStageService extends Service {

    public static final String CHAT_ACTION_NEW_MESSAGE = "chat_action_new_mesage";
    public static final String CHAT_ACTION_REMOVE_NOTIFY = "chat_action_remove_notify";
    public static final String CHAT_ACTION_READ_MESSAGE = "chat_action_read_mesage";
    public static final String CHAT_ACTION_SETREAD_MESSAGE = "chat_action_setread_mesage";
    private ChatService mService;
    private DatabaseHelper mDatabaseHelper;

    private NotificationManager mNotificationManager;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Util.ChatLog("action:" + action);
            if (action != null && CHAT_ACTION_REMOVE_NOTIFY.equals(action)) {
                int id = intent.getIntExtra("id", 1);
                removeNotification(id);
            } else if (action != null && CHAT_ACTION_NEW_MESSAGE.equals(action)) {
                String userId = intent.getStringExtra("userId");
                String from_userId = intent.getStringExtra("from_userId");
                String to_userId = intent.getStringExtra("to_userId");
                String send_userId = intent.getStringExtra("send_userId");
                String message = intent.getStringExtra("context");
                boolean isNew = intent.getBooleanExtra("isNew", false);
                ContentValues values = new ContentValues();
                values.put(MyChatTable.MessageColumns.userId, userId);
                values.put(MyChatTable.MessageColumns.send_userId, send_userId);
                values.put(MyChatTable.MessageColumns.from_userId, from_userId);
                values.put(MyChatTable.MessageColumns.to_userId, to_userId);
                values.put(MyChatTable.MessageColumns.messageDate, System.currentTimeMillis());
                values.put(MyChatTable.MessageColumns.context, message);
                values.put(MyChatTable.MessageColumns.isNew, !isNew);
                String sql = "insert into " + MyChatTable.MessageColumns.TABLE_NAME
                        + "(" + MyChatTable.MessageColumns.userId + ","
                        + MyChatTable.MessageColumns.send_userId + ","
                        + MyChatTable.MessageColumns.from_userId + ","
                        + MyChatTable.MessageColumns.to_userId + ","
                        + MyChatTable.MessageColumns.messageDate + ","
                        + MyChatTable.MessageColumns.context + ","
                        + MyChatTable.MessageColumns.isNew + ")"
                        + " values('" + userId + "','"
                        + send_userId + "','"
                        + from_userId + "','"
                        + to_userId + "', datetime('now') ,'"
                        + message + "', 0);";
                insertToDatabase(sql, values);
            } else if (action != null && CHAT_ACTION_READ_MESSAGE.equals(action)) {
                queryMessage();
            } else if (action != null && CHAT_ACTION_SETREAD_MESSAGE.equals(action)) {
                String send_userId = intent.getStringExtra("send_userId");
                String userId = intent.getStringExtra("userId");
                updateMessageRead(userId, send_userId);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mService = ChatService.getInstance();
        mService.setBackStageService(this);
        mDatabaseHelper = new DatabaseHelper(this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(CHAT_ACTION_NEW_MESSAGE);
        mFilter.addAction(CHAT_ACTION_REMOVE_NOTIFY);
        mFilter.addAction(CHAT_ACTION_READ_MESSAGE);
        mFilter.addAction(CHAT_ACTION_SETREAD_MESSAGE);
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

    private void insertToDatabase(String sql, ContentValues values) {
        SQLiteDatabase sdb = mDatabaseHelper.getWritableDatabase();
        sdb.beginTransaction();
        Util.ChatLog("sql:" + sql);
        sdb.insert(MyChatTable.MessageColumns.TABLE_NAME, null,
                values);
        // sdb.execSQL(sql);
        sdb.setTransactionSuccessful();
        sdb.endTransaction();
        sdb.close();
    }

    private void queryMessage() {
        String[] COLUMNS = new String[] {
                MyChatTable.MessageColumns.userId, MyChatTable.MessageColumns.send_userId,
                MyChatTable.MessageColumns.from_userId, MyChatTable.MessageColumns.to_userId,
                MyChatTable.MessageColumns.messageDate, MyChatTable.MessageColumns.context,
                MyChatTable.MessageColumns.isNew
        };
        SQLiteDatabase sdb = mDatabaseHelper.getReadableDatabase();
        sdb.beginTransaction();
        Cursor c = sdb.query(MyChatTable.MessageColumns.TABLE_NAME, COLUMNS,
                MyChatTable.MessageColumns.userId + "=" + mService.getMySelf().getId(),
                null,
                null,
                null,
                MyChatTable.MessageColumns.messageDate);
        sdb.setTransactionSuccessful();
        MessageService.clearAllMessage();
        while (c.moveToNext()) {
            String send_userId = c.getString(c
                    .getColumnIndex(MyChatTable.MessageColumns.send_userId));
            String from_userId = c.getString(c
                    .getColumnIndex(MyChatTable.MessageColumns.from_userId));
            long messageDate = c.getLong(c
                    .getColumnIndex(MyChatTable.MessageColumns.messageDate));
            String context = c.getString(c.getColumnIndex(MyChatTable.MessageColumns.context));
            int isNew = c.getInt(c.getColumnIndex(MyChatTable.MessageColumns.isNew));
            User fromUser = from_userId.equals(mService.getMySelf().getId()) ? mService.getMySelf()
                    : mService.getUserById(from_userId);
            MessageService.addMessageByDatabase(context, mService.getMySelf(), isNew == 0 ? true
                    : false,
                    mService.getUserById(send_userId), fromUser, messageDate);
        }
        c.close();
        sdb.endTransaction();
        sdb.close();
        mService.refreshMessageList();
    }

    private void updateMessageRead(String userId, String send_userId) {
        String sql = "update " + MyChatTable.MessageColumns.TABLE_NAME + " set "
                + MyChatTable.MessageColumns.isNew + " =1"
                + " where " + MyChatTable.MessageColumns.userId + "=" + userId
                + " and " + MyChatTable.MessageColumns.send_userId + "=" + send_userId;
        SQLiteDatabase sdb = mDatabaseHelper.getReadableDatabase();
        sdb.beginTransaction();
        sdb.execSQL(sql);
        sdb.setTransactionSuccessful();
        sdb.endTransaction();
        sdb.close();
    }
}
