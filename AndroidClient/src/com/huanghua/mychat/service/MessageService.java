
package com.huanghua.mychat.service;

import android.content.Context;
import android.content.Intent;

import com.huanghua.pojo.NewMessage;
import com.huanghua.pojo.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MessageService {

    private static HashMap<User, ArrayList<NewMessage>> mMessageBox = new HashMap<User, ArrayList<NewMessage>>();

    public static ArrayList<NewMessage> getMessageByUser(User mCurrentUser) {
        ArrayList<NewMessage> result = new ArrayList<NewMessage>();
        if (mMessageBox != null) {
            if (mMessageBox.containsKey(mCurrentUser)) {
                result = mMessageBox.get(mCurrentUser);
            }
        }
        return result;
    }

    public static void addMessage(Context context, String newmessage, User u, boolean isNew,
            User sendUser, User fromUser, User toUser) {
        if (mMessageBox.containsKey(sendUser)) {
            ArrayList<NewMessage> message = mMessageBox.get(sendUser);
            NewMessage m = new NewMessage();
            m.setMessageDate(new Date());
            m.setContext(newmessage);
            m.setUser(fromUser);
            m.setNew(isNew);
            message.add(m);
        } else {
            ArrayList<NewMessage> message = new ArrayList<NewMessage>();
            NewMessage m = new NewMessage();
            m.setMessageDate(new Date());
            m.setContext(newmessage);
            m.setUser(fromUser);
            m.setNew(isNew);
            message.add(m);
            mMessageBox.put(sendUser, message);
        }
        Intent intent = new Intent(BackStageService.CHAT_ACTION_NEW_MESSAGE);
        intent.putExtra("from_userId", fromUser.getId());
        intent.putExtra("to_userId", toUser.getId());
        intent.putExtra("userId", u.getId());
        intent.putExtra("send_userId", sendUser.getId());
        intent.putExtra("context", newmessage);
        intent.putExtra("isNew", isNew);
        context.sendBroadcast(intent);
        List<Map.Entry<User, ArrayList<NewMessage>>> lists = new ArrayList<Map.Entry<User, ArrayList<NewMessage>>>(
                mMessageBox.entrySet());
        Collections.sort(lists, new Comparator<Map.Entry<User, ArrayList<NewMessage>>>() {
            @Override
            public int compare(Entry<User, ArrayList<NewMessage>> lhs,
                    Entry<User, ArrayList<NewMessage>> rhs) {
                ArrayList<NewMessage> u1 = lhs.getValue();
                ArrayList<NewMessage> u2 = rhs.getValue();
                int u1size = u1.size();
                int u2size = u2.size();
                if (u1size == 0 && u2size == 0) {
                    return 0;
                } else if (u1size == 0 && u2size > 0) {
                    return -1;
                } else if (u1size > 0 && u2size == 0) {
                    return 1;
                } else {
                    NewMessage nm1 = u1.get(u1.size() - 1);
                    NewMessage nm2 = u2.get(u2.size() - 1);
                    Date d1 = nm1.getMessageDate();
                    Date d2 = nm2.getMessageDate();
                    return d1.compareTo(d2);
                }
            }
        });
    }

    public static HashMap<User, ArrayList<NewMessage>> getMessageBox() {
        return mMessageBox;
    }

    public static int getNewMessageByUser(User u) {
        int result = 0;
        if (mMessageBox.containsKey(u)) {
            ArrayList<NewMessage> message = mMessageBox.get(u);
            int i = 0;
            for (NewMessage nm : message) {
                if (nm.isNew()) {
                    i++;
                }
            }
            result = i;
        }

        return result;
    }

    public static String getAllNewMessage(User ignore) {
        int result = getAllNewMessage2(ignore);
        if (result == 0) {
            return "";
        } else {
            return "(" + result + ")";
        }
    }

    public static int getAllNewMessage2(User ignore) {
        int result = 0;
        Iterator<Entry<User, ArrayList<NewMessage>>> iterators = mMessageBox.entrySet().iterator();
        while (iterators.hasNext()) {
            Entry<User, ArrayList<NewMessage>> entry = iterators.next();
            User u = entry.getKey();
            if (u == ignore) {
                continue;
            }
            ArrayList<NewMessage> list = entry.getValue();
            for (NewMessage nm : list) {
                if (nm.isNew()) {
                    result++;
                }
            }
        }
        return result;
    }

    public static void setMessageReadByUser(User u) {
        ArrayList<NewMessage> lists = mMessageBox.get(u);
        if (lists != null && lists.size() > 0) {
            for (NewMessage nm : lists) {
                nm.setNew(false);
            }
        }
    }

    public static void clearAllMessage() {
        mMessageBox.clear();
    }

    public static void addMessageByDatabase(String newmessage, User u, boolean isNew,
            User sendUser, User fromUser, String date) {
        if (mMessageBox.containsKey(sendUser)) {
            ArrayList<NewMessage> message = mMessageBox.get(sendUser);
            NewMessage m = new NewMessage();
            m.setMessageDate(new Date(date));
            m.setContext(newmessage);
            m.setUser(fromUser);
            m.setNew(isNew);
            message.add(m);
        } else {
            ArrayList<NewMessage> message = new ArrayList<NewMessage>();
            NewMessage m = new NewMessage();
            m.setMessageDate(new Date(date));
            m.setContext(newmessage);
            m.setUser(fromUser);
            m.setNew(isNew);
            message.add(m);
            mMessageBox.put(sendUser, message);
        }
    }
}
